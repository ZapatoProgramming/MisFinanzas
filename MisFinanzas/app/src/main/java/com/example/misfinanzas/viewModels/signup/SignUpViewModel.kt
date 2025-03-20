package com.example.misfinanzas.viewModels.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.models.SignUpModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {

    private val authService = FirebaseAuthService()

    // Estado del formulario de inicio de sesión
    private val _signupForm = MutableStateFlow(SignUpModel())
    val signupForm: StateFlow<SignUpModel> = _signupForm

    // Estado para manejar mensajes de error o éxito
    private val _signupMessage = MutableStateFlow<String?>(null)
    val signupMessage: StateFlow<String?> = _signupMessage

    // Estado para controlar la visibilidad de la contraseña
    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isPasswordVisibleTwo = MutableStateFlow(false)
    val isPasswordVisibleTwo: StateFlow<Boolean> = _isPasswordVisibleTwo

    // Actualizar el correo electrónico
    fun updateEmail(email: String) {
        _signupForm.value = _signupForm.value.copy(email = email)
    }

    // Actualizar la contraseña
    fun updatePassword(password: String) {
        _signupForm.value = _signupForm.value.copy(password = password)
    }

    fun updatePasswordTwo(confirmPassword: String) {
        _signupForm.value = _signupForm.value.copy(confirmPassword = confirmPassword)
    }

    // Alternar la visibilidad de la contraseña
    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun togglePasswordVisibilityTwo() {
        _isPasswordVisibleTwo.value = !_isPasswordVisibleTwo.value
    }

    fun updateName(name: String){
        _signupForm.value = _signupForm.value.copy(name = name)
    }

    fun updateLastName(lastName: String){
        _signupForm.value = _signupForm.value.copy(lastName = lastName)
    }

    fun signup(){
        viewModelScope.launch {
            val email = _signupForm.value.email
            val password = _signupForm.value.password

            // Validaciones básicas
            if (email.isEmpty() || password.isEmpty()) {
                _signupMessage.value = "Por favor, complete todos los campos."
                return@launch
            }

            val (success, errorMessage) = authService.createUserWithEmailAndPassword(email, password)
            if (success) {
                _signupMessage.value = "Creacion de cuenta exitosa."
            } else {
                _signupMessage.value = errorMessage ?: "Error en la creación de cuenta."
            }

        }
    }

}