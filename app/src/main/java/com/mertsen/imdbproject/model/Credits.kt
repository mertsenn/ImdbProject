package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class Credits(
    @SerializedName("id")
    val movieID : Int,
    @SerializedName("cast")
    val casts : List<Cast>,
    @SerializedName("crew")
    val crew : List<Crew>
)
