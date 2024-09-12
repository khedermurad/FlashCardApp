package com.example.flipcards.model


interface SetRepository {
    fun getReviews(docId: String, onSuccess: (List<Review>) -> Unit, onFailure: (Exception) -> Unit)
    fun deleteDocumentWithSubcollections(docId: String, onDeleteComplete: () -> Unit)
    fun getUserCollections(uid: String, onSuccess: (List<Pair<Array<String>, Array<String>>>) -> Unit, onFailure: (Exception) -> Unit)
    fun alreadyRated(docId: String, uid: String, callback: (Boolean) -> Unit)
    fun saveDocumentToCollection(userId: String, collectionId: String, docId: String, onSuccess: () -> Unit, onAlreadyExists: () -> Unit,onFailure: (Exception) -> Unit)
    fun createNewCollection(userId: String, docId: String, title: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deleteDocIdFromCollection(uid: String, collectionId: String, docId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun saveReview(docId: String, review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit )
}