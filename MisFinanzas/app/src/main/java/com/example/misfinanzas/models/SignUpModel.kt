package com.example.misfinanzas.models

data class SignUpModel (
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val lastName: String = ""
)