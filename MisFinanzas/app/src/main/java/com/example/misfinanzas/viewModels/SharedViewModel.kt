package com.example.misfinanzas.viewModels

import android.util.Log
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
import com.example.misfinanzas.room.CategoryEntity
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import com.example.misfinanzas.utils.FirestoreUtils
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
        fetchCategories(userId)
        syncLocalTransactionsToFirebase(userId)
        syncLocalSubscriptionsToFirebase(userId)
        syncLocalCategoriesToFirebase(userId)
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
        Log.d("xd", localBalance.toString())
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

            val bal = BalanceEntity(
                userId = userId,
                initial_balance = balance,
                current_balance = balance,
                last_updated = Date()
            )

            if(!hasEnteredBalance) {
                hasEnteredBalance = true
                roomRepository.insertBalance(bal)
                if (!repository.updateUserField(userId, "has_entered_balance", true)) {
                    _error.value = "Error al actualizar has_entered_balance"
                    return@launch
                }
            }else{
                roomRepository.updateBalance(userId,balance)
            }
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

    private fun fetchCategories(userId: String) = viewModelScope.launch {
        val categoryDocuments = repository.fetchCategories(userId)

        val categoryEntities: List<CategoryEntity> = categoryDocuments?.mapNotNull { doc ->
            repository.documentToEntity(doc, userId) as? CategoryEntity
        } ?: emptyList()

        roomRepository.insertAllCategories(categoryEntities)
    }

    private fun syncLocalCategoriesToFirebase(userId: String) = viewModelScope.launch {
        val categoryEntities = roomRepository.getAllCategories(userId)
        val categoryDocuments = categoryEntities.map { entity ->
            Category(
                id = entity.id,
                name = entity.name,
                color = entity.color,
                description = entity.description
            )
        }

        categoriesState.value = categoryDocuments
        val categoryNames = categoryDocuments.map { it.name }
        categoriesNamesState.value = categoryNames

        categoryDocuments.forEach { document ->
            FirestoreUtils.uploadDocument(
                collectionName = "User/${userId}/Categories",
                documentId = document.id,
                data = document
            )
        }
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}