package com.mertsen.imdbproject.dependecyInjection.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(FavoriteMovie::class), version = 1)
abstract class FavoriteMovieDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao():FavoriteMovieDao
}