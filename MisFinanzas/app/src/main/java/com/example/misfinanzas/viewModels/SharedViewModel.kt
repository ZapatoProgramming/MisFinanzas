package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.Balance
import com.example.misfinanzas.models.Category
import com.example.misfinanzas.models.UserData
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.repositories.TransactionRepository
import com.example.misfinanzas.room.BalanceEntity
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class SharedViewModel: ViewModel() {

    private val repository = TransactionRepository()
    private val roomRepository = RoomRepository()

    private val _userData = MutableStateFlow<UserData?>(null)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)

    var balance by mutableDoubleStateOf(0.0)
    var hasEnteredBalance by mutableStateOf(false)
        private set
    var hasAddedFirstTransaction by mutableStateOf(false)
        private set

    val categoriesNamesState = MutableStateFlow<List<String>>(emptyList())
    private val categoriesState = MutableStateFlow<List<Category>>(emptyList())

    val message: String
        get() = when {
            !hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primer saldo y primera transacción"
            hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primera transacción"
            else -> "Crea tu historial de ingresos y gastos"
        }

    fun checkData() = viewModelScope.launch {
        val userId = getCurrentUserId().toString()
        syncBalance(userId)
        fetchTransactions(userId)
        fetchSubscriptions(userId)
        syncLocalTransactionsToFirebase(userId)
        syncLocalSubscriptionsToFirebase(userId)
        fetchCategories(userId)
    }

    private fun fetchTransactions(userId: String) = viewModelScope.launch{
       val transactionDocs = repository.fetchTransactions(userId)
        val transactionEntities: List<TransactionEntity> = transactionDocs?.map { doc ->
            repository.documentToEntity(doc,userId) as TransactionEntity
        } ?: emptyList()
        roomRepository.insertAllTransactions(transactionEntities)
    }

    private fun fetchSubscriptions(userId: String) = viewModelScope.launch {
        val subscriptionDocs = repository.fetchSubscriptions(userId)
        val subscriptionEntities: List<SubscriptionEntity> = subscriptionDocs?.map{ doc ->
            repository.documentToEntity(doc, userId) as SubscriptionEntity
        } ?: emptyList()
        roomRepository.insertAllSubscriptions(subscriptionEntities)
    }

    private fun syncBalance(userId: String) = viewModelScope.launch {
        val localBalance = roomRepository.getBalanceByUserId(userId)
        val firebaseBalance = repository.fetchBalanceFromFirestore(userId)

        if(localBalance == null && firebaseBalance == null) return@launch
        if(localBalance == null && firebaseBalance != null){
            updateBalance(firebaseBalance.current_balance)
        }
        if(localBalance != null && firebaseBalance == null){
            balance = localBalance.current_balance
            syncLocalBalanceToFirebase(userId, localBalance)
        }
        if(localBalance != null && firebaseBalance != null){
            if(localBalance.last_updated.after(firebaseBalance.last_updated)){
                balance = localBalance.current_balance
                syncLocalBalanceToFirebase(userId,localBalance)
            }else{
                updateBalance(firebaseBalance.current_balance)
            }
        }
    }

    private suspend fun syncLocalBalanceToFirebase(userId: String, balanceEntity: BalanceEntity) {
        val balance = repository.entityToDocument(balanceEntity) as Balance
        repository.updateBalance(userId,balance)
    }

    private fun syncLocalTransactionsToFirebase(userId: String) = viewModelScope.launch{
        val transactionsEntities: List<TransactionEntity> = roomRepository.getUnsyncedTransactions(userId)
        transactionsEntities.forEach { transactionEntity ->
            val success = repository.uploadTransaction(userId, transactionEntity)
            if (success) markTransactionAsSynced(transactionEntity.id)
        }
    }

    private fun syncLocalSubscriptionsToFirebase(userId: String) = viewModelScope.launch {
        val subscriptionsEntities: List<SubscriptionEntity> = roomRepository.getUnsyncedSubscriptions(userId)
        subscriptionsEntities.forEach { subscriptionEntity ->
            val success = repository.uploadSubscription(userId, subscriptionEntity)
            if (success) markTransactionAsSynced(subscriptionEntity.id)
        }
    }

    private fun markTransactionAsSynced(transactionId: String) = viewModelScope.launch{
        roomRepository.markTransactionAsSynced(transactionId)
    }

    fun updateBalance(newBalance: Double) = viewModelScope.launch {
        try {
            val userId = getCurrentUserId() ?: run {
                _error.value = "No se pudo obtener el ID del usuario."
                return@launch
            }

            balance = newBalance

            if (!hasEnteredBalance) {
                hasEnteredBalance = true
                if (!repository.updateUserField(userId, "has_entered_balance", true)) {
                    _error.value = "Error al actualizar has_entered_balance"
                    return@launch
                }
            }

            val bal = BalanceEntity(
                userId = userId,
                current_balance = balance,
                last_updated = Date()
            )

            roomRepository.insertBalance(bal)
            syncLocalBalanceToFirebase(userId, bal)

        } catch (e: Exception) {
            _error.value = "Error inesperado: ${e.message}"
        }
    }

    private fun applyExpense(expense: String) {
        val amount = repository.validateAmount(expense)
        updateBalance(balance - amount)
    }
    private fun applyIncome(income: String) {
        val amount = repository.validateAmount(income)
        updateBalance(balance + amount)
    }
    private fun markFirstTransactionAdded() { hasAddedFirstTransaction = true }

    fun fetchUserData(userId: String) = viewModelScope.launch {
        try {
            updateUserData(repository.fetchUserData(userId))
        } catch (e: Exception) {
            _error.value = "Error al cargar datos: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private fun updateUserData(data: UserData?) {
        _userData.value = data
        data?.let {
            hasEnteredBalance = it.has_entered_balance
            hasAddedFirstTransaction = it.has_added_first_transaction
        }
    }

    fun userHasAddedFirstTransaction(userId: String) = viewModelScope.launch{
        if (!hasAddedFirstTransaction) {
            markFirstTransactionAdded()
            repository.updateUserField(userId,
                "has_added_first_transaction",
                true)
        }
    }

    fun applyTransactionIfNeeded(tipoTransaccion: String, cantidad: String, date: Date){
        if (!repository.isDateInFuture(date)) {
            if (tipoTransaccion == "Gasto") applyExpense(cantidad) else applyIncome(cantidad)
        }
    }

    private fun fetchCategories(userId: String) = viewModelScope.launch{
        val categoriesDocuments = repository.fetchCategories(userId)
        if (categoriesDocuments != null) {
            categoriesState.value = categoriesDocuments
        }
        val categoryNames: List<String> = categoriesDocuments?.map { it.name } ?: emptyList()
        categoriesNamesState.value = categoryNames
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}