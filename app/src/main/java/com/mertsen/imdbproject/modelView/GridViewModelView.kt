package com.mertsen.imdbproject.modelView

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
import com.mertsen.imdbproject.view.MovieGridViewRecycleAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

// HiltViewModel, Hilt tarafından yönetilen bir ViewModel sınıfını temsil eder
@HiltViewModel
class GridViewModelView @Inject constructor(
    @Named("movieService") val movieService: MovieService, // "movieService" adında etiketlenmiş bir MovieService bağımlılığının enjekte edilmesi
    private val favoriteMovieDatabase: FavoriteMovieDatabase // FavoriteMovieDatabase bağımlılığının enjekte edilmesi

) : ViewModel() {

    // MutableLiveData, gözlemleyicilere veri akışı sağlayan veri deposu türünden bir sınıftır
    val liveDataMovieListGrid = MutableLiveData<MovieResponse>()
    val liveDataImageUrls = MutableLiveData<List<String>>() // Resim URL listesi burada tutulacak

    // Movie verilerini almak için çağrılan yöntem
    fun callMovieRepo() {
        // viewModelScope, ViewModel'in ömrü boyunca çalışan CoroutineScope'u temsil eder
        viewModelScope.launch(Dispatchers.IO) {
            // MovieService ile API'den tüm filmlerin alınması
            val movie = movieService.getAllMovie()
            // MutableLiveData'ye yeni veri atanır
            liveDataMovieListGrid.postValue(movie)

            // Sonuçları dolaşarak poster url'lerini elde etmek
            val imageUrls = mutableListOf<String>()
            movie.results.forEach { result ->
                result.poster_path?.let { path ->
                    val imageUrl = "https://image.tmdb.org/t/p/w780$path"
                    imageUrls.add(imageUrl)
                }
            }
            // MutableLiveData'ye yeni url listesi atanır
            liveDataImageUrls.postValue(imageUrls)
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
