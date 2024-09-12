package com.example.flipcards.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.flipcards.R
import com.example.flipcards.model.CardData
import com.google.android.material.textfield.TextInputEditText


private const val THRESHOLD = 4
class CardTestAdapter(
    private var cards: List<CardData>, private val viewPager2: ViewPager2,
    private val onAnswerChecked: (Boolean) -> Unit,
    private val onLastCardChecked: () -> Unit
) : RecyclerView.Adapter<CardTestAdapter.ViewPagerViewHolder>() {

    var isRepeated = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_test_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }

    fun updateCards(newCards: List<CardData>) {
        cards = newCards
        notifyDataSetChanged()
    }

    fun resetCards() {
        isRepeated = true
        notifyDataSetChanged()
    }

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.ctQuestionTv)
        private val answerTextInput: TextInputEditText = itemView.findViewById(R.id.ctAnswerTi)
        private val answerTextView: TextView = itemView.findViewById(R.id.ctAnswerTv)
        private val showAnswerButton: Button = itemView.findViewById(R.id.showAnswerBtn)
        private val testButton: Button = itemView.findViewById(R.id.testBtn)

        private var isAnswerVisible = false
        private var tested = false


        @SuppressLint("SetTextI18n")
        fun bind(card: CardData) {
            if(isRepeated){
                resetState()
            }


            questionTextView.text = card.word


            showAnswerButton.setOnClickListener {
                if (isAnswerVisible) {
                    answerTextView.text = ""
                    showAnswerButton.text = "Antwort anzeigen"
                } else {
                    answerTextView.text = card.definition
                    showAnswerButton.text = "Antwort ausblenden"
                }
                isAnswerVisible = !isAnswerVisible
            }

            testButton.setOnClickListener {
                if (tested) {
                    val nextPosition = bindingAdapterPosition + 1
                    if (nextPosition < cards.size) {
                        viewPager2.currentItem = nextPosition
                        testButton.text = "Überprüfen"
                    }else{
                        onLastCardChecked()
                    }
                } else {
                    val levenshteinDistance = levenshtein(answerTextInput.text.toString().trim().lowercase(),
                        card.definition?.trim()
                            ?.lowercase() ?: ""
                    )

                    val isCorrect = levenshteinDistance <= THRESHOLD

                    if (isCorrect) {
                        answerTextInput.setTextColor(Color.parseColor("#09ad14"))
                    } else {
                        answerTextInput.setTextColor(Color.parseColor("#f50a0a"))
                    }

                    testButton.text = "Nächste Karte"
                    onAnswerChecked(isCorrect)
                }
                tested = !tested
            }
        }

        private fun resetState() {
            answerTextInput.setText("")
            answerTextInput.setTextColor(Color.parseColor("#FF1D1B20"))
            answerTextView.text = ""
            showAnswerButton.text = "Antwort anzeigen"
            testButton.text = "Überprüfen"
            isAnswerVisible = false
            tested = false
        }

        fun levenshtein(a: String, b: String): Int {
            val dp = Array(a.length + 1) { IntArray(b.length + 1) }
            for (i in 0..a.length) dp[i][0] = i
            for (j in 0..b.length) dp[0][j] = j

            for (i in 1..a.length) {
                for (j in 1..b.length) {
                    val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                    dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
                }
            }

            return dp[a.length][b.length]
        }

    }
}
