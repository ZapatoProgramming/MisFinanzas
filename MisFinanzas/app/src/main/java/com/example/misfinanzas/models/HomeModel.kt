package com.example.misfinanzas.models

import com.example.misfinanzas.utils.FirestoreUtils
import com.google.firebase.firestore.FieldValue
import java.util.Calendar
import java.util.Date
import java.util.UUID

data class UserData(
    val name: String = "",
    val last_name: String = "",
    val has_entered_balance: Boolean = false,
    val has_added_first_transaction: Boolean = false
)

data class Balance(
    val current_balance: Double = 0.0
)

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

class TransactionRepository {

    // Subir una transacción a Firestore
    suspend fun uploadTransaction(userId: String, transaction: Transaction): Boolean {
        val transactionWithId = transaction.copy(id = UUID.randomUUID().toString())
        return FirestoreUtils.uploadDocument(
            collectionName = "User/$userId/Transactions",
            documentId = transactionWithId.id,
            data = transactionWithId
        )
    }

    // Subir una suscripción a Firestore
    suspend fun uploadSubscription(userId: String, subscription: Subscription): Boolean {
        val subscriptionWithId = subscription.copy(id = UUID.randomUUID().toString())
        return FirestoreUtils.uploadDocument(
            collectionName = "User/$userId/Subscriptions",
            documentId = subscriptionWithId.id,
            data = subscriptionWithId
        )
    }

    // Actualizar el saldo en Firestore
    suspend fun updateBalance(userId: String, newBalance: Double): Boolean {
        val balanceData = Balance(current_balance = newBalance)
        return FirestoreUtils.uploadDocument("Balance", userId, balanceData)
    }

    // Actualizar un campo específico en Firestore
    suspend fun updateUserField(userId: String, fieldName: String, fieldValue: Any): Boolean {
        return FirestoreUtils.updateField("User", userId, fieldName, fieldValue)
    }

    // Obtener datos del usuario desde Firestore
    suspend fun fetchUserData(userId: String): UserData? {
        return FirestoreUtils.fetchDocumentAs<UserData>("User", userId)
    }

    // Obtener el saldo del usuario desde Firestore
    suspend fun fetchBalance(userId: String): Balance? {
        return FirestoreUtils.fetchDocumentAs<Balance>("Balance", userId)
    }

    // Calcular la próxima fecha de pago
    fun calculateNextPaymentDate(startDate: Date, frequency: String): Date {
        val calendar = Calendar.getInstance().apply { time = startDate }
        when (frequency) {
            "Mensual" -> calendar.add(Calendar.MONTH, 1)
            "Anual" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.time
    }

    // Validar la cantidad de una transacción
    fun validateAmount(amount: String): Double {
        val parsedAmount = amount.toDoubleOrNull() ?: 0.0
        if (parsedAmount <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a cero.")
        }
        return parsedAmount
    }

    // Obtener la fecha de una transacción
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

    suspend fun fetchPendingTransactions(userId: String): List<Transaction> {
        return FirestoreUtils.fetchCollectionAs<Transaction>("User/$userId/Transactions",
            Transaction::class.java)
            .filter { !it.solved && it.date != null }
    }

    suspend fun fetchPendingSubscriptions(userId: String): List<Subscription> {
        return FirestoreUtils.fetchCollectionAs<Subscription>("User/$userId/Subscriptions",
            Subscription::class.java)
            .filter { !it.solved && it.next_payment_date != null }
    }

    suspend fun markTransactionAsSolved(userId: String, transactionId: String): Boolean {
        return FirestoreUtils.updateField(
            collectionName = "User/$userId/Transactions",
            documentId = transactionId,
            fieldName = "solved",
            fieldValue = true
        )
    }

    suspend fun markSubscriptionAsSolved(userId: String, subscriptionId: String): Boolean {
        return FirestoreUtils.updateField(
            collectionName = "User/$userId/Subscriptions",
            documentId = subscriptionId,
            fieldName = "solved",
            fieldValue = true
        )
    }

    suspend fun updateSubscriptionNextPaymentDate(userId: String, subscriptionId: String, nextPaymentDate: Date): Boolean {
        return FirestoreUtils.updateField(
            collectionName = "User/$userId/Subscriptions",
            documentId = subscriptionId,
            fieldName = "next_payment_date",
            fieldValue = nextPaymentDate
        )
    }

    fun isDateInFuture(date: Date): Boolean {
        val currentDate = Calendar.getInstance().time
        return date.after(currentDate)
    }
}

