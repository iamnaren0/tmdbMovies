package com.example.data

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)
