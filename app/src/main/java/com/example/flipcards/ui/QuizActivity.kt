package com.example.flipcards.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.flipcards.adapters.QuizAdapter
import com.example.flipcards.databinding.ActivityQuizBinding
import com.example.flipcards.viewmodel.CardsViewModel
import com.example.flipcards.viewmodel.CardsViewViewModelFactory

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var cardsViewModel: CardsViewModel


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cardsViewModel = ViewModelProvider(
            this,
            CardsViewViewModelFactory(MyApp.appModule.cardsRepository, MyApp.appModule.generativeModel)
        )[CardsViewModel::class.java]

        val adapter = QuizAdapter(emptyList())
        binding.quizViewPager.adapter = adapter

        progressBar = binding.quizProgressBar

        val docID = intent.getStringExtra("setId")

        cardsViewModel.loadQuizCards(docID!!)
        cardsViewModel.quizCards.observe(this, Observer { quizCards ->
            adapter.updateCards(quizCards)
            progressBar.max = quizCards.size
            adapter.notifyDataSetChanged()
        })


        binding.quizViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                progressBar.progress = position + 1
            }
        })


    }
}