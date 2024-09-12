package com.example.flipcards.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.Set
import com.example.flipcards.model.SetsCollectionRepository
import kotlinx.coroutines.launch

class SetsCollectionViewModel(
    private val repository: SetsCollectionRepository
    ,private val userId: String
    ,private val collectionId: String) : ViewModel() {

    private val _setsWithDocIds = MutableLiveData<List<Pair<Set, String>>>()
    val setsWithDocIds: LiveData<List<Pair<Set, String>>> get() = _setsWithDocIds

    init {
        loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch {
            try {
                repository.getCollectionSets(userId, collectionId).collect { setsWithDocIds ->
                    _setsWithDocIds.value = setsWithDocIds
                    Log.i("SetsCollectionViewModel", "sets size ${setsWithDocIds.size}")
                }
            } catch (e:Exception){
                Log.e(TAG, "Error when loading CollectionSets: $e")
            }
        }
    }


}