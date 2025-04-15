package com.example.misfinanzas.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.AddModel
import com.example.misfinanzas.models.Balance
import com.example.misfinanzas.models.DashboardModel
import com.example.misfinanzas.models.Subscription
import com.example.misfinanzas.models.Transaction
import com.example.misfinanzas.repositories.TransactionRepository
import com.example.misfinanzas.models.UserData
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.room.BalanceEntity
import com.example.misfinanzas.room.SubscriptionEntity
import com.example.misfinanzas.room.TransactionEntity
import com.example.misfinanzas.views.HomeScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class HomeViewModel(): ViewModel(){

    val repository = TransactionRepository()
    val roomRepository = RoomRepository()

    // State
    private val _userData = MutableStateFlow<UserData?>(null)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)

    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands = _navigationCommands.asSharedFlow()

    private val _currentRoute = mutableStateOf(HomeScreens.Dashboard.route)
    val currentRoute: String get() = _currentRoute.value

    val months = DashboardModel.months
    var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
        private set
    var selectedMonth by mutableStateOf(months[currentIndex])
        private set

    // User state
    var balance by mutableDoubleStateOf(0.0)
    var hasEnteredBalance by mutableStateOf(false)
        private set
    var hasAddedFirstTransaction by mutableStateOf(false)
        private set

    // Transaction inputs
    var cantidad by mutableStateOf("")
    var categoria by mutableStateOf("")
    var dia by mutableStateOf("")
    var mes by mutableStateOf("")
    var anio by mutableStateOf("")
    var esHoy by mutableStateOf(false)
    var esSubscripcion by mutableStateOf(false)
    var frecuenciaSubscripcion by mutableStateOf("Mensual")
    var tipoTransaccion by mutableStateOf("Gasto")

    val opcionesFrecuencia = listOf("Mensual", "Anual")

    val message: String
        get() = when {
            !hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primer saldo y primera transacción"
            hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primera transacción"
            else -> "Crea tu historial de ingresos y gastos"
        }

    init {
        updateSelectedMonth()
    }

    // Navigation
    fun navigateTo(route: String) {
        _currentRoute.value = route
        viewModelScope.launch { _navigationCommands.emit(NavigationCommand.NavigateTo(route)) }
    }

    sealed class NavigationCommand {
        data class NavigateTo(val route: String) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }

    fun navigateToPreviousMonth() {
        if (currentIndex > 0) {
            currentIndex--
            updateSelectedMonth()
        }
    }

    fun navigateToNextMonth() {
        if (currentIndex < months.lastIndex) {
            currentIndex++
            updateSelectedMonth()
        }
    }

    private fun updateSelectedMonth() {
        selectedMonth = months[currentIndex]
    }

    // Transaction input handlers
    fun updateCantidad(value: String) { cantidad = value }
    fun updateCategoria(value: String) { categoria = value }
    fun updateFrecuenciaSubscripcion(value: String) { frecuenciaSubscripcion = value }
    fun updateTipoTransaccion(value: String) { tipoTransaccion = value }

    fun toggleEsHoy(value: Boolean) {
        esHoy = value
        if (value) {
            val (d, m, a) = AddModel.getCurrentDate()
            dia = d
            mes = m
            anio = a
        }
    }

    fun toggleEsSubscripcion(value: Boolean) {
        esSubscripcion = value
    }

    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>> {
        return roomRepository.getAllSubscriptions(getCurrentUserId().toString())
    }

    fun checkData() = viewModelScope.launch {
        val userId = getCurrentUserId().toString()
        syncBalance(userId)
        syncLocalTransactionsToFirebase(userId)
    }

    fun syncBalance(userId: String) = viewModelScope.launch {
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

    private suspend fun syncSubscriptionsToFirebase(userId: String) = viewModelScope.launch {
        val subscriptionsEntities: List<SubscriptionEntity> = roomRepository.getUnsyncedSubscriptions(userId)
        val subscriptionsDocuments: List<Subscription> = subscriptionsEntities
            .map { repository.entityToDocument(it) as Subscription }
        subscriptionsDocuments.forEach { subscription ->
            repository.uploadSubscription(userId, subscription)
        }
    }

    fun createTransaction(userId: String) = viewModelScope.launch {
        try {
            val amount = repository.validateAmount(cantidad)
            val date = repository.getTransactionDate(esHoy, dia, mes, anio)

            if (esSubscripcion) {
                saveSubscription(userId, amount, date)
            } else {
                saveTransaction(userId, amount, date)
            }

            if (!hasAddedFirstTransaction) {
                markFirstTransactionAdded()
                repository.updateUserField(userId,
                    "has_added_first_transaction",
                    true)
            }
            resetTransactionInputs()
        } catch (e: Exception) {
            _error.value = e.message ?: "Error inesperado"
        }
    }

    private suspend fun saveSubscription(userId: String, amount: Double, date: Date) {
        // Crear el objeto Subscription
        val subscriptionEntity = SubscriptionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            start_date = date,
            frequency = frecuenciaSubscripcion,
            next_payment_date = repository.calculateNextPaymentDate(date, frecuenciaSubscripcion),
            created_at = Date()
        )

        // Aplicar el gasto o ingreso si la fecha no está en el futuro
        if (!repository.isDateInFuture(date)) {
            if (subscriptionEntity.type == "Gasto") applyExpense(amount) else applyIncome(amount)
        }

        // Guardar la suscripción en la base de datos
        roomRepository.insertSubscription(subscriptionEntity)
    }

    private suspend fun saveTransaction(userId: String, amount: Double, date: Date) {

        val transactionEntity = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            date = date,
            created_at = Date(),
        )

        if (!repository.isDateInFuture(date)) {
            if (transactionEntity.type == "Gasto") applyExpense(amount) else applyIncome(amount)
            transactionEntity.solved = true
        }

        roomRepository.insertTransaction(transactionEntity)
    }

    fun markTransactionAsSynced(transactionId: String) = viewModelScope.launch{
        roomRepository.markTransactionAsSynced(transactionId)
    }

    private fun resetTransactionInputs() {
        cantidad = ""
        categoria = ""
        dia = ""
        mes = ""
        anio = ""
        esHoy = false
        esSubscripcion = false
        frecuenciaSubscripcion = "Mensual"
        tipoTransaccion = "Gasto"
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

    fun applyExpense(expense: Double) = updateBalance(balance - expense)
    fun applyIncome(income: Double) = updateBalance(balance + income)
    fun markFirstTransactionAdded() { hasAddedFirstTransaction = true }

    fun fetchUserData(userId: String) = viewModelScope.launch {
        _isLoading.value = true
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

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

}