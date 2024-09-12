package com.example.flipcards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flipcards.model.AuthRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    private val _signOutResult = MutableLiveData<Result<Boolean>>()
    val signOutResult: LiveData<Result<Boolean>> get() = _signOutResult

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = authRepository.signIn(email, password)
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = authRepository.signUp(email, password)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _signOutResult.value = authRepository.signOut()
        }
    }

    fun isUserSignedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }

}