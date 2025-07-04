package com.example.data.usecase

import com.example.data.repo.MovieRepository

class SearchMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend fun execute(query: String, page: Int = 1) = movieRepository.searchMovies(query, page)
}