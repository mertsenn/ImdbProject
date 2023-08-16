package com.mertsen.imdbproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertsen.imdbproject.databinding.FragmentFavoriteMoveBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovie
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDatabase
import com.mertsen.imdbproject.view.FavoriteMovieRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Favori filmleri gösteren fragment
@AndroidEntryPoint
class FavoriteMoveFragment : Fragment() {
    lateinit var binding: FragmentFavoriteMoveBinding

    @Inject
    lateinit var favoriteMovieDatabase: FavoriteMovieDatabase

    private lateinit var favoriteMovieDao: FavoriteMovieDao

    // Favori film listesi gözlemleyicisi
    private val favoriteMovieListObserver = Observer<List<FavoriteMovie>> { favoriteMovies ->
        favoriteMovieAdapter.setFavoriteMovies(favoriteMovies)
    }

    // Favori film RecyclerView adaptörü
    private lateinit var favoriteMovieAdapter: FavoriteMovieRecyclerAdapter

    // Fragment oluşturulduğunda çağrılır
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteMoveBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Görünüm oluşturulduğunda çağrılır
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteMovieRecyclerView = binding.favoriteMovieRecyclerView

        // Favori film RecyclerView adaptörü oluşturuldu
        favoriteMovieAdapter = FavoriteMovieRecyclerAdapter()

        favoriteMovieDao = favoriteMovieDatabase.favoriteMovieDao()

        favoriteMovieRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteMovieAdapter
        }

        // LiveData ile favori film listesini gözlemleme
        favoriteMovieDao.getAllLiveData().observe(viewLifecycleOwner, favoriteMovieListObserver)
    }
}
