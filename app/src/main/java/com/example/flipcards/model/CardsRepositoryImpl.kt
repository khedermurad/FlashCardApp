package com.example.flipcards.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class CardsRepositoryImpl(private val db: FirebaseFirestore) : CardsRepository {

    override suspend fun getCards(setId: String): List<CardData> {

        val snapshot = db.collection("cardSets")
            .document(setId)
            .collection("cards")
            .orderBy("order")
            .get()
            .await()

        return snapshot.toObjects(CardData::class.java)
    }

    override suspend fun getLastReadPage(userId: String, setId: String): Long? {

        val document = db.collection("readingStatus")
            .document(userId)
            .collection("cardSets")
            .document(setId)
            .get()
            .await()

        return document.getLong("lastReadPage")
    }

    override suspend fun saveLastReadPage(userId: String, setId: String, lastReadPage: Long) {
        val data = hashMapOf("lastReadPage" to lastReadPage)
        db.collection("readingStatus")
            .document(userId)
            .collection("cardSets")
            .document(setId)
            .set(data)
            .await()
    }
}