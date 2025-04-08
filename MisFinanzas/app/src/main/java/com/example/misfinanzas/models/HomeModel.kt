package com.example.misfinanzas.models

import com.google.firebase.firestore.FieldValue

data class UserData(
    val name: String = "",
    val last_name: String = "",
    val has_entered_balance: Boolean = false,
    val has_added_first_transaction: Boolean = false
)

data class Balance(
    val current_balance: Double = 0.0
)

data class Transaction(
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "",
    val is_subscription: Boolean = false,
    val frequency: String? = null,
    val created_at: Any? = FieldValue.serverTimestamp()
)
