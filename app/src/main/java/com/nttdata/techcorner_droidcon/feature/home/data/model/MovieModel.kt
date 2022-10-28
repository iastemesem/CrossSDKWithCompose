package com.nttdata.techcorner_droidcon.feature.home.data.model


import androidx.annotation.Keep
import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie

@Keep
data class MovieModel(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)

@Keep
data class Result(
    val adult: Boolean,
    val backdrop_path: String,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

fun Result.toMovie(): Movie {
    return Movie(
        title = this.title,
        description = this.overview,
        briefDescription = this.overview.substring(0, 60),
        adultOnly = this.adult,
        backgroundImageUrl = this.backdrop_path,
        originalLanguage = this.original_language,
        posterImageUrl = this.poster_path,
        voteAverage = this.vote_average.toString(),
        voteCount = this.vote_count.toString(),
        releaseDate = this.release_date
    )
}