package com.example.flipcards.model

import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getUserSets(userId: String): Flow<List<Pair<Set, String>>>
    suspend fun searchOwnSets(query: String, userId: String): Flow<List<Pair<Set, String>>>

}