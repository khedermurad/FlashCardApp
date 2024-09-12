package com.example.flipcards.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.databinding.ActivitySignInBinding
import com.example.flipcards.viewmodel.AuthViewModel
import com.example.flipcards.viewmodel.AuthViewModelFactory

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewModel = ViewModelProvider(this, AuthViewModelFactory(MyApp.appModule.authRepository))[AuthViewModel::class.java]

        if(authViewModel.isUserSignedIn()){
            goToMain()
        }

        binding.btnNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener {
            binding.btnSignin.isEnabled = false

            val email = binding.emailSignin.text.toString()
            val password = binding.passwordSignin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.signIn(email, password)
            } else {
                binding.btnSignin.isEnabled = true
                Toast.makeText(this, "Leere Felder sind nicht erlaubt.", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authResult.observe(this, Observer{
            binding.btnSignin.isEnabled = true
            if (it.isSuccess) {
                goToMain()
            } else {
                Toast.makeText(this, it.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}