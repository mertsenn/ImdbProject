package com.mertsen.imdbproject.dependecyInjection.room


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM FavoriteMovie")
    fun getAllLiveData(): LiveData<List<FavoriteMovie>>
    @Delete
    fun delete (movie: FavoriteMovie)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: FavoriteMovie)

    @Query("SELECT * FROM FavoriteMovie WHERE PID = :PID")
    fun getFavoriteMovieById(PID: Int): FavoriteMovie?



}