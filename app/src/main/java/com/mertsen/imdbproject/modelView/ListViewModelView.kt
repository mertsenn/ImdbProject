package com.mertsen.imdbproject.modelView

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDatabase
import com.mertsen.imdbproject.model.MovieResponse
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.service.MovieService
import com.mertsen.imdbproject.service.MoviesPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// HiltViewModel, Hilt tarafından yönetilen bir ViewModel sınıfını temsil eder
@HiltViewModel
class ListViewModelView @Inject constructor(
    val movieService: MovieService, // MovieService bağımlılığının enjekte edilmesi
    private val favoriteMovieDatabase: FavoriteMovieDatabase // FavoriteMovieDatabase bağımlılığının enjekte edilmesi
): ViewModel() {

    // MutableLiveData, gözlemleyicilere veri akışı sağlayan veri deposu türünden bir sınıftır
    val liveDataMovieList = MutableLiveData<MovieResponse>()
    val liveDataMovieUrlList = MutableLiveData<List<String>>()


    // Movie verilerini almak için çağrılan yöntem
    fun callMovieRepo() {
        // viewModelScope, ViewModel'in ömrü boyunca çalışan CoroutineScope'u temsil eder
        viewModelScope.launch(Dispatchers.IO) {
            // MovieService ile API'den tüm filmlerin alınması
            val movie = movieService.getAllMovie()
            // MutableLiveData'ye yeni veri atanır
            liveDataMovieList.postValue(movie)

            // Sonuçları dolaşarak poster url'lerini elde
            val imageUrls = mutableListOf<String>()
            movie.results.forEach { result ->
                result.poster_path?.let { path ->
                    val imageUrl = "https://image.tmdb.org/t/p/w780$path"
                    imageUrls.add(imageUrl)
                }
            }
            // MutableLiveData'ye yeni url listesi atanır
            liveDataMovieUrlList.postValue(imageUrls)
        }
    }

    // Favori film verilerine erişim için FavoriteMovieDao döndürülür
    fun getFavoriteMovieDao(): FavoriteMovieDao {
        return favoriteMovieDatabase.favoriteMovieDao()
    }
    fun addFavoriteMovie (movie : Moviess){
        viewModelScope.launch(Dispatchers.IO){
            val favoriteMovie = FavoriteMovie(
                PID = movie.id.toInt(),
                imageUrl = movie.poster_path!!,
                title = movie.title!!,
                isFavorite = true
            )
            favoriteMovieDatabase.favoriteMovieDao().insert(favoriteMovie)
        }
    }
    fun removeFavoriteMovie (movie : Moviess){
        viewModelScope.launch(Dispatchers.IO){
            val favoriteMovie = FavoriteMovie(
                PID = movie.id.toInt(),
                imageUrl = movie.poster_path!!,
                title = movie.title!!,
                isFavorite = false
            )
            favoriteMovieDatabase.favoriteMovieDao().delete(favoriteMovie)


        }
    }




}
