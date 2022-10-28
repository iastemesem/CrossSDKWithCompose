package com.nttdata.techcorner_droidcon.feature.home.presentation.model

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.core.content.ContextCompat
import com.google.ambient.crossdevice.discovery.DeviceFilter
import com.google.ambient.crossdevice.sessions.*
import com.google.ambient.crossdevice.wakeup.StartComponentRequest
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.nttdata.techcorner_droidcon.feature.home.presentation.home.HomeViewModel
import com.nttdata.techcorner_droidcon.feature.home.presentation.service.Callback
import com.nttdata.techcorner_droidcon.feature.home.presentation.service.MovieManager
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.Executor


class SessionTwoUsersMovieManager(context: Context) : MovieManager {
    companion object {
        const val _tag = "SessionManager"
        const val _action = "com.nttdata.techcorner_droidcon.SESSIONS_TWO_USERS_WAKEUP"
    }

    private final val primarySessionStateCallback = object : PrimarySessionStateCallback {
        override fun onParticipantDeparted(sessionId: SessionId, participant: SessionParticipant) {
            /* The PrimarySession will only be destroyed if done explicitly. Since the only
             * participant has departed, the PrimarySession should now be destroyed. */
            Log.d(_tag, "SessionParticipant departed: " + participant.displayName);

        }

        override fun onParticipantJoined(sessionId: SessionId, participant: SessionParticipant) {
            Log.d(_tag, "New Participant joined: " + participant.displayName);
            getMovieData().getSecondarySessionName().value = participant.displayName.toString()
            if (primarySession == null) {
                Log.d(
                    _tag,
                    "Cannot add callback to joined participant since PrimarySession is null"
                );
                return;
            }

            addRemoteConnectionCallback(primarySession!!, participant)
        }

        override fun onPrimarySessionCleanup(sessionId: SessionId) {
            Log.d(_tag, "PrimarySession cleanup");
            primarySession = null;
        }

        override fun onShareFailureWithParticipant(
            sessionId: SessionId,
            exception: SessionException,
            participant: SessionParticipant
        ) {
            Log.e(_tag, "Share failure with participant: " + participant.displayName, exception);
        }

        override fun onShareInitiated(sessionId: SessionId, numPotentialParticipants: Int) {
            if (numPotentialParticipants == 0) {
                Log.d(_tag, "No participants joining Session, destroying PrimarySession");
                destroyPrimarySession();
            }
        }

        /** Add a callback to SessionParticipant for handling received messages  */
        private fun addRemoteConnectionCallback(
            session: PrimarySession, participant: SessionParticipant
        ) {
            session
                .getSecondaryRemoteConnectionForParticipant(participant)
                .registerReceiver(object : SessionConnectionReceiver {
                    override fun onMessageReceived(
                        participant: SessionParticipant,
                        payload: ByteArray
                    ) {
                        handleMessageReceived(payload)
                    }
                })
        }
    }

    private final val secondarySessionStateCallback = object : SecondarySessionStateCallback {
        override fun onSecondarySessionCleanup(sessionId: SessionId) {
            secondarySession = null
            secondaryConnection = null
        }

    }


    private var context: Context = context
    private var mainExecutor: Executor
    private var movieData: HomeViewModel? = null
    private var sessions: Sessions? = null

    private var primarySession: PrimarySession? = null
    private var secondarySession: SecondarySession? = null
    private var secondaryConnection: SessionRemoteConnection? = null

    init {
        sessions = Sessions.create(context)
        mainExecutor = ContextCompat.getMainExecutor(context)
        sessions!!.registerActivityResultCaller(context as ActivityResultCaller)
    }

    /**
     *Getter for managed HomeViewModel object
     */
    override fun getMovieData(): MovieData {
        return this.movieData!!
    }

    override fun disconnect() {
        closeConnections()
    }

    override fun findOtherDevices() {
        //movieData!!.getMovieState().value = MovieData.MovieState.SEARCHING

        val sessionId = sessions!!.createSession(null)
        Futures.addCallback(
            sessions!!.shareSessionFuture(
                sessionId,
                StartComponentRequest.Builder().setAction(_action).setReason("Scegli il dispositivo con il quale condividere la tua sessione").build(),
                listOf(/*DeviceFilter.trustRelationshipFilter(DeviceFilter.TrustRelationshipType.MY_DEVICES_ONLY)*/),
                primarySessionStateCallback
            ),
            object : FutureCallback<PrimarySession> {
                override fun onSuccess(result: PrimarySession?) {
                    Log.d(_tag, "Successfully launched opponent picker")
                    primarySession = result
                }

                override fun onFailure(t: Throwable) {
                    Log.e(_tag, "Failed to launch opponent picker: ", t)
                }
            }, mainExecutor
        )
    }

    override fun sendMovieSelected(movieId: String, callback: Callback) {
        movieData!!.setLocalMovieChoice(movieId)
        movieData!!.getMovieState().value = MovieData.MovieState.WAITING
        broadcastMovieChoice(callback)
    }

    override fun finishSession() {

    }

    override fun acceptSessionInvitation(intent: Intent) {
        getSecondarySessionAndRemoteConnection(intent)
    }

