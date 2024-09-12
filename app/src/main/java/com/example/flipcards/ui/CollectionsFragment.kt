package com.example.flipcards.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcards.adapters.CollectionsAdapter
import com.example.flipcards.databinding.FragmentCollectionsBinding
import com.example.flipcards.viewmodel.CollAdapterViewModel
import com.example.flipcards.viewmodel.CollAdapterViewModelFactory
import com.example.flipcards.viewmodel.CollectionsViewModel
import com.example.flipcards.viewmodel.CollectionsViewModelFactory


private const val TAG = "CollectionsFragment"

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var collectionsViewModel: CollectionsViewModel
    private lateinit var collectionsAdapterViewModel: CollAdapterViewModel

    private lateinit var adapter: CollectionsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = MyApp.appModule.firebaseAuth
        val userId = auth.currentUser?.uid ?: return

        collectionsViewModel = ViewModelProvider(this, CollectionsViewModelFactory(MyApp.appModule.collectionRepository, userId))[CollectionsViewModel::class.java]
        collectionsAdapterViewModel = ViewModelProvider(this, CollAdapterViewModelFactory(MyApp.appModule.collAdapterRepository))[CollAdapterViewModel::class.java]

        adapter = CollectionsAdapter(requireContext(), emptyList(), userId, collectionsAdapterViewModel)
        binding.collectionsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.collectionsRv.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(binding.collectionsRv.context, LinearLayoutManager.VERTICAL)
        binding.collectionsRv.addItemDecoration(dividerItemDecoration)


        collectionsViewModel.collections.observe(viewLifecycleOwner, Observer { collections ->
            Log.d("CollectionsFragment", "Observed collections: $collections")
            adapter.updateCollections(collections)
        })

        binding.addImageButton.setOnClickListener{
            showNewCollectionDialog()
        }

    }

    private fun showNewCollectionDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Neue Collection")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val title = input.text.toString()
            if (title.isNotEmpty()) {
                collectionsViewModel.saveCollection(title)
            } else {
                Toast.makeText(context, "Titel darf nicht leer sein.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Abbrechen") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }




    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}