package com.nttdata.techcorner_droidcon.feature.home.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie
import com.nttdata.techcorner_droidcon.feature.home.domain.use_case.GetMovies
import kotlinx.coroutines.launch

class HomeViewModel constructor(
    private val getMoviesUseCase: GetMovies
) : ViewModel() {
    private val _movies = mutableStateListOf<Movie>()

    val movies: List<Movie>
        get() = _movies

    suspend fun getMovies() {
        viewModelScope.launch {
            try {
                _movies.addAll(getMoviesUseCase())
            } catch (e: Exception) {

            }
        }
    }
}