    override fun isHost(): Boolean {
        return primarySession != null
    }

    /**
     * Gets the SecondarySession and uses it to get the RemoteConnection.
     */
    private fun
            getSecondarySessionAndRemoteConnection(intent: Intent) {
        Futures.addCallback(
            sessions!!.getSecondarySessionFuture(intent, secondarySessionStateCallback),
            object : FutureCallback<SecondarySession> {
                override fun onSuccess(result: SecondarySession) {
                    Log.d(_tag, "Succeded to get SecondarySession ")
                    updateSecondarySession(result)
                    getRemoteConnectionAndRegisterReceiver(result)
                }

                override fun onFailure(t: Throwable) {
                    Log.e(_tag, "onFailure: ", t)
                }
            },
            mainExecutor
        )
    }

    /**
     * Gets the RemoteConnection and registers a message receiver to it.
     */
    private fun getRemoteConnectionAndRegisterReceiver(secondarySession: SecondarySession) {
        secondaryConnection = secondarySession.getDefaultRemoteConnection()
        secondaryConnection!!.registerReceiver(object : SessionConnectionReceiver {
            override fun onMessageReceived(participant: SessionParticipant, payload: ByteArray) {
                handleMessageReceived(payload)
            }
        })

        movieData!!.getMovieState().value = MovieData.MovieState.WAITING
        movieData!!.getSecondarySessionName().value =
            secondaryConnection!!.participant.displayName.toString()
    }

    /**
     * Sends the game choice to whichever connection is open.
     */
    private fun broadcastMovieChoice(callback: Callback) {
        Log.i(_tag, "sending game choice")
        val sendMessageFuture: ListenableFuture<Void?>? = getSendMessageFuture()

        if (sendMessageFuture == null) {
            Log.w(_tag, "There are no open connection to send a message to")
            return
        }

        Futures.addCallback(
            sendMessageFuture,
            object : FutureCallback<Void?> {
                override fun onSuccess(result: Void?) {
                    Log.i(_tag, "Successfully sent movie choice")
                    movieData!!.setLocalMovieChoiceConfirmed(true)
                    finishSession()
                    callback.onSuccess()
                }

                override fun onFailure(t: Throwable) {
                    Log.e(_tag, "Failed to send moviw choice", t)
                    callback.onFailure()
                }
            }, mainExecutor
        )
    }

    private fun getSendMessageFuture(): ListenableFuture<Void?>? {
        val message = movieData!!.getLocalMovieChoice().encodeToByteArray()
        if (primarySession != null) {
            return primarySession!!.broadcastToSecondariesFuture(message)
        } else if (secondaryConnection != null) {
            return secondaryConnection!!.sendFuture(message)
        }

        return null
    }

    /**
     * Destroys a SecondarySession.
     */
    private fun destroySecondarySession(
        secondarySession: SecondarySession,
        callback: FutureCallback<Void?>
    ) {
        Futures.addCallback(
            secondarySession.destroySecondarySessionFuture(),
            callback,
            mainExecutor
        )
    }

    /**
     * Destroys a PrimarySession.
     */
    private fun destroyPrimarySession() {
        Futures.addCallback(
            primarySession!!.destroyPrimarySessionAndStopSharingFuture(),
            object : FutureCallback<Void?> {
                override fun onSuccess(result: Void?) {
                    Log.i(_tag, "Destroyed primary session handle")
                }

                override fun onFailure(t: Throwable) {
                    Log.e(_tag, "Failed to destroy primary session handle", t)
                }
            },
            mainExecutor
        )
    }

    /**
     * Disconnects from any previous SecondarySession and sets the current to the provided
     * SecondarySession.
     */
    private fun updateSecondarySession(secondarySession: SecondarySession) {
        // Close any existing connection
        if (this.secondarySession != null) {
            destroySecondarySession(
                this.secondarySession!!,
                object : FutureCallback<Void?> {
                    override fun onSuccess(result: Void?) {
                        Log.i(_tag, "Successfully disconnected from the previous SecondarySession");
                    }

                    override fun onFailure(t: Throwable) {
                        Log.e(
                            _tag,
                            "Error destroying previous secondary session, which is now orphaned",
                            t
                        );
                    }
                })
        }
        this.secondarySession = secondarySession
    }

    /**
     * Closes any open PrimarySession or SecondarySession.
     */
    private fun closeConnections() {
        if (primarySession != null) {
            destroyPrimarySession()
        }
        if (secondarySession != null) {
            destroySecondarySession(
                secondarySession!!,
                object : FutureCallback<Void?> {
                    override fun onSuccess(result: Void?) {
                        Log.i(_tag, "Successfully destroyed SecondarySession")
                    }

                    override fun onFailure(t: Throwable?) {
                        Log.e(_tag, "Failed to destroy secondary session handle", t)
                    }
                })
        }
    }

    /**
     * Sets the opponent's choice and attempts to finish the round
     */
    private fun handleMessageReceived(message: ByteArray) {
        movieData!!.setRemoteMovieChoice(String(message, UTF_8))
    }
}