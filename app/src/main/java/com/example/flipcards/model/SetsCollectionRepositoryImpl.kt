package com.example.flipcards.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SetsCollectionRepositoryImpl(private val db: FirebaseFirestore): SetsCollectionRepository {
    override suspend fun getDocIds(userId: String, collectionId: String): List<String> {
        return try {
            val docIdsRef = db.collection("userCollections")
                .document(userId)
                .collection("collections")
                .document(collectionId)
                .collection("documentIds")
            val snapshot = docIdsRef.get().await()
            snapshot.documents.map { it.id }
        }catch (e: Exception){
            emptyList()
        }
    }

    override suspend fun getCollectionSets(userId: String, collectionId: String): Flow<List<Pair<Set, String>>> = flow {
        try {
            val docIds = getDocIds(userId, collectionId)

            if (docIds.isNotEmpty()) {
                val snapshot = db.collection("cardSets")
                    .whereIn("__name__", docIds)
                    .get()
                    .await()

                val setsWithDocIds = snapshot.documents.mapNotNull { doc ->
                    val set = doc.toObject<Set>()
                    if (set != null) {
                        val isOwner = set.userId == userId
                        val isPublic = set.visibility == "Öffentlich"
                        if (isOwner || isPublic) {
                            Pair(set, doc.id)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

                emit(setsWithDocIds)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

}