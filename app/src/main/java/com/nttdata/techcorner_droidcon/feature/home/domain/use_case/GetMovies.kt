package com.nttdata.techcorner_droidcon.feature.home.domain.use_case

import com.nttdata.techcorner_droidcon.feature.home.domain.entity.Movie
import com.nttdata.techcorner_droidcon.feature.home.domain.repository.HomeRepository

class GetMovies(private val repository: HomeRepository) {
    suspend operator fun invoke(): List<Movie> {
        return repository.getMovies()
    }
}