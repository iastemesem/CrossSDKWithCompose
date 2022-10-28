package com.nttdata.techcorner_droidcon.feature.home.domain.entity

data class Movie(
    val title: String,
    val description: String,
    val briefDescription: String,
    val adultOnly: Boolean,
    val backgroundImageUrl: String,
    val originalLanguage: String,
    val posterImageUrl: String,
    val voteAverage: String,
    val voteCount: String,
    val releaseDate: String,
)