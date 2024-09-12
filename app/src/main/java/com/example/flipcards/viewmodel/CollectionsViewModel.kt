package com.example.flipcards.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.CollectionsRepository
import kotlinx.coroutines.launch

class CollectionsViewModel(private val repository: CollectionsRepository, private val userId: String) : ViewModel() {

    private val _collections = MutableLiveData<List<String>>(emptyList())
    val collections: LiveData<List<String>> get() = _collections

    init {
        getUserCollections()
    }

    private fun getUserCollections() {
        viewModelScope.launch {
            try {
                val collections = repository.getUserCollections(userId)
                _collections.value = collections
                Log.d("CollectionsViewModel", "Collections updated: $collections")
            } catch (e: Exception) {
                Log.e("CollectionsViewModel", "Error fetching collections", e)
            }
        }
    }
    fun saveCollection(title: String){
        viewModelScope.launch {
            try {
                repository.saveCollection(userId, title)
                getUserCollections()
            }catch (e: Exception){
                Log.e("CollectionsViewModel", "Error saving collection", e)
            }
        }
    }

}
