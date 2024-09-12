package com.example.flipcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.SearchRepository
import kotlinx.coroutines.Dispatchers
import com.example.flipcards.model.Set
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<List<Pair<Set, String>>>()
    val searchResults: LiveData<List<Pair<Set, String>>> get() = _searchResults

    fun searchSets(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchPublicSets(query).collect { setsWithDocIds ->
                _searchResults.postValue(setsWithDocIds)
            }
        }
    }
}