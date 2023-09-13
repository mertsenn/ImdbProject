package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class VideosResults(
    @SerializedName("id")
    val id :String,
    @SerializedName("key")
    val key : String,
    @SerializedName("type")
    val type : String
)
