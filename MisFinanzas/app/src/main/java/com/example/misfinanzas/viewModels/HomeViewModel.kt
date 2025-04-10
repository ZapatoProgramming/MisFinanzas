package com.example.misfinanzas.viewModels

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
import com.example.misfinanzas.views.HomeScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

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

    // Core transaction logic
    fun createTransaction(userId: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val amount = repository.validateAmount(cantidad)
            val date = repository.getTransactionDate(esHoy, dia, mes, anio)

            if (esSubscripcion) {
                saveSubscription(userId, amount, date)
            } else {
                saveTransaction(userId, amount, date)
            }

            if (!hasAddedFirstTransaction) markFirstTransactionAdded()
            resetTransactionInputs()
        } catch (e: Exception) {
            _error.value = e.message ?: "Error inesperado"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun saveSubscription(userId: String, amount: Double, date: Date) {
        val sub = Subscription(
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            start_date = date,
            frequency = frecuenciaSubscripcion,
            next_payment_date = repository.calculateNextPaymentDate(date, frecuenciaSubscripcion),
            created_at = FieldValue.serverTimestamp()
        )
        if (!repository.uploadSubscription(userId, sub)) {
            _error.value = "Error al guardar la suscripción."
        }
    }

    private suspend fun saveTransaction(userId: String, amount: Double, date: Date) {
        val transaction = Transaction(
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            date = date,
            created_at = FieldValue.serverTimestamp(),
            solved = false
        )

        if (!repository.isDateInFuture(date)) {
            if (transaction.type == "Gasto") applyExpense(amount) else applyIncome(amount)
            transaction.solved = true
        }

        if (!repository.uploadTransaction(userId, transaction)) {
            _error.value = "Error al guardar la transacción."
        }
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

            if (!repository.updateBalance(userId, newBalance)) {
                _error.value = "Error al guardar el saldo."
            }

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

    fun fetchBalance(userId: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            updateBalanceState(repository.fetchBalance(userId))
        } catch (e: Exception) {
            _error.value = "Error al cargar el saldo: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private fun updateBalanceState(data: Balance?) {
        balance = data?.current_balance ?: 0.0
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    sealed class NavigationCommand {
        data class NavigateTo(val route: String) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }
}