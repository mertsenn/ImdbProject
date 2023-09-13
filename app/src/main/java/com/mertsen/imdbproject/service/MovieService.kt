package com.mertsen.imdbproject.service

import com.mertsen.imdbproject.model.Credits
import com.mertsen.imdbproject.model.Genres
import com.mertsen.imdbproject.model.MovieDetails
import com.mertsen.imdbproject.model.MovieResponse
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.model.Videos
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("3/movie/popular?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getAllMovie() : MovieResponse
    @GET("3/movie/popular?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getAllResult() : Result<List<MovieResponse>>
    @GET("{ImageUrl}")
    suspend fun getImageResult(@Path("ImageUrl") imageUrl: String): Moviess
    @GET("3/movie/popular?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getNextMoviePage(@Query("page") pageNum: Int): MovieResponse
    @GET("3/search/movie?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun searchMovies(@Query("query") query: String): MovieResponse
    @GET("3/movie/{movie_id}?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getMovieCategory(@Path("movie_id")movie_id :String) :MovieDetails
    @GET("3/movie/{movie_id}/credits?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getMovieCast(@Path("movie_id")movie_id : String ): Credits
    @GET("3/movie/{movie_id}/videos?api_key=b6b352461eeae68bd8852a0b1574ff6b")
    suspend fun getMovieTrailer(@Path("movie_id")movie_id: String) : Videos
}