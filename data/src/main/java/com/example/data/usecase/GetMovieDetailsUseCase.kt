package com.example.data.usecase

import com.example.data.repo.MovieRepository

class GetMovieDetailsUseCase(private val movieRepository: MovieRepository) {
    suspend fun execute(movieId: Int) = movieRepository.getMovieDetails(movieId)
}