package com.example.misfinanzas.models

data class SignUpModel (
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val lastName: String = ""
)

data class UserDataSignUp(
    val name: String = "",
    val last_name: String = "",
    val has_entered_balance: Boolean = false,
    val has_added_first_transaction: Boolean = false,
    val created_at: Any? = null
)