package com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote

import com.nttdata.techcorner_droidcon.BuildConfig
import com.nttdata.techcorner_droidcon.feature.home.data.api.HomeAPI
import com.nttdata.techcorner_droidcon.feature.home.data.model.MovieModel

class HomeRemoteDataSourceImpl : HomeRemoteDataSource {
    override suspend fun getMovies(): MovieModel {
        return try {
            HomeAPI.getInstance().getMovies(BuildConfig.MOVIE_API_KEY)
        } catch (e: Exception) {
            MovieModel(page = 0, results = listOf(), total_pages = 0, total_results = 0)
        }
    }
}