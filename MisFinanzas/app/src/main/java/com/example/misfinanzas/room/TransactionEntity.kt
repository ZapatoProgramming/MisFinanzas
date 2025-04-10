package com.example.misfinanzas.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Date? = null,
    val created_at: Long = 0,
    var solved: Boolean = false
)