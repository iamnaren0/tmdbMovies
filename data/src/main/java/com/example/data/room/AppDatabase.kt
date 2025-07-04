package com.example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class, BookmarkedMovieEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun bookmarkedMovieDao(): BookmarkedMovieDao
}
