package com.example.misfinanzas.viewModels.signup

import androidx.lifecycle.ViewModel
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.models.SignUpModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

}