package com.cashflow.app.ui.auth

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val userEmail: String? = null
)

sealed class AuthIntent {
    data class SignIn(val email: String, val password: String) : AuthIntent()
    data class SignUp(val email: String, val password: String) : AuthIntent()
    object SignOut : AuthIntent()
    object ClearError : AuthIntent()
}

