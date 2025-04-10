package com.example.misfinanzas.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.models.LoginModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authService = FirebaseAuthService()

    private val _loginForm = MutableStateFlow(LoginModel())
    val loginForm: StateFlow<LoginModel> = _loginForm

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage: StateFlow<String?> = _loginMessage

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun updateEmail(email: String) {
        _loginForm.value = _loginForm.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _loginForm.value = _loginForm.value.copy(password = password)
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun login() {
        _isLoading.value = true
        viewModelScope.launch {
            val email = _loginForm.value.email
            val password = _loginForm.value.password

            if (email.isEmpty() || password.isEmpty()) {
                _loginMessage.value = "Por favor, complete todos los campos."
                return@launch
            }

            val deferredResult = async { authService.signInWithEmailAndPassword(email, password) }
            val (success, errorMessage) = deferredResult.await()
            if (success) {
                _loginMessage.value = "Inicio de sesión exitoso."
                _loginSuccess.value = true
            } else {
                _loginMessage.value = errorMessage ?: "Error en el inicio de sesión."
                _loginSuccess.value = false
            }

            _isLoading.value = false

        }
    }
}