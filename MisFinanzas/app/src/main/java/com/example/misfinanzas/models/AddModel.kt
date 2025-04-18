package com.example.misfinanzas.models

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
    val description: String = "",
    val date: Date? = null,
    val created_at: Date? = null,
    var solved: Boolean = false
)

data class Subscription(
    val id: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val start_date: Date? = null,
    val frequency: String = "",
    val next_payment_date: Date? = null,
    val created_at: Date? = null
)