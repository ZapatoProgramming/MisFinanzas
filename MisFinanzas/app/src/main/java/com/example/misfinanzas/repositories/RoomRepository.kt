package com.example.misfinanzas.repositories

import com.example.misfinanzas.room.BalanceEntity
import com.example.misfinanzas.room.GlobalDatabase
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomRepository @Inject constructor() {

    private val transactionDao = GlobalDatabase.transactionDao
    private val balanceDao = GlobalDatabase.balanceDao
    private val subscriptionDao = GlobalDatabase.subscriptionDao

    suspend fun insertBalance(balance: BalanceEntity) {
        balanceDao.insertBalance(balance)
    }

    suspend fun getBalanceByUserId(userId: String): BalanceEntity? {
        return balanceDao.getBalanceByUserId(userId)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun getUnsyncedTransactions(userId: String) : List<TransactionEntity>{
        return transactionDao.getUnsyncedTransactions(userId)
    }

    suspend fun markTransactionAsSynced(transactionId: String){
        transactionDao.markTransactionAsSynced(transactionId)
    }

    suspend fun insertSubscription(subscriptionEntity: SubscriptionEntity){
        subscriptionDao.insertSubscription(subscriptionEntity)
    }

    fun getAllSubscriptions(userId: String): Flow<List<SubscriptionEntity>> {
        return subscriptionDao.getAllSubscriptions(userId)
    }

    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions(userId)
    }

    suspend fun getUnsyncedSubscriptions(userId: String) : List<SubscriptionEntity>{
        return subscriptionDao.getUnsyncedSubscriptions(userId)
    }

}