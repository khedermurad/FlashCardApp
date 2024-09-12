package com.example.flipcards.model

import kotlinx.coroutines.flow.Flow

interface SetsCollectionRepository {
    suspend fun getDocIds(userId: String, collectionId: String): List<String>
    suspend fun getCollectionSets(userId: String, collectionId: String): Flow<List<Pair<Set, String>>>
}