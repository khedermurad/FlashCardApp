package com.example.flipcards.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await


class HomeRepositoryImpl(private val db: FirebaseFirestore) : HomeRepository {

    override fun getUserSets(userId: String): Flow<List<Pair<Set, String>>> = flow {
        val snapshot = db.collection("cardSets")
            .whereEqualTo("userId", userId)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
            .get()
            .await()

        val sets = snapshot.toObjects(Set::class.java)
        val docIds = snapshot.documents.map { it.id }
        emit(sets.zip(docIds))
    }

    override suspend fun searchOwnSets(query: String, userId: String): Flow<List<Pair<Set, String>>> = flow {
        val setsWithDocIds = mutableListOf<Pair<Set, String>>()
        val querySnapshot = db.collection("cardSets")
            .whereEqualTo("userId", userId)
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