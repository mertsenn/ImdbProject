package com.mertsen.imdbproject.view

import MovieGenreRecylerAdapter
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.mertsen.imdbproject.databinding.FragmentMovieDetailBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDatabase
import com.mertsen.imdbproject.modelView.DetailMovieModelView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Detaylı film görünümü fragmentı
@AndroidEntryPoint
class DetailMovieFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private val args: DetailMovieFragmentArgs by navArgs()
    private lateinit var db : FavoriteMovieDatabase
    private lateinit var favoriteMovieDao: FavoriteMovieDao
    val viewModel : DetailMovieModelView by viewModels()
    lateinit var movieGenreRecylerAdapter: MovieGenreRecylerAdapter
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
        db = Room.databaseBuilder(
            requireContext(),
            FavoriteMovieDatabase::class.java,
            "FavoriteMovies"
        ).build()

        // Favori film DAO'su alındı
        favoriteMovieDao = db.favoriteMovieDao()

        // Görünüme bağlı öğeler alındı
        val myMovieImage = binding.movieImage
        val myMovieTitle = binding.movieTitle
        val myMovieReleaseDate = binding.movieReleaseDate
        //val myMoviePopularity = binding.moviePopularity
        val myMovieVoteCount = binding.movieVoteCount
        val myMovieRate = binding.movieRate
        val myMovieOverview = binding.movieOverview
        val myAddToFavoriteButton = binding.addFavoriteButton
        //val myDeleteButton = binding.removeFromFavoriteButton
        val myMovieTrailer = binding.movieTrailer
        val myMovieDirector = binding.director
        val myMovieWriter = binding.writers

        // Detaylar görünüm öğelerine atandı
        myMovieTitle.text = args.movieTitleArg
        myMovieReleaseDate.text = args.movieReleaseDateArg
        //myMoviePopularity.text = args.moviePopularityArg.toString()
        myMovieVoteCount.text = "${args.movieVoteCountArg.toInt().toString()} People Voted"
        myMovieRate.text = "${args.movieVoteArg.toString()} / 10"
        myMovieOverview.setText(args.movieOverView)
        myMovieOverview.movementMethod = ScrollingMovementMethod()
        val myMovieId = args.movieIDArg
        val myImageUrl = args.movieUrlArg

        //filmlerin kategorilerinin sınıflandırılması
        viewModel.movieIdFromFragment = myMovieId//movei id ile specific filmin katagorilerini almak için istek
        viewModel.callMovieGenre()//istek ile gelen kategorileri livedataya atma
        Log.i("DetailMovieFragment", "Call Movie Calıstı")

        viewModel.liveDataMovieGenres.observe(viewLifecycleOwner) { genres ->//livedatayı inceler
            val movieGenreAdapter = MovieGenreRecylerAdapter(genres)//genres adındaki değişkeni recylerview a liste olarak atar
            binding.genresRecyclerView.apply {
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )//yatay recycler view
                adapter = movieGenreAdapter//adaptör tanımlama
            }
        }


        //buton rengi ayarları için
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val isFavorite = favoriteMovieDao.getFavoriteMovieById(myMovieId.toInt())
            withContext(Dispatchers.Main) {
                if (isFavorite != null) {
                    // Update the button's state
                    myAddToFavoriteButton.setBackgroundColor(Color.parseColor("#E3E5F3"))
                    myAddToFavoriteButton.setTextColor(Color.parseColor("#000000"))
                    myAddToFavoriteButton.setText("Remove from Favorites")
                } else {
                    // Update the button's state
                    myAddToFavoriteButton.setBackgroundColor(Color.parseColor("#583DA6"))
                    myAddToFavoriteButton.setTextColor(Color.parseColor("#FFFFFF"))
                    myAddToFavoriteButton.setText("Add to Favorites")
                }
            }
        }


        //Oyuncuların bilgileri ve fotoğraflarının konulduğu kod bölümü
        viewModel.callMovieCast()
        viewModel.liveDataMovieCast.observe(viewLifecycleOwner){
            val castAdapter = CastsRecyclerAdapter(it)
            binding.castPhotos.apply {
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter= castAdapter
            }
            castAdapter.notifyDataSetChanged()
        }


        //movie Crew livedatası burda çağırılıyor
        viewModel.liveDataMovieCrewDirector.observe(viewLifecycleOwner) { crewList ->
            val cleanedList = crewList.map { it.name?.removeSurrounding("[", "]") }
            myMovieDirector.text = "Director: ${cleanedList.joinToString(", ") { it ?: "N/A" }}"
        }

        viewModel.liveDataMovieCrewWriter.observe(viewLifecycleOwner) { crewList ->
            val cleanedList = crewList.map { it.name?.removeSurrounding("[", "]") }
            myMovieWriter.text = "Writers: ${cleanedList.joinToString(", ") { it ?: "N/A" }}"
        }


        //Movie Trailerin olduğu LiveDatayı Çağırma
        viewModel.callMovieTrailer()
        viewModel.liveDataMovieTrailer.observe(viewLifecycleOwner){key->
            val videoKey= key[0]
            val youtubeUrl = "https://www.youtube.com/embed/$videoKey?autoplay=1"
            val html = "<iframe width=\"100%\" height=\"100%\" src=\"$youtubeUrl\" frameborder=\"0\" allowfullscreen></iframe>"
            myMovieTrailer.webChromeClient = object : WebChromeClient() {
                var customView: View? = null
                var customViewCallback: CustomViewCallback? = null

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)

                    customView = view
                    customViewCallback = callback

                    myMovieTrailer.visibility = View.GONE

                    val decorView = requireActivity().window.decorView as FrameLayout
                    decorView.addView(customView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                        ))
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()

                        customView?.let {
                            val decorView = requireActivity().window.decorView as FrameLayout
                        decorView.removeView(it)
                        customViewCallback?.onCustomViewHidden()
                        customView = null
                        customViewCallback = null
                    }

                    myMovieTrailer.visibility = View.VISIBLE
                }
            }
            myMovieTrailer.webViewClient = WebViewClient()
            myMovieTrailer.settings.javaScriptEnabled = true
            myMovieTrailer.loadData(html, "text/html", "utf-8")
        }



        // Glide kütüphanesi kullanılarak film afişi yüklendi
        Glide.with(view.context).load("https://image.tmdb.org/t/p/w780${myImageUrl}")
            .into(myMovieImage)

        // "Favorilere Ekle" butonuna tıklanınca
        myAddToFavoriteButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val isFavorite = favoriteMovieDao.getFavoriteMovieById(myMovieId.toInt())
                Log.i("myMoviewIDTag", "$myMovieId")

                if (isFavorite == null) {
                    val favoriteMovie = FavoriteMovie(
                        PID = myMovieId.toInt(),
                        title = args.movieTitleArg,
                        imageUrl = args.movieUrlArg,
                        isFavorite = true
                    )
                    // Veritabanına favori film eklendi
                    favoriteMovieDao.insert(favoriteMovie)
                    withContext(Dispatchers.Main) {
                        myAddToFavoriteButton.setBackgroundColor(Color.parseColor("#E3E5F3"))
                        myAddToFavoriteButton.setTextColor(Color.parseColor("#000000"))
                        myAddToFavoriteButton.setText("Remove to Favorites")
                        Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    val favoriteMovie = FavoriteMovie(
                        PID = myMovieId.toInt(),
                        title = args.movieTitleArg,
                        imageUrl = args.movieUrlArg,
                        isFavorite = false
                    )
                    // Veritabanından favori film çıkarıldı
                    favoriteMovieDao.delete(favoriteMovie)


                    withContext(Dispatchers.Main) {
                        myAddToFavoriteButton.setBackgroundColor(Color.parseColor("#583DA6"))
                        myAddToFavoriteButton.setTextColor(Color.parseColor("#FFFFFF"))
                        myAddToFavoriteButton.setText("Add to Favorites")
                        Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }

    } }
