package com.example.misfinanzas.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseAuthService {

    private val auth: FirebaseAuth = Firebase.auth

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Pair<Boolean, String?> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, "Error desconocido: ${e.message}")
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Pair<Boolean, String?> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Pair(true, null)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Pair(false, "Correo electrónico o contraseña incorrectos. ${e.message}")
        } catch (e: FirebaseAuthInvalidUserException) {
            Pair(false, "El usuario no existe o está deshabilitado. ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Error desconocido: ${e.message}")
        }
    }

}