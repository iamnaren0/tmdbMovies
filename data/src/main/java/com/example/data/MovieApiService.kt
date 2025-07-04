package com.example.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("3/discover/movie")
    suspend fun getMovies(): List<Movie>

    @GET("3/trending/movie/day")
    suspend fun getTrendingMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MovieResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Movie

    @GET("3/search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int = 1): MovieResponse
}