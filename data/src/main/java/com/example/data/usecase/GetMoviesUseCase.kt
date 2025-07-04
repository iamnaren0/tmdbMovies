package com.example.data.usecase

import com.example.data.Movie
import com.example.data.repo.MovieRepository

class GetMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend fun execute(): List<Movie> {
        return movieRepository.getMovies()
    }
}