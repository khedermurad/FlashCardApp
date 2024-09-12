package com.example.flipcards.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcards.R
import com.example.flipcards.adapters.SetsAdapter
import com.example.flipcards.databinding.FragmentHomeBinding
import com.example.flipcards.viewmodel.AuthViewModel
import com.example.flipcards.viewmodel.AuthViewModelFactory
import com.example.flipcards.viewmodel.HomeViewModel
import com.example.flipcards.viewmodel.HomeViewModelFactory
import com.example.flipcards.viewmodel.SetsViewModel
import com.example.flipcards.viewmodel.SetsViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var setsViewModel: SetsViewModel
    private lateinit var authViewModel: AuthViewModel


    private lateinit var adapter: SetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = MyApp.appModule.firebaseAuth
        val userId = auth.currentUser?.uid ?: return

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(MyApp.appModule.homeRepository, userId))[HomeViewModel::class.java]

        setsViewModel = ViewModelProvider(this, SetsViewModelFactory(MyApp.appModule.setRepository))[SetsViewModel::class.java]

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(MyApp.appModule.authRepository))[AuthViewModel::class.java]


        adapter = SetsAdapter(requireContext(), emptyList(), emptyList(), userId, "HomeFragment", null, setsViewModel)
        binding.rvOwnPosts.adapter = adapter
        binding.rvOwnPosts.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.setsWithDocIds.observe(viewLifecycleOwner, Observer { setsWithDocIds ->
            val sets = setsWithDocIds.map { it.first }
            val docIds = setsWithDocIds.map { it.second }
            Log.d("HomeFragment", "Sets size: ${sets.size}, DocIds size: ${docIds.size}")
            adapter.updateSets(sets, docIds)
        })

        binding.homeSearchView.clearFocus()
        binding.homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()){
                    homeViewModel.searchSets(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        homeViewModel.searchResults.observe(viewLifecycleOwner, Observer { setsWithDocIds ->
            val sets = setsWithDocIds.map { it.first }
            val docIds = setsWithDocIds.map { it.second }
            adapter.updateSets(sets, docIds)
        })

        binding.profileBtn.setOnClickListener{
            val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_profile, null)
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()

            val user = MyApp.appModule.firebaseAuth.currentUser
            val email = user?.email.toString()
            dialogView.findViewById<TextView>(R.id.emailTextView).text = email

            dialogView.findViewById<AppCompatButton>(R.id.logout_btn).setOnClickListener{
                authViewModel.signOut()
            }

            dialogView.findViewById<AppCompatButton>(R.id.settings_btn).setOnClickListener {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }

            authViewModel.signOutResult.observe(viewLifecycleOwner, Observer {
                if (it.isSuccess) {
                    Toast.makeText(requireContext(), "Erfolgreich abgemeldet", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), it.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
                }
            })

            dialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

