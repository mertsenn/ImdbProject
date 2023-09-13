package com.mertsen.imdbproject.modelView

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.mertsen.imdbproject.model.Cast
import com.mertsen.imdbproject.model.Credits
import com.mertsen.imdbproject.model.Crew
import com.mertsen.imdbproject.model.Genres
import com.mertsen.imdbproject.model.MovieDetails
import com.mertsen.imdbproject.model.MovieResponse
import com.mertsen.imdbproject.model.VideosResults
import com.mertsen.imdbproject.service.MovieService
import com.mertsen.imdbproject.view.DetailMovieFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailMovieModelView @Inject constructor(
    val movieService: MovieService,
    ):ViewModel() {


    val liveDataMovieGenres = MutableLiveData<List<String>>()//Genreler için
    var movieIdFromFragment: String = ""//specific film ID sine göre retrofit isteği
    val liveDataMovieCast =  MutableLiveData<List<Cast>>()//Castlar için
    val liveDataMovieTrailer = MutableLiveData<List<String>>()//youtube trailerleri için
    val liveDataMovieCrewDirector = MutableLiveData<List<Crew>>()
    val liveDataMovieCrewWriter = MutableLiveData<List<Crew>>()

//film kategorilerini çeker
    fun callMovieGenre() {
        viewModelScope.launch(Dispatchers.IO) {
            val movieDetails = movieService.getMovieCategory(movieIdFromFragment)
            val genreLiveData = mutableListOf<String>()
            movieDetails.genres.forEach{myName->
                myName.name?.let{name->
                    val movieName = name
                    genreLiveData.add(movieName)
                }
                }

            liveDataMovieGenres.postValue(genreLiveData)


            }

        }
    //cast bilgilerini çeker
    fun callMovieCast (){
        viewModelScope.launch(Dispatchers.IO){
            val callCast = movieService.getMovieCast(movieIdFromFragment)
            val callCrew = movieService.getMovieCast(movieIdFromFragment)
            liveDataMovieCast.postValue(callCast.casts)

            val directors = callCrew.crew.filter { it.job == "Director" }
            val writers = callCrew.crew.filter { it.job == "Writer" }

            liveDataMovieCrewDirector.postValue(directors)
            liveDataMovieCrewWriter.postValue(writers)
        }
    }

    //film youtube keylerini api den gelen verilerin typelarına göre checkleyip eğer typesi trailer ise livedatayaa ekler
    fun callMovieTrailer (){
        viewModelScope.launch(Dispatchers.IO) {
            val callTrailer = movieService.getMovieTrailer(movieIdFromFragment)
            val trailerVideos = callTrailer.results.filter { it.type == "Trailer" }
            val trailerKey = trailerVideos.map{it.key}
            liveDataMovieTrailer.postValue(trailerKey)
        }
    }
}