package com.example.misfinanzas.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {

    private val auth: FirebaseAuth = Firebase.auth

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Pair<Boolean, String?> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            Pair(true, null)
            true
            Pair(false, "El usuario no existe o está deshabilitado.")
        } catch (e: Exception) {
            Pair(false, "Error desconocido: ${e.message}")
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Pair<Boolean, String?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Pair(true, null) // Éxito, sin mensaje de error
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Pair(false, "Correo electrónico o contraseña incorrectos.")
        } catch (e: FirebaseAuthInvalidUserException) {
            Pair(false, "El usuario no existe o está deshabilitado.")
        } catch (e: Exception) {
            Pair(false, "Error desconocido: ${e.message}")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}