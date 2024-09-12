package com.example.flipcards.model

import com.google.firebase.firestore.PropertyName

data class Set(
    var category: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0L,
    var description: String = "",
    var language: String = "",
    var title: String = "",
    var userId: String = "",
    var visibility: String = ""
)
