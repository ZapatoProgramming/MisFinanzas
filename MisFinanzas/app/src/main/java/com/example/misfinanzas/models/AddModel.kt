package com.example.misfinanzas.models

import com.google.firebase.firestore.FieldValue
import java.util.Calendar
import java.util.Date

object AddModel {
    fun getCurrentDate(): Triple<String, String, String> {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        return Triple(day, month, year)
    }
}

data class Transaction(
    val id: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Date? = null,
    val created_at: Any? = FieldValue.serverTimestamp(),
    var solved: Boolean = false
)

data class Subscription(
    val id: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val start_date: Date? = null,
    val frequency: String = "",
    val next_payment_date: Date? = null,
    val created_at: Any? = FieldValue.serverTimestamp(),
    val solved: Boolean = false
)