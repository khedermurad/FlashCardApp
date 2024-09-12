package com.example.flipcards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcards.adapters.SetsAdapter
import com.example.flipcards.databinding.FragmentSearchBinding
import com.example.flipcards.viewmodel.SearchViewModel
import com.example.flipcards.viewmodel.SearchViewModelFactory
import com.example.flipcards.viewmodel.SetsViewModel
import com.example.flipcards.viewmodel.SetsViewModelFactory


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var setsViewModel: SetsViewModel

    private lateinit var adapter: SetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = MyApp.appModule.firebaseAuth
        val userId = auth.currentUser?.uid ?: return

        searchViewModel = ViewModelProvider(this, SearchViewModelFactory(MyApp.appModule.searchRepository))[SearchViewModel::class.java]
        setsViewModel = ViewModelProvider(this, SetsViewModelFactory(MyApp.appModule.setRepository))[SetsViewModel::class.java]

        adapter = SetsAdapter(requireContext(), emptyList(), emptyList(), userId, "SearchFragment", null, setsViewModel)
        binding.searchRv.adapter = adapter
        binding.searchRv.layoutManager = LinearLayoutManager(requireContext())

        binding.searchSearchView.clearFocus()
        binding.searchSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchViewModel.searchSets(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchViewModel.searchResults.observe(viewLifecycleOwner, Observer { setsWithDocIds ->
            val sets = setsWithDocIds.map { it.first }
            val docIds = setsWithDocIds.map { it.second }
            adapter.updateSets(sets, docIds)
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}