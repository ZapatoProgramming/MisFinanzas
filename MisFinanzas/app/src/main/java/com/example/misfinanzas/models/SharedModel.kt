package com.example.misfinanzas.models

import java.util.Date

data class UserData(
    val name: String = "",
    val last_name: String = "",
    val has_entered_balance: Boolean = false,
    val has_added_first_transaction: Boolean = false
)

data class Balance(
    val current_balance: Double = 0.0,
    val last_updated: Date = Date()
)

data class Category(
    val id: String = "",
    val name: String = "",
    val color: String = "",
    val description: String = ""
)

