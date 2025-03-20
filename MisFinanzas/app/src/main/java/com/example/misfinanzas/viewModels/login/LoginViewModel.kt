package com.example.misfinanzas.viewModels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.models.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authService = FirebaseAuthService()

    // Estado del formulario de inicio de sesión
    private val _loginForm = MutableStateFlow(LoginModel())
    val loginForm: StateFlow<LoginModel> = _loginForm

    // Estado para manejar mensajes de error o éxito
    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage: StateFlow<String?> = _loginMessage

    // Estado para controlar la visibilidad de la contraseña
    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    // Actualizar el correo electrónico
    fun updateEmail(email: String) {
        _loginForm.value = _loginForm.value.copy(email = email)
    }

    // Actualizar la contraseña
    fun updatePassword(password: String) {
        _loginForm.value = _loginForm.value.copy(password = password)
    }

    // Alternar la visibilidad de la contraseña
    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    // Lógica para iniciar sesión
    fun login() {
        viewModelScope.launch {
            val email = _loginForm.value.email
            val password = _loginForm.value.password

            // Validaciones básicas
            if (email.isEmpty() || password.isEmpty()) {
                _loginMessage.value = "Por favor, complete todos los campos."
                return@launch
            }

            val (success, errorMessage) = authService.signInWithEmailAndPassword(email, password)
            if (success) {
                _loginMessage.value = "Inicio de sesión exitoso."
            } else {
                _loginMessage.value = errorMessage ?: "Error en el inicio de sesión."
            }

        }
    }
}