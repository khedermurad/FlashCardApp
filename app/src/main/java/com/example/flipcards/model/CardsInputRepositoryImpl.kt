package com.example.flipcards.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await


@Suppress("UNCHECKED_CAST")
class CardsInputRepositoryImpl(private val db: FirebaseFirestore) : CardsInputRepository {

    override suspend fun loadCategories(): List<String> {
        val document = db.collection("Options").document("RTslcmZC1BsSq6eT6BAd").get().await()
        return document.get("Categories") as List<String>
    }

    override suspend fun loadLanguages(): List<String> {
        val document = db.collection("Options").document("RTslcmZC1BsSq6eT6BAd").get().await()
        return document.get("Languages") as List<String>
    }


    override suspend fun saveCardSet(
        create: Boolean,
        docId: String?,
        title: String,
        description: String,
        category: String,
        language: String,
        visibility: String,
        userId: String,
        cards: List<CardData>
    ): Task<Void> {

        val cardSet = hashMapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "language" to language,
            "visibility" to visibility,
            "userId" to userId,
            "creation_time_ms" to System.currentTimeMillis()
        )

        if (create) {
            val newDoc = db.collection("cardSets").add(cardSet).await()
            val cardSetId = newDoc.id
            saveCards(cardSetId, cards)
        } else {
            docId?.let {
                db.collection("cardSets").document(it).set(cardSet).await()
                saveCards(it, cards)
            }
        }
        return Tasks.forResult(null)
    }


    private suspend fun saveCards(cardSetId: String, cards: List<CardData>) {
        val batch = db.batch()
        val cardsCollectionRef = db.collection("cardSets").document(cardSetId).collection("cards")

        cards.forEachIndexed { index, cardData ->
            val cardMap = hashMapOf(
                "word" to cardData.word,
                "definition" to cardData.definition,
                "order" to index
            )
            val cardDoc = cardsCollectionRef.document()
            batch.set(cardDoc, cardMap)
        }

        batch.commit().await()
    }

    override suspend fun loadCardSet(docId: String): Flow<CardSet> {
        val document = db.collection("cardSets").document(docId).get().await()
        val cardSet = document.toObject(CardSet::class.java)
        val cardsSnapshot = document.reference.collection("cards").orderBy("order").get().await()
        val cards = cardsSnapshot.toObjects(CardData::class.java)
        cardSet?.cards = cards
        return flowOf(cardSet!!)
    }

    override suspend fun deleteAllCards(docId: String?): Task<Void> {
        if (docId == null) {
            throw IllegalArgumentException("Document ID cannot be null")
        }

        val batch: WriteBatch = db.batch()
        val cardsCollectionRef = db.collection("cardSets").document(docId).collection("cards")

        val cards = cardsCollectionRef.get().await()

        for (document in cards) {
            batch.delete(document.reference)
        }

        return batch.commit()
    }
}