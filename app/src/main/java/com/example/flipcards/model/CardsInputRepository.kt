package com.example.flipcards.model

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

interface CardsInputRepository {
    suspend fun loadCategories(): List<String>
    suspend fun loadLanguages(): List<String>
    suspend fun saveCardSet(
        create: Boolean,
        docId: String?,
        title: String,
        description: String,
        category: String,
        language: String,
        visibility: String,
        userId: String,
        cards: List<CardData>
    ): Task<Void>
    suspend fun loadCardSet(docId: String): Flow<CardSet>
    suspend fun deleteAllCards(docId: String?): Task<Void>
}