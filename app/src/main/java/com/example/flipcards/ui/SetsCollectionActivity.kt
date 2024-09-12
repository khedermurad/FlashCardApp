package com.example.flipcards.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcards.adapters.SetsAdapter
import com.example.flipcards.databinding.ActivitySetsCollectionBinding
import com.example.flipcards.viewmodel.SetsCollectionViewModel
import com.example.flipcards.viewmodel.SetsCollectionViewModelfactory
import com.example.flipcards.viewmodel.SetsViewModel
import com.example.flipcards.viewmodel.SetsViewModelFactory

class SetsCollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetsCollectionBinding

    private lateinit var setsCollectionViewModel: SetsCollectionViewModel
    private lateinit var setsViewModel: SetsViewModel


    private lateinit var adapter: SetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySetsCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        binding.setsCollectionTv.text = title
        val collectionId = intent.getStringExtra("collectionId")

        val auth = MyApp.appModule.firebaseAuth
        val userId = auth.currentUser?.uid ?: return

        setsCollectionViewModel = ViewModelProvider(this, SetsCollectionViewModelfactory(MyApp.appModule.setsCollectionRepository, userId, collectionId!!))[SetsCollectionViewModel::class.java]

        setsViewModel = ViewModelProvider(this, SetsViewModelFactory(MyApp.appModule.setRepository))[SetsViewModel::class.java]

        adapter = SetsAdapter(this, emptyList(), emptyList(), userId, "SetsCollectionActivity", collectionId, setsViewModel)
        binding.setsCollectionRv.adapter = adapter
        binding.setsCollectionRv.layoutManager = LinearLayoutManager(this)


        setsCollectionViewModel.setsWithDocIds.observe(this ,Observer { setsWithDocIds ->
            val sets = setsWithDocIds.map { it.first }
            val docIds = setsWithDocIds.map { it.second }
            Log.d("SetsCollection", "Sets size: ${sets.size}, DocIds size: ${docIds.size}")
            adapter.updateSets(sets, docIds)
        })

    }
}