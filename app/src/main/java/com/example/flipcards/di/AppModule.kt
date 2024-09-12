package com.example.flipcards.di

import android.content.Context
import com.example.flipcards.BuildConfig
import com.example.flipcards.model.AuthRepository
import com.example.flipcards.model.AuthRepositoryImpl
import com.example.flipcards.model.CardsInputRepository
import com.example.flipcards.model.CardsInputRepositoryImpl
import com.example.flipcards.model.CardsRepository
import com.example.flipcards.model.CardsRepositoryImpl
import com.example.flipcards.model.CollAdapterRepository
import com.example.flipcards.model.CollAdapterRepositoryImpl
import com.example.flipcards.model.CollectionsRepository
import com.example.flipcards.model.CollectionsRepositoryImpl
import com.example.flipcards.model.HomeRepository
import com.example.flipcards.model.HomeRepositoryImpl
import com.example.flipcards.model.SearchRepository
import com.example.flipcards.model.SearchRepositoryImpl
import com.example.flipcards.model.SetRepository
import com.example.flipcards.model.SetRepositoryImpl
import com.example.flipcards.model.SetsCollectionRepository
import com.example.flipcards.model.SetsCollectionRepositoryImpl
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


interface AppModule{
    val firebaseAuth: FirebaseAuth
    val db: FirebaseFirestore
    val generativeModel: GenerativeModel

    val authRepository: AuthRepository
    val homeRepository: HomeRepository
    val setRepository: SetRepository
    val searchRepository: SearchRepository
    val setsCollectionRepository: SetsCollectionRepository
    val cardsInputRepository: CardsInputRepository
    val cardsRepository: CardsRepository
    val collectionRepository: CollectionsRepository
    val collAdapterRepository: CollAdapterRepository

}

class AppModuleImpl(private val appContext: Context) : AppModule {
    override val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuth)
    }
    override val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.apiKey
        )
    }

    override val homeRepository: HomeRepository by lazy {
        HomeRepositoryImpl(db)
    }
    override val setRepository: SetRepository by lazy {
        SetRepositoryImpl(db)
    }
    override val searchRepository: SearchRepository by lazy {
        SearchRepositoryImpl(db)
    }
    override val setsCollectionRepository: SetsCollectionRepository by lazy {
        SetsCollectionRepositoryImpl(db)
    }
    override val cardsInputRepository: CardsInputRepository by lazy {
        CardsInputRepositoryImpl(db)
    }
    override val cardsRepository: CardsRepository by lazy {
        CardsRepositoryImpl(db)
    }
    override val collectionRepository: CollectionsRepository by lazy {
        CollectionsRepositoryImpl(db)
    }
    override val collAdapterRepository: CollAdapterRepository by lazy {
        CollAdapterRepositoryImpl(db)
    }


}