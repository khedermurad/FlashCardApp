package com.example.flipcards.model

data class QuizCard(
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int
)
