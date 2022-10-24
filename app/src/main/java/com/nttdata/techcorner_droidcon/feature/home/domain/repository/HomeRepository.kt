package com.nttdata.techcorner_droidcon.feature.home.domain.repository

import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie

interface HomeRepository {
    suspend fun getMovies(): List<Movie>
}