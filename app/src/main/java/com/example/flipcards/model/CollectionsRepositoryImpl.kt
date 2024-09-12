package com.example.flipcards.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

private const val TAG = "CollectionsRepositoryImpl"
class CollectionsRepositoryImpl(private val db: FirebaseFirestore) : CollectionsRepository {

    override suspend fun getUserCollections(userId: String): List<String> {
        return try {
            val userCollectionsRef = db.collection("userCollections")
                .document(userId)
                .collection("collections")
                .orderBy("title", Query.Direction.ASCENDING)
            val snapshot = userCollectionsRef.get().await()
            val collections = snapshot.documents.map { it.id }
            Log.d(TAG, "Fetched collections: $collections")
            collections
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching collections", e)
            emptyList()
        }
    }

    override suspend fun saveCollection(userId: String, title: String) {
        try {
            val collectionRef = db.collection("userCollections")
                .document(userId)
                .collection("collections")

            val collection = hashMapOf("title" to title)

            collectionRef.add(collection).await()
            Log.d(TAG, "Collection saved: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving collection", e)
        }
    }
}