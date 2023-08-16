package com.mertsen.imdbproject.dependecyInjection.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteMovie(
    @PrimaryKey
    val PID : Int,
    @ColumnInfo("title")
    val title :String,
    @ColumnInfo("imageUrl")
    val imageUrl :String
)
