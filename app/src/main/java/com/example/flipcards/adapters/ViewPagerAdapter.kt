package com.example.flipcards.adapters

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flipcards.R
import com.example.flipcards.model.CardData

class ViewPagerAdapter(private var cards: List<CardData>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
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

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordTextView: TextView = itemView.findViewById(R.id.card_word)
        private val definitionTextView: TextView = itemView.findViewById(R.id.card_definition)

        fun bind(card: CardData) {
            wordTextView.movementMethod = ScrollingMovementMethod()
            definitionTextView.movementMethod = ScrollingMovementMethod()

            wordTextView.text = card.word
            definitionTextView.text = card.definition
            itemView.setOnClickListener {
                if (wordTextView.visibility == View.VISIBLE) {
                    wordTextView.visibility = View.GONE
                    definitionTextView.visibility = View.VISIBLE
                } else {
                    wordTextView.visibility = View.VISIBLE
                    definitionTextView.visibility = View.GONE
                }
            }
        }
    }
}

