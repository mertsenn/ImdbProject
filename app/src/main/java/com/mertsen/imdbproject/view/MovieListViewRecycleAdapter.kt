package com.mertsen.imdbproject.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.view.ListViewFragmentDirections

// PagingDataAdapter kullanarak film listesi için RecyclerView adaptörü
class MovieListViewRecycleAdapter(private val favoriteMovieDao: FavoriteMovieDao) :
    PagingDataAdapter<Moviess, MovieListViewRecycleAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    // Film poster URL'leri için bir liste
    private var imageUrls: List<String>? = null

    // Favori filmlerin listesi
    private var favoriteMovies: List<FavoriteMovie> = emptyList()

    // İki film öğesini karşılaştırmak için DiffUtil kullanımı
    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Moviess>() {
            override fun areItemsTheSame(oldItem: Moviess, newItem: Moviess): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Moviess, newItem: Moviess): Boolean {
                return oldItem == newItem
            }
        }
    }

    // Film poster URL'leri ayarlama işlemi
    fun setImageUrls(urls: List<String>) {
        imageUrls = urls
    }

    // Favori filmleri ayarlama işlemi
    fun setFavoriteMovies(favoriteMovies: List<FavoriteMovie>) {
        this.favoriteMovies = favoriteMovies
        notifyDataSetChanged()
    }

    // RecyclerView öğelerini görüntülemek için inner ViewHolder sınıfı
    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mySingleMovieItem: TextView = itemView.findViewById(R.id.singleMovieItem)
        private val myMovieImage: ImageView = itemView.findViewById(R.id.myFavoriteMovieImage)
        private val starImageView: ImageView = itemView.findViewById(R.id.starImageView)

        // Film verilerini bağlama işlemi
        fun bind(movie: Moviess) {
            mySingleMovieItem.text = movie.title
            val imageUrl = movie.poster_path
            imageUrl?.let {
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w780${it}")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(myMovieImage)
            }

            // Film favori mi kontrolü ve yıldız simgesinin görünürlüğünü ayarlama
            val isFavorite = favoriteMovies.any { it.PID == movie.id.toInt() }
            starImageView.visibility = if (isFavorite) View.VISIBLE else View.GONE

            // Film öğesine tıklamada navigasyon işlemi
            mySingleMovieItem.setOnClickListener {
                val action = ListViewFragmentDirections.actionListViewFragmentToDetailMovieFragment(
                    movie.title!!,
                    movie.poster_path!!,
                    movie.popularity!!,
                    movie.release_date!!,
                    movie.vote_count!!.toFloat(),
                    movie.vote_average!!,
                    movie.overview!!,
                    movie.id!!
                )
                itemView.findNavController().navigate(action)
            }
        }
    }

    // RecyclerView öğeleri oluşturma işlemi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.single_move_item, parent, false)
        return MovieViewHolder(itemView)
    }

    // RecyclerView öğeleri bağlama işlemi
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { movie ->
            holder.bind(movie)
        }
    }
}
