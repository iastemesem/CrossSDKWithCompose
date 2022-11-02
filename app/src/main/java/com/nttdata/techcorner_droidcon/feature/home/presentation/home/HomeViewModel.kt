package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ambient.crossdevice.sessions.*
import com.google.ambient.crossdevice.wakeup.StartComponentRequest
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie
import com.nttdata.techcorner_droidcon.feature.home.domain.use_case.GetMovies
import com.nttdata.techcorner_droidcon.feature.home.presentation.model.MovieData
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets.UTF_8

class HomeViewModel constructor(
    private val getMoviesUseCase: GetMovies,
    private val context: Context
) : ViewModel(), MovieData {
    private val _movies = mutableStateListOf<Movie>()
    private var activePrimarySession: PrimarySession? = null
    private var sessions: Sessions = Sessions.create(context = context)

    companion object {
        private const val KEY_LOCAL_PLAYER_NAME = "LOCAL_PLAYER_NAME"
        private const val KEY_OPPONENT_PLAYER_NAME = "OPPONENT_PLAYER_NAME"
        private const val KEY_LOCAL_PLAYER_SCORE = "LOCAL_PLAYER_SCORE"
        private const val KEY_OPPONENT_PLAYER_SCORE = "OPPONENT_PLAYER_SCORE"
        private const val KEY_ROUNDS_COMPLETED = "ROUNDS_COMPLETED"
        private const val TAG = "TECH_CORNER"
        const val SHARE_ACTION = "movies_share_action"
    }

    enum class HomeState {
        LOADING,
        LOADED,
        ERROR
    }

    init {
        sessions.registerActivityResultCaller(context as ActivityResultCaller)
    }


    private var localMovieChoice: String = "1234567890"
    private var remoteMovieChoice: String = ""
    private var localMovieChoiceConfirmed: Boolean = false
    private var movieState = MutableLiveData(MovieData.MovieState.DISCONNECTED)
    var homeState = MutableLiveData(HomeState.LOADING)
    private var _movieCartList = mutableListOf<Int>()
    var cartCounter = MutableLiveData<String>("0")

    val movies: MutableLiveData<List<Movie>>
        get() = MutableLiveData<List<Movie>>(_movies)

    fun getMovies() {
        viewModelScope.launch {
            try {
                val movies = getMoviesUseCase()
                _movies.addAll(movies.shuffled())
                homeState.postValue(HomeState.LOADED)
            } catch (e: Exception) {

            }
        }
    }

    fun setRemoteMovieChoice(movieId: String) {
        localMovieChoice = movieId
    }

    fun setLocalMovieChoice(movieId: String) {
        remoteMovieChoice = movieId
    }

    fun setLocalMovieChoiceConfirmed(confirmed: Boolean) {
        localMovieChoiceConfirmed = confirmed
    }

    fun onAddClicked(movieId: Int) {
        _movieCartList.add(movieId);
        cartCounter.value = _movieCartList.size.toString()
        viewModelScope.launch {
            activePrimarySession?.getSecondaryRemoteConnections()?.get(0)?.send(
                "A $movieId".toString().toByteArray(
                    UTF_8
                )
            )
        }
    }

    fun onRemoveClicked(movieId: Int) {
        val removed = _movieCartList.remove(movieId)
        cartCounter.value = _movieCartList.size.toString()
        if (removed)
            viewModelScope.launch {
                activePrimarySession?.getSecondaryRemoteConnections()?.get(0)?.send(
                    "R $movieId".toString().toByteArray(
                        UTF_8
                    )
                )
            }
    }

    fun findOtherDevices() {
        val sessionId = sessions.createSession(ApplicationSessionTag("movies_share"))
        viewModelScope.launch {
            activePrimarySession = sessions.shareSession(
                sessionId = sessionId,
                StartComponentRequest.Builder()
                    .setAction(SHARE_ACTION)
                    .setReason("Seleziona il device con il quale vuoi condividere la sessione")
                    .build(),
                emptyList(),
                MovieSharedSessionStateCallback()
            )
        }
    }

    fun handleIntent(intent: Intent) {
        viewModelScope.launch {
            val secondarySession =
                sessions.getSecondarySession(intent, MoviesSecondaryShareSessionStateCallback())
            val remoteConnection = secondarySession.getDefaultRemoteConnection()

            remoteConnection.registerReceiver(
                object : SessionConnectionReceiver {
                    override fun onMessageReceived(
                        participant: SessionParticipant,
                        payload: ByteArray
                    ) {
                        val payloadReceived = String(payload)
                        Log.d(TAG, "Payload received: $payloadReceived")
                        val movieIdFromPayload = payloadReceived.removeRange(0, 2)
                        if (payloadReceived.startsWith("A")) {
                            _movieCartList.add(movieIdFromPayload.toInt())
                        } else if (payloadReceived.startsWith("R")) {
                            _movieCartList.remove(movieIdFromPayload.toInt())
                        }

                        cartCounter.value = _movieCartList.size.toString()
                    }
                }
            )
        }

    }

    override fun getPrimarySessionName(): MutableLiveData<String> {
        return MutableLiveData("Primary Session")
    }

    override fun getSecondarySessionName(): MutableLiveData<String> {
        return MutableLiveData("Secondary Session")
    }

    override fun getMovieState(): MutableLiveData<MovieData.MovieState> {
        return movieState
    }

    override fun getLocalMovieChoice(): String {
        return localMovieChoice
    }

    override fun getRemoteMovieChoiche(): String {
        return remoteMovieChoice
    }

    private inner class MovieSharedSessionStateCallback : PrimarySessionStateCallback {
        override fun onParticipantDeparted(sessionId: SessionId, participant: SessionParticipant) {
            //custom leave logic
            Log.d(TAG, "onParticipantDeparted: ")
        }

        override fun onParticipantJoined(sessionId: SessionId, participant: SessionParticipant) {
            viewModelScope.launch {
                val connection =
                    checkNotNull(activePrimarySession).getSecondaryRemoteConnectionForParticipant(
                        participant
                    )
                _movieCartList.map { cart ->
                    connection.send("A $cart".toByteArray(UTF_8))
                }
                connection.registerReceiver(
                    object : SessionConnectionReceiver {
                        override fun onMessageReceived(
                            participant: SessionParticipant,
                            payload: ByteArray
                        ) {
                            val ok = payload.contentEquals("ok".toByteArray(UTF_8))
                            Log.d(TAG, "Session share initialized. ok=$ok: ")

                            viewModelScope.launch {
                                checkNotNull(activePrimarySession).broadcastToSecondaries(
                                    "Movies shared".toByteArray(
                                        UTF_8
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

        override fun onPrimarySessionCleanup(sessionId: SessionId) {
            //custom cleanup logic here
            Log.d(TAG, "onPrimarySessionCleanup: ")
        }

        override fun onShareFailureWithParticipant(
            sessionId: SessionId,
            exception: SessionException,
            participant: SessionParticipant
        ) {
            //handle error
            Log.d(TAG, "onShareFailureWithParticipant: ")
        }

        override fun onShareInitiated(sessionId: SessionId, numPotentialParticipants: Int) {
            // Custom logic here for when n devices can potentially join.
            // e.g. if there were 0, cancel/error if desired,
            //      if non-0 maybe spin until numPotentialParticipants join etc.
            Log.d(TAG, "onShareInitiated: ")
        }
    }

    private inner class MoviesSecondaryShareSessionStateCallback : SecondarySessionStateCallback {
        override fun onSecondarySessionCleanup(sessionId: SessionId) {
            //custom cleanup logic here.
            Log.d(TAG, "onSecondarySessionCleanup: ")
        }
    }
}
