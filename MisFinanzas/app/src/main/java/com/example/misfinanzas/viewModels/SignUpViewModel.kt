package com.example.misfinanzas.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.models.Category
import com.example.misfinanzas.models.SignUpModel
import com.example.misfinanzas.models.UserDataSignUp
import com.example.misfinanzas.utils.FirestoreUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SignUpViewModel: ViewModel() {

    private val authService = FirebaseAuthService()

    private val _signupForm = MutableStateFlow(SignUpModel())
    val signupForm: StateFlow<SignUpModel> = _signupForm

    private val _signupMessage = MutableStateFlow<String?>(null)
    val signupMessage: StateFlow<String?> = _signupMessage

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isPasswordVisibleTwo = MutableStateFlow(false)
    val isPasswordVisibleTwo: StateFlow<Boolean> = _isPasswordVisibleTwo

    private val _signUpSuccess = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun updateEmail(email: String) {
        _signupForm.value = _signupForm.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _signupForm.value = _signupForm.value.copy(password = password)
    }

    fun updatePasswordTwo(confirmPassword: String) {
        _signupForm.value = _signupForm.value.copy(confirmPassword = confirmPassword)
    }

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
        _isLoading.value = true
        viewModelScope.launch {
            val email = _signupForm.value.email
            val password = _signupForm.value.password

            if (email.isEmpty() || password.isEmpty()) {
                _signupMessage.value = "Por favor, complete todos los campos."
                return@launch
            }

            val deferredResult = async { authService.createUserWithEmailAndPassword(email, password) }
            val (success, errorMessage) = deferredResult.await()
            if (success) {
                _signupMessage.value = "Creacion de cuenta exitosa."
                _signUpSuccess.value = true
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val userData = UserDataSignUp(
                        name = _signupForm.value.name,
                        last_name = _signupForm.value.lastName,
                        has_entered_balance = false,
                        has_added_first_transaction = false
                    )
                    async {createUserDocument(userId, userData)}.await()
                    async {createDefaultCategoriesForUser(userId)}.await()
                }
            } else {
                _signupMessage.value = errorMessage ?: "Error en la creación de cuenta."
                _signUpSuccess.value = false
            }
            _isLoading.value = false
        }
    }

    private fun createUserDocument(userId: String, userData: UserDataSignUp) {
        viewModelScope.launch {
            try {
                val dataWithTimestamp = userData.copy(created_at = FieldValue.serverTimestamp())
                FirestoreUtils.uploadDocument("User", userId, dataWithTimestamp)
            } catch (e: Exception) {
                _signupMessage.value = "Error al crear el documento del usuario: ${e.message}"
            }
        }
    }

    private suspend fun createDefaultCategoriesForUser(userId: String) {
        val defaultCategories = listOf(
            Category(name = "Comida", color = "#FF6347", description = "Gastos relacionados con alimentos"),
            Category(name = "Transporte", color = "#4682B4", description = "Viajes, combustible o transporte público"),
            Category(name = "Salud", color = "#32CD32", description = "Medicamentos, consultas médicas"),
            Category(name = "Entretenimiento", color = "#8A2BE2", description = "Cine, videojuegos, etc."),
            Category(name = "Educación", color = "#FFD700", description = "Libros, cursos, colegiaturas")
        )

        // Subir cada categoría como documentos individuales en la subcolección Categories
        defaultCategories.forEach { category ->
            val categoryId = UUID.randomUUID().toString()
            FirestoreUtils.uploadDocument("User/${userId}/Categories", categoryId, category)
        }
    }
}