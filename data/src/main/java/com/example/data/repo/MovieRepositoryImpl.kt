package com.example.data.repo

import com.example.data.Movie
import com.example.data.MovieApiService
import com.example.data.MovieResponse

class MovieRepositoryImpl(private val movieApiService: MovieApiService) : MovieRepository {
    override suspend fun getMovies(): List<Movie> {
        return try {
            val movies = movieApiService.getMovies()
            movies
        } catch (e: Exception) {
            // Log the error or handle it appropriately
            emptyList()
        }
    }

    override suspend fun getTrendingMovies(page: Int): MovieResponse? {
        return try {
            movieApiService.getTrendingMovies(page)
        } catch (e: Exception) {
            // Log the error or handle it appropriately
            null
        }
    }

    override suspend fun getNowPlayingMovies(page: Int): MovieResponse? {
        return try {
            movieApiService.getNowPlayingMovies(page)
        } catch (e: Exception) {
            // Log the error or handle it appropriately
            null
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Movie? {
        return try {
            movieApiService.getMovieDetails(movieId)
        } catch (e: Exception) {
            // Log the error or handle it appropriately
            null
        }
    }

    override suspend fun searchMovies(
        query: String,
        page: Int
    ): MovieResponse? {
        return try {
            movieApiService.searchMovies(query, page)
        } catch (e: Exception) {
            // Log the error or handle it appropriately
            null
        }
    }
}