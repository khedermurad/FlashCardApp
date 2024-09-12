package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.SetsCollectionRepository


class SetsCollectionViewModelfactory(private val repository: SetsCollectionRepository
, private val userId: String
, private val collectionId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetsCollectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetsCollectionViewModel(repository, userId, collectionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}