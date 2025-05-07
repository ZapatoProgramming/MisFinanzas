package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.AddModel
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.repositories.TransactionRepository
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class AddViewModel : ViewModel() {

    private val transactionRepository = TransactionRepository()
    private val roomRepository = RoomRepository()

    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var category by mutableStateOf("")
    var day by mutableStateOf("")
    var month by mutableStateOf("")
    var year by mutableStateOf("")
    var isToday by mutableStateOf(false)
    var isSubscription by mutableStateOf(false)
    var subscriptionFrequency by mutableStateOf("Mensual")
    var transactionType by mutableStateOf("Gasto")
    val frequencyOptions = listOf("Mensual", "Anual")

    fun updateAmount(value: String) {
        amount = value
    }

    fun updateDescription(value: String){
        description = value
    }

    fun updateCategory(value: String) {
        category = value
    }

    fun updateSubscriptionFrequency(value: String) {
        subscriptionFrequency = value
    }

    fun updateTransactionType(value: String) {
        transactionType = value
    }

    fun toggleIsToday(value: Boolean) {
        isToday = value
        if (value) {
            val (d, m, y) = AddModel.getCurrentDate()
            day = d
            month = m
            year = y
        }
    }

    fun toggleIsSubscription(value: Boolean) {
        isSubscription = value
    }

    fun createTransaction(userId: String) = viewModelScope.launch {
        val validatedAmount = transactionRepository.validateAmount(amount)
        val transactionDate = transactionRepository.getTransactionDate(isToday, day, month, year)

        if (isSubscription) {
            saveSubscription(userId, validatedAmount, transactionDate)
        } else {
            saveTransaction(userId, validatedAmount, transactionDate)
        }

        resetInputs()
    }

    private suspend fun saveSubscription(userId: String, amount: Double, startDate: Date) {
        val subscription = SubscriptionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = transactionType,
            amount = amount,
            category = category,
            description = description,
            start_date = startDate,
            frequency = subscriptionFrequency,
            next_payment_date = transactionRepository.calculateNextPaymentDate(startDate, subscriptionFrequency),
            created_at = Date()
        )

        roomRepository.insertSubscription(subscription)
        if(transactionRepository.uploadSubscription(userId, subscription)){
            subscription.synced = true
            roomRepository.insertSubscription(subscription)
        }

    }

    private suspend fun saveTransaction(userId: String, amount: Double, date: Date) {
        if(category == "") category = "Otros"
        val transaction = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = transactionType,
            amount = amount,
            category = category,
            description = description,
            date = date,
            created_at = Date()
        )

        if (!transactionRepository.isDateInFuture(date)) {
            transaction.solved = true
        }

        roomRepository.insertTransaction(transaction)
        if(transactionRepository.uploadTransaction(userId, transaction)){
            transaction.synced = true
            roomRepository.insertTransaction(transaction)
        }
    }

    private fun resetInputs() {
        amount = ""
        category = ""
        description = ""
        day = ""
        month = ""
        year = ""
        isToday = false
        isSubscription = false
        subscriptionFrequency = "Mensual"
        transactionType = "Gasto"
    }
}
