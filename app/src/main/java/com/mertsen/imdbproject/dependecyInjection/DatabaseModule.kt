package com.mertsen.imdbproject.dependecyInjection

import android.app.Application
import androidx.room.Room
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFavoriteMovieDatabase(application: Application): FavoriteMovieDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            FavoriteMovieDatabase::class.java,
            "FavoriteMovies"
        ).build()
    }
    @Provides
    fun provideFavoriteMovieDao(database: FavoriteMovieDatabase): FavoriteMovieDao {
        return database.favoriteMovieDao()
    }
}