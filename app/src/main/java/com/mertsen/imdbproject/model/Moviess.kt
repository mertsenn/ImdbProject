package com.mertsen.imdbproject.model

import com.google.gson.annotations.SerializedName

data class Moviess(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("original_language")
    val original_language: String?,
    @SerializedName("original_title")
    val original_title: String?,
    @SerializedName("popularity")
    val popularity: Float?,
    @SerializedName("release_date")
    val release_date: String?,
    @SerializedName("vote_average")
    val vote_average: Float?,
    @SerializedName("vote_count")
    val vote_count: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("poster_path")
    val poster_path: String?,
    @SerializedName("overview")
    val overview : String?,
    @SerializedName("id")
    val id : String

)
