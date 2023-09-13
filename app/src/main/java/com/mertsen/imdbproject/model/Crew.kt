package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class Crew(
    @SerializedName("name")
    val name : String,
    @SerializedName("job")
    val job : String,
    @SerializedName("profile_path")
    val profile_path : String,
    @SerializedName("id")
    val id : Int,
)
