package com.example.flipcards.model

interface CardsRepository {
    suspend fun getCards(setId: String): List<CardData>
    suspend fun getLastReadPage(userId: String, setId: String): Long?
    suspend fun saveLastReadPage(userId: String, setId: String, lastReadPage: Long)
}