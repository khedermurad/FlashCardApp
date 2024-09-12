package com.example.flipcards.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await



class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Boolean> {
        return try {
            firebaseAuth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}