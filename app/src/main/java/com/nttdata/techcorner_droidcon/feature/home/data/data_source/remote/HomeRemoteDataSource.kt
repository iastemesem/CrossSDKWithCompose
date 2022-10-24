package com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote

import com.nttdata.techcorner_droidcon.feature.home.data.model.MovieModel

interface HomeRemoteDataSource {
    suspend fun getMovies(): MovieModel
}