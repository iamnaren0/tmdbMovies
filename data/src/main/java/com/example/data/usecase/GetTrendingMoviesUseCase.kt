package com.example.data.usecase

import com.example.data.repo.MovieRepository

class GetTrendingMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend fun execute(page: Int = 1) = movieRepository.getTrendingMovies(page)
}