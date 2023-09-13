package com.mertsen.imdbproject.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.databinding.FavoriteSingleMovieItemBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie

// Favori filmleri gösteren RecyclerView adaptörü
class FavoriteMovieRecyclerAdapter : RecyclerView.Adapter<FavoriteMovieRecyclerAdapter.FavoriteMovieHolder>() {
    private var myfavoriteMovies: List<FavoriteMovie> = emptyList()

    // Favori film öğesinin bağlama işlemleri
    inner class FavoriteMovieHolder(private val binding: FavoriteSingleMovieItemBinding) : RecyclerView.ViewHolder(binding.root) {

        // Favori film verisini bağlama işlemi
        fun bind(favoriteMovie: FavoriteMovie) {
            binding.singleMovieItem.text = favoriteMovie.title

            val imageUrl = favoriteMovie.imageUrl
            imageUrl?.let {
                // Glide kütüphanesi ile resim yüklemesi
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w780${imageUrl}")
                    .into(binding.myFavoriteMovieImage)
            }
        }
    }

    // Yeni bir ViewHolder oluşturma
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoriteSingleMovieItemBinding.inflate(inflater, parent, false)

        return FavoriteMovieHolder(binding)
    }

    // Gösterilecek öğe sayısı
    override fun getItemCount(): Int {
        return myfavoriteMovies.size
    }

    // Veriyi görüntüleme işlemi
    override fun onBindViewHolder(holder: FavoriteMovieHolder, position: Int) {
        val currentFavoriteMovie = myfavoriteMovies[position]
        holder.bind(currentFavoriteMovie)
    }

    // Favori filmleri güncelleme işlemi
    fun setFavoriteMovies(favoriteMovies: List<FavoriteMovie>) {
        myfavoriteMovies = favoriteMovies
        notifyDataSetChanged()
    }
}
