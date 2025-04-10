package com.example.misfinanzas.repositories

import com.example.misfinanzas.models.Balance
import com.example.misfinanzas.models.Subscription
import com.example.misfinanzas.models.Transaction
import com.example.misfinanzas.models.UserData
import com.example.misfinanzas.utils.FirestoreUtils
import java.util.Calendar
import java.util.Date
import java.util.UUID

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

    fun isDateInFuture(date: Date): Boolean {
        val currentDate = Calendar.getInstance().time
        return date.after(currentDate)
    }
}