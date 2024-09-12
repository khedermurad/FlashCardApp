package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.model.CardsRepository
import com.google.ai.client.generativeai.GenerativeModel

class CardsViewViewModelFactory(private val repository: CardsRepository, private val generativeModel: GenerativeModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
            return CardsViewModel(repository, generativeModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}