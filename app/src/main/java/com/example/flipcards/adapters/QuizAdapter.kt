package com.example.flipcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flipcards.R
import com.example.flipcards.model.QuizCard

class QuizAdapter(private var cards: List<QuizCard>) : RecyclerView.Adapter<QuizAdapter.ViewPagerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }

    fun updateCards(newCards: List<QuizCard>) {
        cards = newCards
        notifyDataSetChanged()
    }

    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val quizQuestionTv : TextView = itemView.findViewById(R.id.quizQuestionTv)
        private val quizAnswer1Tv : TextView = itemView.findViewById(R.id.quizAnswer1Tv)
        private val quizAnswer2Tv : TextView = itemView.findViewById(R.id.quizAnswer2Tv)
        private val quizAnswer3Tv : TextView = itemView.findViewById(R.id.quizAnswer3Tv)
        private val quizAnswer4Tv : TextView = itemView.findViewById(R.id.quizAnswer4Tv)
        private val answerTextViews: List<TextView> = listOf(quizAnswer1Tv, quizAnswer2Tv, quizAnswer3Tv, quizAnswer4Tv)

        fun bind(card: QuizCard) {
            quizQuestionTv.text = card.question

            val answers = card.answers + List(4 - card.answers.size) { "leer" }

            quizAnswer1Tv.text = answers[0]
            quizAnswer2Tv.text = answers[1]
            quizAnswer3Tv.text = answers[2]
            quizAnswer4Tv.text = answers[3]

            answerTextViews.forEach { textView ->
                textView.background = ContextCompat.getDrawable(itemView.context, R.drawable.rounded_background)
                textView.isClickable = true
            }

            answerTextViews.forEachIndexed { index, textView ->
                textView.setOnClickListener {
                    if (index == card.correctAnswerIndex) {
                        textView.background = ContextCompat.getDrawable(itemView.context, R.drawable.quiz_correct)
                        answerTextViews.forEach { it.isClickable = false }
                    } else {
                        textView.background = ContextCompat.getDrawable(itemView.context, R.drawable.quiz_incorrect)
                    }
                }
            }
        }

    }

}