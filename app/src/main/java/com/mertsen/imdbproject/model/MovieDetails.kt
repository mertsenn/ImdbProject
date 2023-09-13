package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class MovieDetails (
    @SerializedName("adult")
    val adult : Boolean,
    @SerializedName("backdrop_path")
    val backdrop_path: String,
    @SerializedName("genres")
    val genres : List<Genres>
)
