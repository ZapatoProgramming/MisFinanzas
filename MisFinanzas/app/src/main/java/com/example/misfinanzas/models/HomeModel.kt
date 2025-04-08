package com.example.misfinanzas.models

data class UserData(
    val name: String = "",
    val last_name: String = "",
    val has_entered_balance: Boolean = false,
    val has_added_first_transaction: Boolean = false
)

data class Balance(
    val current_balance: Double = 0.0
)
