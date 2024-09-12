package com.example.flipcards.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flipcards.databinding.ActivitySignUpBinding
import com.example.flipcards.viewmodel.AuthViewModel
import com.example.flipcards.viewmodel.AuthViewModelFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(MyApp.appModule.authRepository)
        )[AuthViewModel::class.java]


        binding.signinText.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignup.setOnClickListener {
            binding.btnSignup.isEnabled = false

            val email = binding.emailSignup.text.toString()
            val password = binding.passwordSignup.text.toString()
            val confirmPassword = binding.repeatPasswordSignup.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    authViewModel.signUp(email, password)
                } else {
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(
                        this,
                        "Kennwort und Bestätigung stimmen nicht überein.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.btnSignup.isEnabled = true
                Toast.makeText(this, "Leere Felder sind nicht erlaubt.", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authResult.observe(this, Observer {
            binding.btnSignup.isEnabled = true
            if (it.isSuccess) {
                Toast.makeText(this, "Erfolgreich angemeldet", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, it.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            }
        })


    }
}