package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie
import com.nttdata.techcorner_droidcon.feature.home.domain.use_case.GetMovies
import com.nttdata.techcorner_droidcon.feature.home.presentation.model.MovieData
import kotlinx.coroutines.launch

class HomeViewModel constructor(
    private val getMoviesUseCase: GetMovies
) : ViewModel(), MovieData {
    private val _movies = mutableStateListOf<Movie>()

    companion object {
        private const val KEY_LOCAL_PLAYER_NAME = "LOCAL_PLAYER_NAME"
        private const val KEY_OPPONENT_PLAYER_NAME = "OPPONENT_PLAYER_NAME"
        private const val KEY_LOCAL_PLAYER_SCORE = "LOCAL_PLAYER_SCORE"
        private const val KEY_OPPONENT_PLAYER_SCORE = "OPPONENT_PLAYER_SCORE"
        private const val KEY_ROUNDS_COMPLETED = "ROUNDS_COMPLETED"
    }

    enum class HomeState {
        LOADING,
        LOADED,
        ERROR
    }


    private var localMovieChoice: String = "1234567890"
    private var remoteMovieChoice: String = ""
    private var localMovieChoiceConfirmed: Boolean = false
    private var movieState = MutableLiveData(MovieData.MovieState.DISCONNECTED)
    var homeState = MutableLiveData(HomeState.LOADING)

    val movies: List<Movie>
        get() = _movies

    suspend fun getMovies() {
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
}