package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedMovieDao {
    @Insert
    suspend fun insertBookmarkedMovie(movie: BookmarkedMovieEntity)

    @Delete
    suspend fun deleteBookmarkedMovie(movie: BookmarkedMovieEntity)

    @Query("SELECT * FROM bookmarked_movies")
    fun getAllBookmarkedMovies(): Flow<List<BookmarkedMovieEntity>>
}
