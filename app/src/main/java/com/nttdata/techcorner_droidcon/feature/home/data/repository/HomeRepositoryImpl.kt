package com.nttdata.techcorner_droidcon.feature.home.data.repository

import com.nttdata.techcorner_droidcon.feature.home.data.data_source.remote.HomeRemoteDataSource
import com.nttdata.techcorner_droidcon.feature.home.data.model.toMovie
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie
import com.nttdata.techcorner_droidcon.feature.home.domain.repository.HomeRepository

class HomeRepositoryImpl(private val dataSource: HomeRemoteDataSource) : HomeRepository {
    override suspend fun getMovies(): List<Movie> {
        val response = dataSource.getMovies()
        val moviesList = arrayListOf<Movie>()
        response.results.map {
            moviesList.add(it.toMovie())
        }

        return moviesList;
    }
}