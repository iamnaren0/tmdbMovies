package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_movies")
data class BookmarkedMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)
