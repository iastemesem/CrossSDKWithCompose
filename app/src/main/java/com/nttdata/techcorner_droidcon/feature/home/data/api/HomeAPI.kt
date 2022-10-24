package com.nttdata.techcorner_droidcon.feature.home.data.api

import com.nttdata.techcorner_droidcon.feature.home.data.model.MovieModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeAPI {
    @GET("discover/movie")
    suspend fun getMovies(@Query("api_key") apiKey:String): MovieModel

    companion object {
        var homeAPI: HomeAPI? = null
        fun getInstance(): HomeAPI {
            if (homeAPI == null) {
                homeAPI = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(HomeAPI::class.java)
            }

            return homeAPI!!;
        }
    }
}