package com.example.flipcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.CardData
import com.example.flipcards.model.CardSet
import com.example.flipcards.model.CardsInputRepository
import kotlinx.coroutines.launch

class CardsInputViewModel(private val repository: CardsInputRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    private val _languages = MutableLiveData<List<String>>()
    val languages: LiveData<List<String>> get() = _languages

    private val _cardSet = MutableLiveData<CardSet>()
    val cardSet: LiveData<CardSet> get() = _cardSet

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.loadCategories()
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            }
        }
    }

    fun loadLanguages() {
        viewModelScope.launch {
            try {
                _languages.value = repository.loadLanguages()
            } catch (e: Exception) {
                _error.value = "Failed to load languages: ${e.message}"
            }
        }
    }

    fun saveCardSet(
        create: Boolean,
        docId: String?,
        title: String,
        description: String,
        category: String,
        language: String,
        visibility: String,
        userId: String,
        cards: List<CardData>
    ) {
        viewModelScope.launch {
            try {
                repository.saveCardSet(create, docId, title, description, category, language, visibility, userId, cards)
            } catch (e: Exception) {
                _error.value = "Failed to save card set: ${e.message}"
            }
        }
    }

    fun loadCardSet(docId: String) {
        viewModelScope.launch {
            try {
                repository.loadCardSet(docId).collect { cardSet ->
                    _cardSet.value = cardSet
                }
            } catch (e: Exception) {
                _error.value = "Failed to load card set: ${e.message}"
            }
        }
    }

    fun deleteAllCards(docId: String){
        viewModelScope.launch {
            try {
                repository.deleteAllCards(docId)
            }catch (e: Exception){
                _error.value = "Failed to delete all cards: ${e.message}"
            }
        }
    }

}