package com.mertsen.imdbproject.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.databinding.FragmentGridViewBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.modelView.GridViewModelView
import com.mertsen.imdbproject.modelView.SharedViewModel
import com.mertsen.imdbproject.service.MoviesPagingSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GridViewFragment : Fragment() {
    // Veri bağlama nesnesi
    private lateinit var binding: FragmentGridViewBinding

    // Film grid adaptörü
    lateinit var movieGridAdapter: MovieGridViewRecycleAdapter

    // ViewModel nesnesi
    val viewModel: GridViewModelView by viewModels()

    // Arama işlemleri için SearchView
    private lateinit var searchView: SearchView

    // Paylaşılan ViewModel nesnesi
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val favoriteMovieDao: FavoriteMovieDao by lazy {
        viewModel.getFavoriteMovieDao()
    }

    // Fragment oluşturulduğunda
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Veri bağlamayı oluştur
        binding = FragmentGridViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fragment görünümü oluşturulduğunda
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Favorilere gitmek için butonun tıklanma işlemi
        val gridViewToFavoriteButton = binding.gridViewToFavoriteButton
        gridViewToFavoriteButton.setOnClickListener {
            // GridViewFragment'ten FavoriteMoveFragment'e geçiş yap
            view.findNavController().navigate(R.id.action_gridViewFragment_to_favoriteMoveFragment)
        }

        // Film grid adaptörünü oluştur
        movieGridAdapter = MovieGridViewRecycleAdapter(favoriteMovieDao,viewModel)
        binding.gridRecycleView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2) // İki sütunlu grid düzeni
            adapter = movieGridAdapter
        }

        // LiveData'ları izleyerek veri güncellemelerini yap
        viewModel.liveDataMovieListGrid.observe(viewLifecycleOwner, { movieList ->
            viewModel.liveDataImageUrls.observe(viewLifecycleOwner, { imageUrls ->
                movieGridAdapter.setData( imageUrls)
            })
        })

        // API'den filmleri çağırma işlemi
        viewModel.callMovieRepo()

        // Paging Configuration
        val pagingConfig = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,

            )

        // Pager oluştur
        val pager = Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviesPagingSource(viewModel.movieService) }
        )

        // Create the Flow of PagingData
        val pagingDataFlow: Flow<PagingData<Moviess>> = pager.flow

        // Paging verilerini topla ve adaptere gönderme işlemi
        viewLifecycleOwner.lifecycleScope.launch {
            pagingDataFlow.collectLatest { pagingData ->
                movieGridAdapter.submitData(pagingData)
            }
        }

        // Paylaşılan ViewModel üzerinden arama sorgusunu dinleme işlemi
        sharedViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (!query.isNullOrBlank()) {
                simpleSearch(query)
            }
        }
    }

    // Basit arama işlemini gerçekleştirme
    fun simpleSearch(query: String) {
        Log.d("SearchDebug", "simpleSearch is called with query: $query")

        if (query.isNotBlank()) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // API'den film arama sonuçlarını alma
                    val movieSearchResponse = viewModel.movieService.searchMovies(query)
                    val searchResults = movieSearchResponse.results ?: emptyList()

                    // Arama sonuçlarını PagingData'ya dönüştürme ve adaptere gönderme
                    val pagingData = PagingData.from(searchResults)
                    movieGridAdapter.submitData(pagingData)

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "hata oldu", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("SearchDebug", "arama gerçekleştirilmedi ")
        }
    }
}
