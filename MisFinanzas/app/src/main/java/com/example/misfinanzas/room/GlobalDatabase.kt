package com.example.misfinanzas.room

import android.content.Context

object GlobalDatabase {
    private lateinit var appDatabase: AppDatabase

    fun initialize(context: Context) {
        appDatabase = AppDatabase.getInstance(context)
    }

    // Proporciona acceso al DAO
    val transactionDao: TransactionDao
        get() = appDatabase.transactionDao()

    val balanceDao: BalanceDao
        get() = appDatabase.balanceDao()

    val subscriptionDao: SubscriptionDao
        get() = appDatabase.subscriptionDao()
}