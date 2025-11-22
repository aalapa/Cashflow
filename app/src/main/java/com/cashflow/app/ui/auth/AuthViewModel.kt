package com.cashflow.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    init {
        // Check if user is already signed in
        val currentUser = auth.currentUser
        _state.update {
            it.copy(
                isAuthenticated = currentUser != null,
                userEmail = currentUser?.email
            )
        }
        
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _state.update {
                it.copy(
                    isAuthenticated = user != null,
                    userEmail = user?.email
                )
            }
        }
    }
    
    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> {
                signIn(intent.email, intent.password)
            }
            is AuthIntent.SignUp -> {
                signUp(intent.email, intent.password)
            }
            is AuthIntent.SignOut -> {
                signOut()
            }
            is AuthIntent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }
    
    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // State will be updated by auth state listener
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Sign in failed"
                    )
                }
            }
        }
    }
    
    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                // State will be updated by auth state listener
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Sign up failed"
                    )
                }
            }
        }
    }
    
    private fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                // State will be updated by auth state listener
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Sign out failed")
                }
            }
        }
    }
}

