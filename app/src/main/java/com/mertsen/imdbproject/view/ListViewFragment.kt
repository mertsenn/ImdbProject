package com.mertsen.imdbproject.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.databinding.FragmentListViewBinding
import com.mertsen.imdbproject.dependecyInjection.room.FavoriteMovieDao
import com.mertsen.imdbproject.model.Moviess
import com.mertsen.imdbproject.modelView.ListViewModelView
import com.mertsen.imdbproject.modelView.SharedViewModel
import com.mertsen.imdbproject.service.MoviesPagingSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListViewFragment : Fragment() {

    // Fragment'ta kullanılacak veri bağlama nesnesi
    lateinit var binding: FragmentListViewBinding

    // Filmleri gösterecek adapter
    lateinit var movieAdapter: MovieListViewRecycleAdapter

    // ViewModel nesneleri
    private val viewModel by viewModels<ListViewModelView>()
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
        binding = FragmentListViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fragment görünümü oluşturulduğunda
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Favorilere gitmek için butonun tıklanma işlemi
        val listViewToFavoriteButton = binding.listViewToFavoriteButton
        listViewToFavoriteButton.setOnClickListener {
            // Favori fragment'a geçiş yap
            view.findNavController().navigate(R.id.action_listViewFragment_to_favoriteMoveFragment)
        }

        // Film adaptörünü oluştur
        movieAdapter = MovieListViewRecycleAdapter(favoriteMovieDao,viewModel)

        // RecyclerView ayarları
        binding.listRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }

        // LiveData'ları izleyerek veri güncellemelerini yap
        viewModel.liveDataMovieList.observe(viewLifecycleOwner, { movieList ->
            viewModel.liveDataMovieUrlList.observe(viewLifecycleOwner, { imageUrls ->
                movieAdapter.setImageUrls(imageUrls)
            })
        })

        // API'den filmleri çağırma işlemi
        viewModel.callMovieRepo()

        // Favori filmleri izleyerek adapteri güncelleme işlemi
        viewModel.getFavoriteMovieDao().getAllLiveData().observe(viewLifecycleOwner) { favoriteMovies ->
            movieAdapter.setFavoriteMovies(favoriteMovies)
            val favoriteMovieIds = favoriteMovies.map { it.PID }
        }




        // Paging ayarları
        val pagingConfig = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        )

        // Pager oluştur
        val pager = Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviesPagingSource(viewModel.movieService) }
        )

        // Paging verilerini topla ve adaptere gönderme işlemi
        val pagingDataFlow: Flow<PagingData<Moviess>> = pager.flow

        viewLifecycleOwner.lifecycleScope.launch {
            pagingDataFlow.collectLatest { pagingData ->
                movieAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
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
    private fun simpleSearch(query: String) {
        Log.d("SearchDebug", "simpleSearch is called with query: $query")

        if (query.isNotBlank()) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // API'den film arama sonuçlarını alma
                    val movieSearchResponse = viewModel.movieService.searchMovies(query)
                    val searchResults = movieSearchResponse.results ?: emptyList()

                    // Arama sonuçlarını PagingData'ya dönüştürme ve adaptere gönderme
                    val pagingData = PagingData.from(searchResults)
                    movieAdapter.submitData(pagingData)

                    Log.d("SearchDebug", "simpleSearch successful")
                } catch (e: Exception) {
                    Log.e("SearchDebug", "simpleSearch error: ${e.message}", e)
                    Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("SearchDebug", "Search was not performed")
        }
    }
}
