package com.example.flipcards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.Review
import com.example.flipcards.model.SetRepository
import kotlinx.coroutines.launch

class SetsViewModel(private val repository: SetRepository) : ViewModel() {

    fun getReviews(docId: String, onSuccess: (List<Review>) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            repository.getReviews(docId, onSuccess, onFailure)
        }
    }

    fun deleteDocumentWithSubcollections(docId: String, onDeleteComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteDocumentWithSubcollections(docId, onDeleteComplete)
        }
    }


    fun getUserCollections(uid: String, onSuccess: (List<Pair<Array<String>, Array<String>>>) -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.getUserCollections(uid, onSuccess, onFailure)
        }
    }

    fun alreadyRated(docId: String, uid: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.alreadyRated(docId, uid, callback)
        }
    }

    fun saveDocumentToCollection(userId: String, collectionId: String, docId: String, onSuccess: () -> Unit, onAlreadyExists: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            repository.saveDocumentToCollection(userId, collectionId, docId, onSuccess, onAlreadyExists ,onFailure)
        }
    }

    fun createNewCollection(userId: String, docId: String, title: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            repository.createNewCollection(userId, docId, title, onSuccess, onFailure)
        }
    }

    fun deleteDocIdFromCollection(uid: String,
                                  collectionId: String,
                                  docId: String,
                                  onSuccess: () -> Unit,
                                  onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            repository.deleteDocIdFromCollection(uid, collectionId, docId, onSuccess, onFailure)
        }

    }

    fun saveReview(
        docId: String,
        review: Review,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch{
            repository.saveReview(docId, review, onSuccess, onFailure)
        }
    }

}