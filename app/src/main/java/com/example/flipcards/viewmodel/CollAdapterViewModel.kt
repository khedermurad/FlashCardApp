package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.CollAdapterRepository
import kotlinx.coroutines.launch

class CollAdapterViewModel(private val repository: CollAdapterRepository) : ViewModel() {

    fun getCollectionTitle(uid: String, collectionId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.getCollectionTitle(uid, collectionId, onSuccess, onFailure)
        }
    }

    fun updateCollectionTitle(
        uid: String,
        collectionId: String,
        newTitle: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            repository.updateCollectionTitle(uid, collectionId, newTitle, onSuccess, onFailure)
        }
    }

    fun deleteCollection(
        uid: String,
        collectionId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            repository.deleteCollection(uid, collectionId, onSuccess, onFailure)
        }
    }


}