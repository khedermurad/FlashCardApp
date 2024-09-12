package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.SetRepository

class SetsViewModelFactory(private val repository: SetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}