package com.nttdata.techcorner_droidcon.feature.home.presentation.model

import androidx.lifecycle.MutableLiveData

interface MovieData {
    /**
     * Enum class used to maintain the state of the movie
     */
    enum class MovieState {
        DISCONNECTED,
        SEARCHING,
        WAITING
    }

    fun getPrimarySessionName(): MutableLiveData<String>
    fun getSecondarySessionName(): MutableLiveData<String>
    fun getMovieState(): MutableLiveData<MovieState>
    fun getLocalMovieChoice(): String
    fun getRemoteMovieChoiche(): String
}