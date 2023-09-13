package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class Cast(
    @SerializedName("name")
    val name : String,
    @SerializedName("character")
    val character : String,
    @SerializedName("profile_path")
    val profile_path : String,
    @SerializedName("cast_id")
    val castId : Int
)
