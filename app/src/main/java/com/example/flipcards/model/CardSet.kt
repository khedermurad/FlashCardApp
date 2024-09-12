package com.example.flipcards.model

data class CardSet(
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
    val language: String? = "",
    val visibility: String? = "",
    var cards: List<CardData> = emptyList()
){
    constructor() : this(null, null, null, null, null, listOf())
}