package com.mertsen.imdbproject.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.view.GridViewFragment
import com.mertsen.imdbproject.view.ListViewFragment
import dagger.hilt.android.AndroidEntryPoint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.mertsen.imdbproject.modelView.GridViewModelView
import com.mertsen.imdbproject.modelView.SharedViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null
    private val gridViewViewModel: GridViewModelView by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(findViewById(R.id.toolbar))
        val fragmentContainerView = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

        navController = fragmentContainerView?.findNavController()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    sharedViewModel.setSearchQuery(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    sharedViewModel.setSearchQuery(newText)
                }
                return true
            }
        })

        return true
    }






    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_grid_view -> {
                // GridViewFragment'e geçiş
                navController?.navigate(R.id.gridViewFragment)
                return true
            }
            R.id.action_list_view -> {
                // ListViewFragment'e geçiş
                navController?.navigate(R.id.listViewFragment)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


}

