package com.example.flipcards.model

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Boolean>
    suspend fun signUp(email: String, password: String): Result<Boolean>
    suspend fun signOut(): Result<Boolean>
    fun getCurrentUser(): FirebaseUser?
}