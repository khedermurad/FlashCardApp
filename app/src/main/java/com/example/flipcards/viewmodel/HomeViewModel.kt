package com.example.flipcards.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.HomeRepository
import com.example.flipcards.model.Set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: HomeRepository, private val userId: String) : ViewModel() {

    private val _setsWithDocIds = MutableLiveData<List<Pair<Set, String>>>()
    val setsWithDocIds: LiveData<List<Pair<Set, String>>> get() = _setsWithDocIds

    private val _searchResults = MutableLiveData<List<Pair<Set, String>>>()
    val searchResults: LiveData<List<Pair<Set, String>>> get() = _searchResults


    init {
        loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch {
            try {
                repository.getUserSets(userId).collect { setsWithDocIds ->
                    _setsWithDocIds.value = setsWithDocIds
                }
            } catch (e:Exception){
                Log.e(TAG, "Error when loading UserSets: $e")
            }
        }
    }

    fun searchSets(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchOwnSets(query, userId).collect { setsWithDocIds ->
                _searchResults.postValue(setsWithDocIds)
            }
        }
    }


}
