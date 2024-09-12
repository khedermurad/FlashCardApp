package com.example.flipcards.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipcards.model.CardData
import com.example.flipcards.model.CardsRepository
import com.example.flipcards.model.QuizCard
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

const val TAG = "CardsViewModel"

class CardsViewModel(private val repository: CardsRepository, private val generativeModel: GenerativeModel) : ViewModel() {


    private val _cards = MutableLiveData<List<CardData>>()
    val cards: LiveData<List<CardData>> get() = _cards

    private val _lastReadPage = MutableLiveData<Long?>()
    val lastReadPage: LiveData<Long?> get() = _lastReadPage

    private val _quizCards = MutableLiveData<List<QuizCard>>()
    val quizCards: LiveData<List<QuizCard>> get() = _quizCards

    fun loadCards(setId: String) {
        viewModelScope.launch {
            try {
                val cardList = repository.getCards(setId)
                _cards.postValue(cardList)
            } catch (e: Exception) {
                Log.e(TAG, "Error when loading cards: $e")
            }
        }
    }


    fun loadLastReadPage(userId: String, setId: String) {
        viewModelScope.launch {
            try {
                val page = repository.getLastReadPage(userId, setId)
                _lastReadPage.postValue(page)
            } catch (e: Exception) {
                Log.e(TAG, "Error when loading LastReadPage: $e")
            }
        }
    }

    fun saveLastReadPage(userId: String, setId: String, lastReadPage: Long) {
        viewModelScope.launch {
            try {
                repository.saveLastReadPage(userId, setId, lastReadPage)
            } catch (e: Exception) {
                Log.e(TAG, "Error when saving LastReadPage: $e")
            }
        }
    }


    fun loadQuizCards(setId: String) {
        viewModelScope.launch {
            try {
                val cardList = repository.getCards(setId)
                if (cardList.isNotEmpty()) {
                    val quizCardList = cardList.map { card ->
                        val correctAnswer = card.definition ?: ""

                        val prompt = "Generiere nur die falschen Antwortmöglichkeiten für folgendes Quiz:\n" +
                                "Begriff: ${card.word} (Definition: ${correctAnswer})\n" +
                                "Erstelle 3 plausible, aber falsche Antwortmöglichkeiten, die sich deutlich von der korrekten Antwort unterscheiden. \n" +
                                "Gib die falschen Antworten ohne jegliche Markierungen oder Trennzeichen wie Kommas an. \n" +
                                "Trenne jede falsche Antwort durch einen Zeilenumbruch.\n" +
                                "Beispiel: \n" +
                                "Begriff: Ein Geschäft, das Fleisch und Wurst verkauft.\n" +
                                "Definition: die Metzgerei\n" +
                                "Deine Antwort:\n" +
                                "das Theater\n" +
                                "die Autowerkstatt\n" +
                                "der Friseur\n" +
                                "Ein Weiteres Beispiel könnte so Aussehen: \n"+
                                "Begriff: die Metzgerei\n" +
                                "Definition: Ein Geschäft, das Fleisch und Wurst verkauft.\n" +
                                "Deine Antwort:\n" +
                                "Ein Gebäude oder ein Ort, an dem Bühnenstücke, Opern, Konzerte oder andere darstellende Künste aufgeführt werden.\n" +
                                "Ein Betrieb, in dem Fahrzeuge repariert und gewartet werden.\n" +
                                "Ein Betrieb, in dem Brot, Brötchen, Kuchen und andere Backwaren hergestellt und verkauft werden.\n"

                        val generatedResponse = generativeModel.generateContent(prompt)

                        Log.i(TAG, generatedResponse.text.toString())

                        val incorrectAnswers = generatedResponse.text?.split("\n")?.take(3)?.filter { it.isNotBlank() } ?: emptyList()

                        val answers = (incorrectAnswers + correctAnswer).shuffled()
                        val correctAnswerIndex = answers.indexOf(correctAnswer)

                        QuizCard(
                            question = card.word ?: "",
                            answers = answers,
                            correctAnswerIndex = correctAnswerIndex
                        )
                    }
                    _quizCards.postValue(quizCardList)
                } else {
                    _quizCards.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error when loading quiz cards: $e")
            }
        }
    }

    fun shuffleCards(setId: String) {
        viewModelScope.launch {
            try {
                val cardList = repository.getCards(setId)
                val shuffledList = cardList.shuffled()
                _cards.postValue(shuffledList)
            } catch (e: Exception) {
                Log.e(TAG, "Error when shuffling cards: $e")
            }
        }
    }


}

