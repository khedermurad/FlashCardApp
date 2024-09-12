package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.CardsInputRepository

class CardsInputViewModelFactory(
    private val repository: CardsInputRepository
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsInputViewModel::class.java)) {
            return CardsInputViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
