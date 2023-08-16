package com.mertsen.imdbproject.modelView

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {


    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
}