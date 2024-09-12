package com.example.flipcards.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.flipcards.adapters.CardTestAdapter
import com.example.flipcards.databinding.ActivityCardTestBinding
import com.example.flipcards.viewmodel.CardsViewModel
import com.example.flipcards.viewmodel.CardsViewViewModelFactory

class CardTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardTestBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var adapter: CardTestAdapter

    private var correctAnswers = 0
    private var incorrectAnswers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCardTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cardsViewModel = ViewModelProvider(
            this,
            CardsViewViewModelFactory(MyApp.appModule.cardsRepository, MyApp.appModule.generativeModel)
        )[CardsViewModel::class.java]


        adapter = CardTestAdapter(emptyList(), binding.cardTestViewPager, onAnswerChecked = { isCorrect ->
            if (isCorrect) correctAnswers++ else incorrectAnswers++
        }, onLastCardChecked = {
            showResults()
        })

        binding.cardTestViewPager.adapter = adapter
        binding.cardTestViewPager.isUserInputEnabled = false

        progressBar = binding.cardTestProgressBar

        val docID = intent.getStringExtra("setId")

        cardsViewModel.shuffleCards(docID!!)
        cardsViewModel.cards.observe(this, Observer { cards ->
            adapter.updateCards(cards)
            progressBar.max = cards.size
            adapter.notifyDataSetChanged()
        })

        binding.cardTestViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                progressBar.progress = position + 1
            }
        })


    }

    private fun showResults() {
        binding.cardTestViewPager.visibility = View.GONE
        binding.resultLayout.visibility = View.VISIBLE
        binding.correctAnswersTv.text = "Richtig beantwortet: $correctAnswers"
        binding.incorrectAnswersTv.text = "Falsch beantwortet: $incorrectAnswers"

        binding.repeatTestBtn.setOnClickListener {
            correctAnswers = 0
            incorrectAnswers = 0
            adapter.resetCards()
            binding.cardTestViewPager.currentItem = 0
            binding.cardTestViewPager.visibility = View.VISIBLE
            binding.resultLayout.visibility = View.GONE
            progressBar.progress = 1
        }

        binding.finishTestBtn.setOnClickListener {
            finish()
        }


    }
}