package com.example.flipcards.model

data class CardData(
    val word: String?,
    val definition: String?,
){
    constructor() : this(null, null)
}
