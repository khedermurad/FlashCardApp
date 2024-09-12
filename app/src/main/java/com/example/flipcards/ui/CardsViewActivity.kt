package com.example.flipcards.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.flipcards.adapters.ViewPagerAdapter
import com.example.flipcards.databinding.ActivityCardsViewBinding
import com.example.flipcards.viewmodel.CardsViewModel
import com.example.flipcards.viewmodel.CardsViewViewModelFactory
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "CardsViewActivity"

class CardsViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardsViewBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var auth: FirebaseAuth

    private var lastSavedPage: Long = -1

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCardsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = MyApp.appModule.firebaseAuth
        cardsViewModel = ViewModelProvider(this, CardsViewViewModelFactory(MyApp.appModule.cardsRepository, MyApp.appModule.generativeModel))[CardsViewModel::class.java]

        val docId = intent.getStringExtra("setId") ?: return
        progressBar = binding.progressBar

        val adapter = ViewPagerAdapter(emptyList())
        binding.viewPager.adapter = adapter

        val userId = auth.currentUser?.uid ?: return

        cardsViewModel.loadCards(docId)
        cardsViewModel.cards.observe(this, Observer { cards ->
            adapter.updateCards(cards)
            progressBar.max = cards.size
            adapter.notifyDataSetChanged()
            Log.d(TAG, "Cards loaded: ${cards.size}")

            cardsViewModel.loadLastReadPage(userId, docId)
        })

        cardsViewModel.lastReadPage.observe(this, Observer { page ->
            if (page != null) {
                binding.viewPager.setCurrentItem(page.toInt(), false)
                Log.d(TAG, "Set last read page to: $page")
            } else {
                Log.d(TAG, "No saved last read page found")
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                progressBar.progress = position + 1
                Log.d(TAG, "Page selected: $position")

            }
        })

        setupNavigationButtons()
    }

    private fun setupNavigationButtons() {
        binding.nextButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < (binding.viewPager.adapter?.itemCount ?: 0) - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }

        binding.skipNextButton.setOnClickListener {
            binding.viewPager.currentItem = (binding.viewPager.adapter?.itemCount ?: 1) - 1
        }

        binding.prevButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }
        }

        binding.skipPrevButton.setOnClickListener {
            binding.viewPager.currentItem = 0
        }
    }


    override fun onPause() {
        super.onPause()
        saveCurrentPage()
    }

    override fun onStop() {
        super.onStop()
        saveCurrentPage()
    }

    private fun saveCurrentPage() {
        val docId = intent.getStringExtra("setId")
        val userId = auth.currentUser?.uid ?: return

        if (docId != null) {
            val currentPage = binding.viewPager.currentItem.toLong()

            if (currentPage != lastSavedPage) {
                Log.d(TAG, "Saving last read page: $currentPage")
                cardsViewModel.saveLastReadPage(userId, docId, currentPage)
                lastSavedPage = currentPage
            }
        } else {
            Log.e(TAG, "docId is null")
        }
    }


}
