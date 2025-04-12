package com.example.misfinanzas.repositories

import com.example.misfinanzas.room.BalanceEntity
import com.example.misfinanzas.room.GlobalDatabase
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomRepository @Inject constructor() {

    val transactionDao = GlobalDatabase.transactionDao
    val balanceDao = GlobalDatabase.balanceDao
    val subscriptionDao = GlobalDatabase.subscriptionDao

    suspend fun insertBalance(balance: BalanceEntity) {
        balanceDao.insertBalance(balance)
    }

    suspend fun getBalanceByUserId(userId: String): BalanceEntity? {
        return balanceDao.getBalanceByUserId(userId)
    }

    fun getBalances(): Flow<List<BalanceEntity>>{
        return balanceDao.getBalances()
    }

    suspend fun updateBalance(userId: String, newBalance: Double) {
        balanceDao.updateBalance(userId, newBalance)
    }

    // Insert a transaction
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    // Get all transactions
    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions(userId)
    }

    suspend fun deleteTransactions(){
        transactionDao.deleteAllTransactions()
    }

    suspend fun insertSubscription(subscriptionEntity: SubscriptionEntity){
        subscriptionDao.insertSubscription(subscriptionEntity)
    }

    fun getAllSubscriptions(userId: String): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAllSubscriptions(userId)
    }
}