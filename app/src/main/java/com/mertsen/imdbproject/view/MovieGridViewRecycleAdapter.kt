package com.mertsen.imdbproject.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.mertsen.imdbproject.model.MovieResponse
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.modelView.GridViewModelView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// PagingDataAdapter kullanarak film listesi için GridView adaptörü
class MovieGridViewRecycleAdapter(private val favoriteMovie: FavoriteMovieDao, private val gridModelView: GridViewModelView)
    : PagingDataAdapter<Moviess, MovieGridViewRecycleAdapter.MovieGridViewHolder>(MOVIE_COMPARATOR) {

    // Film poster URL'leri için bir liste
    private var imageUrls: List<String>? = null

    // PagingDataAdapter için DiffUtil kullanımı
    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Moviess>() {
            override fun areItemsTheSame(oldItem: Moviess, newItem: Moviess): Boolean {
                return oldItem.id == newItem.id // Film ID'leri aynı mı kontrol eder
            }

            override fun areContentsTheSame(oldItem: Moviess, newItem: Moviess): Boolean {
                return oldItem == newItem // Veri içeriği aynı mı kontrol eder
            }
        }
    }

    // Film poster URL'lerini ayarlama işlemi
    fun setData(urls: List<String>) {
        imageUrls = urls
    }

    // RecyclerView için inner ViewHolder sınıfı
    inner class MovieGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mySingleMovieItem = itemView.findViewById<TextView>(R.id.singleGridMovieItem)
        val movieImageView = itemView.findViewById<ImageView>(R.id.GridImageView) // ImageView eklendi
        val favoriteCheckBox : CheckBox = itemView.findViewById(R.id.favoriteCheckBox)

        // Film verilerini bağlama işlemi
        fun bind(movie: Moviess) {
            mySingleMovieItem.text = movie.title

            val imageUrl = movie.poster_path
            imageUrl?.let {
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w780${it}")
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Önbellekten yüklemeyi devre dışı bırakır
                    .skipMemoryCache(true) // Bellek önbelleğini devre dışı bırakır
                    .into(movieImageView)
            }
            //checkbox ile filmleri favorilere ekleme
            favoriteCheckBox.setOnCheckedChangeListener{buttonView , isChecked ->

                if (isChecked){
                    Log.d("CheckBoxIsSelected", "selected")
                    gridModelView.addFavoriteMovie(movie)
                }
                else {
                    Log.d("CheckBoxIsSelected", "not selected")
                    gridModelView.removeFavoriteMovie(movie)
                }
            }

            // Film öğesine tıklamada navigasyon işlemi
            mySingleMovieItem.setOnClickListener {
                val action = GridViewFragmentDirections.actionGridViewFragmentToDetailMovieFragment(
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
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.grid_single_view_item, parent, false)
        return MovieGridViewHolder(itemView)
    }

    // RecyclerView öğeleri bağlama işlemi
    override fun onBindViewHolder(
        holder: MovieGridViewHolder,
        position: Int
    ) {
        getItem(position).let { movie ->
            if (movie != null) {
                holder.bind(movie)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val isFavorite = movie?.id?.let { favoriteMovie.getFavoriteMovieById(it.toInt()) } != null
                withContext(Dispatchers.Main) {
                    holder.favoriteCheckBox.isChecked = isFavorite
                } }
        }
    }
}
