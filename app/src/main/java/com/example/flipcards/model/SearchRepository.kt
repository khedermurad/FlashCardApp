package com.example.flipcards.model

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPublicSets(query: String): Flow<List<Pair<Set, String>>>
}