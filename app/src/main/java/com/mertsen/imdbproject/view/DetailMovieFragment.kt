package com.mertsen.imdbproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.bumptech.glide.Glide
import com.mertsen.imdbproject.databinding.FragmentMovieDetailBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Detaylı film görünümü fragmentı
@AndroidEntryPoint
class DetailMovieFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private val args: DetailMovieFragmentArgs by navArgs()
    private lateinit var db : FavoriteMovieDatabase
    private lateinit var favoriteMovieDao: FavoriteMovieDao

    // Fragment oluşturulduğunda çağrılır
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Görünüm oluşturulduğunda çağrılır
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Room veritabanı oluşturuldu
        db = Room.databaseBuilder(requireContext(), FavoriteMovieDatabase::class.java, "FavoriteMovies").build()

        // Favori film DAO'su alındı
        favoriteMovieDao = db.favoriteMovieDao()

        // Görünüme bağlı öğeler alındı
        val myMovieImage = binding.movieImage
        val myMovieTitle = binding.movieTitle
        val myMovieReleaseDate = binding.movieReleaseDate
        val myMoviePopularity = binding.moviePopularity
        val myMovieVoteCount = binding.movieVoteCount
        val myMovieRate = binding.movieRate
        val myMovieOverview = binding.movieOverview
        val myAddToFavoriteButton = binding.addFavoriteButton
        val myDeleteButton = binding.removeFromFavoriteButton

        // Detaylar görünüm öğelerine atandı
        myMovieTitle.text = args.movieTitleArg
        myMovieReleaseDate.text = args.movieReleaseDateArg
        myMoviePopularity.text = args.moviePopularityArg.toString()
        myMovieVoteCount.text = args.movieVoteCountArg.toString()
        myMovieRate.text = args.movieVoteArg.toString()
        myMovieOverview.setText(args.movieOverView)

        val myMovieId = args.movieIDArg
        val myImageUrl = args.movieUrlArg

        // Glide kütüphanesi kullanılarak film afişi yüklendi
        Glide.with(view.context).load("https://image.tmdb.org/t/p/w780${myImageUrl}").into(myMovieImage)

        // "Favorilere Ekle" butonuna tıklanınca
        myAddToFavoriteButton.setOnClickListener{
            val favoriteMovie = FavoriteMovie(
                PID = myMovieId.toInt(),
                title = args.movieTitleArg,
                imageUrl = args.movieUrlArg
            )
            // Veritabanına favori film eklendi
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                favoriteMovieDao.insert(favoriteMovie)
            }

            Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
        }

        // "Favorilerden Çıkar" butonuna tıklanınca
        myDeleteButton.setOnClickListener{
            val favoriteMovie = FavoriteMovie(
                PID = myMovieId.toInt(),
                title = args.movieTitleArg,
                imageUrl = args.movieUrlArg
            )
            // Veritabanından favori film çıkarıldı
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                favoriteMovieDao.delete(favoriteMovie)
            }
            Toast.makeText(requireContext(), "Removed From Favorites", Toast.LENGTH_SHORT).show()
        }
    }
}
