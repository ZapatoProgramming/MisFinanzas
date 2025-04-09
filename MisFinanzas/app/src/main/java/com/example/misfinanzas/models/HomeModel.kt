package com.example.misfinanzas.models

import com.google.firebase.firestore.FieldValue
import java.util.Date

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
    val date: Date? = null, // Fecha como Date
    val created_at: Any? = FieldValue.serverTimestamp()
)

data class Subscription(
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val start_date: Date? = null, // Fecha como Date
    val frequency: String = "",
    val next_payment_date: Date? = null, // Fecha como Date
    val created_at: Any? = FieldValue.serverTimestamp()
)
