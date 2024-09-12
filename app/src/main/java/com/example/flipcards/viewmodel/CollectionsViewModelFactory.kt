package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.CollectionsRepository

class CollectionsViewModelFactory(private val repository: CollectionsRepository, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionsViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}