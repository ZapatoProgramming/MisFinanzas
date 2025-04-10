package com.example.misfinanzas.repositories

import com.example.misfinanzas.room.TransactionDao
import com.example.misfinanzas.room.TransactionEntity
import javax.inject.Inject

class RoomRepository @Inject constructor(private val transactionDao: TransactionDao) {

    // Insert a transaction
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    // Get all transactions
    suspend fun getAllTransactions(): List<TransactionEntity> {
        return transactionDao.getAllTransactions()
    }

    // Get a transaction by ID
    suspend fun getTransactionById(id: String): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }
}