package com.example.misfinanzas.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.misfinanzas.models.Subscription
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE userId = :userId")
    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND synced = 0")
    suspend fun getUnsyncedTransactions(userId: String): List<TransactionEntity>

    @Query("UPDATE transactions SET synced = 1 WHERE id = :transactionId")
    suspend fun markTransactionAsSynced(transactionId: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY next_payment_date ASC")
    fun getSubscriptionsByUserId(userId: String): Flow<List<SubscriptionEntity>>

    @Query("DELETE FROM subscriptions WHERE id = :subscriptionId")
    suspend fun deleteSubscriptionById(subscriptionId: String)

    @Query("UPDATE subscriptions SET next_payment_date = :nextPaymentDate WHERE id = :subscriptionId")
    suspend fun updateNextPaymentDate(subscriptionId: String, nextPaymentDate: Date)

    @Query("SELECT * FROM subscriptions WHERE userId = :userId AND synced = 0")
    suspend fun getUnsyncedSubscriptions(userId: String): List<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions WHERE userId = :userId")
    fun getAllSubscriptions(userId: String): Flow<List<SubscriptionEntity>>

    @Query("UPDATE subscriptions SET synced = 1 WHERE id = :subscriptionId")
    suspend fun markSubscriptionAsSynced(subscriptionId: String)

}

@Dao
interface BalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: BalanceEntity)

    @Query("UPDATE balances SET current_balance = :newBalance WHERE userId = :userId")
    suspend fun updateBalance(userId: String, newBalance: Double)

    @Query("SELECT * FROM balances WHERE userId = :userId")
    suspend fun getBalanceByUserId(userId: String): BalanceEntity?

    @Query("SELECT * FROM balances")
    fun getBalances(): Flow<List<BalanceEntity>>

}