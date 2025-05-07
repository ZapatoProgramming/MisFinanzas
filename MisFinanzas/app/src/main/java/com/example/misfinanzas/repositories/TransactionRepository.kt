package com.example.misfinanzas.repositories

import com.example.misfinanzas.models.Balance
import com.example.misfinanzas.models.Category
import com.example.misfinanzas.models.Subscription
import com.example.misfinanzas.models.Transaction
import com.example.misfinanzas.models.UserData
import com.example.misfinanzas.room.BalanceEntity
import com.example.misfinanzas.room.CategoryEntity
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import com.example.misfinanzas.utils.FirestoreUtils
import java.util.Calendar
import java.util.Date

class TransactionRepository {
    suspend fun fetchTransactions(userId: String): List<Transaction>? {
        val collectionName = "User/${userId}/Transactions"
        return FirestoreUtils.fetchCollectionAs<Transaction>(collectionName)
    }

    suspend fun fetchSubscriptions(userId: String): List<Subscription>? {
        val collectionName = "User/${userId}/Subscriptions"
        return FirestoreUtils.fetchCollectionAs<Subscription>(collectionName)
    }

    suspend fun uploadTransaction(userId: String, transactionEntity: TransactionEntity): Boolean {
        val transaction = entityToDocument(transactionEntity)
        return FirestoreUtils.uploadDocument(
            collectionName = "User/$userId/Transactions",
            documentId = transactionEntity.id,
            data = transaction
        )
    }

    suspend fun uploadSubscription(userId: String, subscriptionEntity: SubscriptionEntity): Boolean {
        val subscription = entityToDocument(subscriptionEntity)
        return FirestoreUtils.uploadDocument(
            collectionName = "User/$userId/Subscriptions",
            documentId = subscriptionEntity.id,
            data = subscription
        )
    }

    suspend fun updateBalance(userId: String, newBalance: Balance): Boolean {
        return FirestoreUtils.uploadDocument("Balance", userId, newBalance)
    }

    suspend fun updateUserField(userId: String, fieldName: String, fieldValue: Any): Boolean {
        return FirestoreUtils.updateField("User", userId, fieldName, fieldValue)
    }

    suspend fun fetchUserData(userId: String): UserData? {
        return FirestoreUtils.fetchDocumentAs<UserData>("User", userId)
    }

    suspend fun fetchBalanceFromFirestore(userId: String): Balance? {
        return FirestoreUtils.fetchDocumentAs<Balance>("Balance", userId)
    }

    fun calculateNextPaymentDate(startDate: Date, frequency: String): Date {
        val calendar = Calendar.getInstance().apply { time = startDate }
        when (frequency) {
            "Mensual" -> calendar.add(Calendar.MONTH, 1)
            "Anual" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.time
    }

    fun validateAmount(amount: String): Double {
        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
        if (parsedAmount <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a cero.")
        }
        return parsedAmount
    }

    fun getTransactionDate(isToday: Boolean, day: String, month: String, year: String): Date {
        return if (isToday) {
            Calendar.getInstance().time
        } else {
            if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                throw IllegalArgumentException("Por favor, completa la fecha.")
            }
            Calendar.getInstance().apply {
                set(year.toInt(), month.toInt() - 1, day.toInt())
            }.time
        }
    }

    fun isDateInFuture(date: Date): Boolean {
        val currentDate = Calendar.getInstance().time
        return date.after(currentDate)
    }

    fun entityToDocument(entity: Any) : Any {
        return when(entity){
            is TransactionEntity -> {
                Transaction(
                    id = entity.id,
                    type = entity.type,
                    amount = entity.amount,
                    category = entity.category,
                    description = entity.description,
                    date = entity.date,
                    created_at = entity.created_at,
                    solved = entity.solved
                )
            }
            is SubscriptionEntity -> {
                Subscription(
                    id = entity.id,
                    type = entity.type,
                    amount = entity.amount,
                    category = entity.category,
                    description = entity.description,
                    start_date = entity.start_date,
                    frequency = entity.frequency,
                    next_payment_date = entity.next_payment_date,
                    created_at = entity.created_at
                )
            }
            is BalanceEntity -> {
                Balance(
                    current_balance = entity.current_balance,
                    last_updated = entity.last_updated
                )
            }

            is CategoryEntity -> { // <-- Nuevo caso: Categoría
                Category(
                    name = entity.name,
                    color = entity.color,
                    description = entity.description,
                )
            }
            else -> throw IllegalArgumentException("Entidad no existe")
        }
    }

    fun documentToEntity(document: Any, userId: String): Any? {
        return when (document) {
            is Transaction -> {
                TransactionEntity(
                    id = document.id,
                    userId = userId,
                    type = document.type,
                    amount = document.amount,
                    category = document.category,
                    description = document.description,
                    date = document.date,
                    created_at = document.created_at,
                    solved = document.solved,
                    synced = true
                )
            }

            is Subscription -> {
                val startDate = document.start_date ?: return null
                val nextPaymentDate = document.next_payment_date ?: return null
                val createdAt = document.created_at ?: return null

                SubscriptionEntity(
                    userId = userId,
                    id = document.id,
                    type = document.type,
                    amount = document.amount,
                    category = document.category,
                    description = document.description,
                    frequency = document.frequency,
                    start_date = startDate,
                    next_payment_date = nextPaymentDate,
                    created_at = createdAt,
                    synced = true
                )
            }

            is Category -> { // <-- Nuevo caso: Categoría
                CategoryEntity(
                    id = document.id,
                    name = document.name,
                    color = document.color,
                    description = document.description,
                    userId = userId,
                    synced = true
                )
            }

            else -> {
                null
            }
        }
    }

    suspend fun fetchCategories(userId: String) : List<Category>? {
        val collectionName = "User/${userId}/Categories"
        return FirestoreUtils.fetchCollectionAs<Category>(collectionName)
    }
}