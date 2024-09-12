package com.example.flipcards.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SearchRepositoryImpl(private val db: FirebaseFirestore) : SearchRepository {
    override suspend fun searchPublicSets(query: String): Flow<List<Pair<Set, String>>> = flow {
        val setsWithDocIds = mutableListOf<Pair<Set, String>>()
        val querySnapshot = db.collection("cardSets")
            .whereEqualTo("visibility", "Öffentlich")
            .orderBy("creation_time_ms", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        for (document in querySnapshot) {
            val set = document.toObject(Set::class.java)
            if (set.title.contains(query, ignoreCase = true) || set.description.contains(query, ignoreCase = true)) {
                setsWithDocIds.add(Pair(set, document.id))
            }
        }
        emit(setsWithDocIds)
    }
}