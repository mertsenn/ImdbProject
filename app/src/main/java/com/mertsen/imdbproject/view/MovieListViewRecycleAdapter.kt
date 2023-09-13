package com.mertsen.imdbproject.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
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
import com.mertsen.imdbproject.modelView.ListViewModelView
import com.mertsen.imdbproject.view.ListViewFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text


// PagingDataAdapter kullanarak film listesi için RecyclerView adaptörü
class MovieListViewRecycleAdapter(private val favoriteMovieDao: FavoriteMovieDao, private val listViewModel : ListViewModelView) :


    PagingDataAdapter<Moviess, MovieListViewRecycleAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    // Film poster URL'leri için bir liste
    private var imageUrls: List<String>? = null
    private var isFavorite: Boolean = false


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
        private val myViewCount : TextView = itemView.findViewById(R.id.viewCount)
        private val imdbRate : TextView = itemView.findViewById(R.id.imdbRate)
        private val ratingBar : RatingBar = itemView.findViewById(R.id.imdbRatingBar)
        val checkBox : CheckBox = itemView.findViewById(R.id.favoriteCheckBox)

        // Film verilerini bağlama işlemi

        fun bind(movie: Moviess) {
            mySingleMovieItem.text = movie.title
            myViewCount.text = movie.vote_count.toString()
            val myImdbRate = movie.vote_average
            //filmlerin movie ratinginge göre yıldızları doldurma
            if(myImdbRate != null){
                val truncatedRating = (myImdbRate * 10).toInt() / 20.0//veriler 1 ve 10 arasında olduğu ve yıldızlar 5 tane olduğu için oranladım
                ratingBar.rating = truncatedRating.toFloat()
                val formattedRating = String.format("%.1f", myImdbRate / 2)//3.35 gibi sayıları 3.3 yapma
                imdbRate.text = (formattedRating)
            }

            // checkbox ile favorilere ekleme


            checkBox.setOnCheckedChangeListener{buttonView , isChecked ->
                isFavorite = isChecked
                if (isChecked){
                    Log.d("CheckBoxIsSelected", "selected")
                    listViewModel.addFavoriteMovie(movie)
                }
                else {
                    Log.d("CheckBoxIsSelected", "not selected")
                    listViewModel.removeFavoriteMovie(movie)
                }
            }


            val imageUrl = movie.poster_path
            imageUrl?.let {
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w780${it}")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(myMovieImage)
            }


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

            CoroutineScope(Dispatchers.IO).launch {
                val isFavorite = favoriteMovieDao.getFavoriteMovieById(movie.id.toInt()) != null
                withContext(Dispatchers.Main) {
                    holder.checkBox.isChecked = isFavorite
                } }
}}}
