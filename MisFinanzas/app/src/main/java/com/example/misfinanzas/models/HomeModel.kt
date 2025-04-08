package com.example.misfinanzas.models

data class UserData(
    val name: String = "",
    val lastName: String = "",
    val hasEnteredBalance: Boolean = false,
    val hasAddedFirstExpense: Boolean = false
)
