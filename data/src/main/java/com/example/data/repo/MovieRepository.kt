package com.example.data.repo

import com.example.data.Movie
import com.example.data.MovieResponse

interface MovieRepository {
    suspend fun getMovies(): List<Movie>
    suspend fun getTrendingMovies(page: Int = 1): MovieResponse?
    suspend fun getNowPlayingMovies(page: Int = 1): MovieResponse?
    suspend fun getMovieDetails(movieId: Int): Movie?
    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse?
}