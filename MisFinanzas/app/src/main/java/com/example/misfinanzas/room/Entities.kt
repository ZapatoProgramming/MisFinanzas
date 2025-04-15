package com.example.misfinanzas.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Date? = null,
    val created_at: Date? = null,
    var solved: Boolean = false,
    var synced: Boolean = false
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String = "",
    val userId: String,
    val type: String,
    val amount: Double,
    val category: String,
    val start_date: Date,
    val frequency: String,
    val next_payment_date: Date,
    val created_at: Date = Date(),
    var synced: Boolean = false
)

@Entity(tableName = "balances")
data class BalanceEntity(
    @PrimaryKey val userId: String = "",
    val current_balance: Double = 0.0,
    val last_updated: Date = Date()
)