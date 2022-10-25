package com.nttdata.techcorner_droidcon.feature.home.presentation.service

import android.content.Intent
import com.nttdata.techcorner_droidcon.feature.home.presentation.model.MovieData

interface MovieManager {
    /**
     * Interface for defining MovieData object
     */
    fun getMovieData(): MovieData

    /**
     * Disconnect from connection
     */
    fun disconnect()

    /**
     * Initialize discovery of other device
     */
    fun findOtherDevices()

    /**
     * sends the local film selection to a remote partecipant
     */
    fun sendMovieSelected(movieId:String, callback: Callback)

    /**
     * Processes the session after movie choice
     */
    fun finishSession()

    /**
     * Accepts incoming invitation from a remote partecipant
     */
    fun acceptSessionInvitation(intent:Intent)

    /**
     * Returns whether the MovieManager is the host or not
     */
    fun isHost():Boolean

}

abstract  class Callback {
    fun onSuccess(){}
    fun onFailure(){}
}