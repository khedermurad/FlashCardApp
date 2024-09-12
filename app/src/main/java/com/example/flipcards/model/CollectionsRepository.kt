package com.example.flipcards.model

import com.google.android.gms.tasks.Task

interface CollectionsRepository {
    suspend fun getUserCollections(userId: String): List<String>

    suspend fun saveCollection(userId: String, title:String)

}