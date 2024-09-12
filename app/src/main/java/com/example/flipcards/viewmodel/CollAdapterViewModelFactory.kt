package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.CollAdapterRepository

class CollAdapterViewModelFactory(private val repository: CollAdapterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if(modelClass.isAssignableFrom(CollAdapterViewModel::class.java)){
             @Suppress("UNCHECKED_CAST")
             return CollAdapterViewModel(repository) as T
         }
         throw IllegalArgumentException("Unknown ViewModel class")
    }
}