package com.example.flipcards.model

interface CollAdapterRepository {
    fun getCollectionTitle(uid: String, collectionId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)

    fun updateCollectionTitle(uid: String, collectionId: String, newTitle: String , onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    fun deleteCollection(uid: String, collectionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

}