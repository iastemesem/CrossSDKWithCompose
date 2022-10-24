package com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote

import com.nttdata.techcorner_droidcon.BuildConfig
import com.nttdata.techcorner_droidcon.core.error.ServerException
import com.nttdata.techcorner_droidcon.feature.home.data.api.HomeAPI
import com.nttdata.techcorner_droidcon.feature.home.data.model.MovieModel

class HomeRemoteDataSourceImpl : HomeRemoteDataSource {
    override suspend fun getMovies(): MovieModel {
        try {
            return HomeAPI.getInstance().getMovies(BuildConfig.MOVIE_API_KEY)
        } catch (e: Exception) {
            throw ServerException()
        }
    }
}