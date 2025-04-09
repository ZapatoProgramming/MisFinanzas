package com.example.misfinanzas.viewModels.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.*
import com.example.misfinanzas.views.home.HomeScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(private val repository: TransactionRepository = TransactionRepository()) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    //private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    //private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands = _navigationCommands.asSharedFlow()
    private val _currentRoute = mutableStateOf(HomeScreens.Dashboard.route)
    val currentRoute: String get() = _currentRoute.value

    var selectedMonth by mutableStateOf("")
        private set
    var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
        private set
    val months = DashboardModel.months
    var balance by mutableDoubleStateOf(0.0)
    var hasEnteredBalance by mutableStateOf(false)
        private set
    var hasAddedFirstTransaction by mutableStateOf(false)
    var cantidad by mutableStateOf("")
        private set
    var categoria by mutableStateOf("")
        private set
    var dia by mutableStateOf("")
    var mes by mutableStateOf("")
    var anio by mutableStateOf("")
    var esHoy by mutableStateOf(false)
        private set
    var esSubscripcion by mutableStateOf(false)
        private set
    var frecuenciaSubscripcion by mutableStateOf("Mensual")
        private set
    var tipoTransaccion by mutableStateOf("Gasto")
        private set
    val opcionesFrecuencia = listOf("Mensual", "Anual")

    init {
        updateSelectedMonth()
    }

    fun navigateTo(route: String) {
        viewModelScope.launch {
            _currentRoute.value = route
            _navigationCommands.emit(NavigationCommand.NavigateTo(route))
        }
    }

    fun navigateToPreviousMonth() {
        if (currentIndex > 0) {
            currentIndex--
            updateSelectedMonth()
        }
    }

    fun navigateToNextMonth() {
        if (currentIndex < months.size - 1) {
            currentIndex++
            updateSelectedMonth()
        }
    }

    val message: String
        get() = when {
            !hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primer saldo y primera transacción"
            hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primera transacción"
            else -> "Crea tu historial de ingresos y gastos"
        }

    private fun updateSelectedMonth() {
        selectedMonth = months[currentIndex]
    }

    fun updateCantidad(value: String) {
        cantidad = value
    }

    fun updateCategoria(value: String) {
        categoria = value
    }

    fun toggleEsHoy(value: Boolean) {
        esHoy = value
        if (value) {
            val (diaHoy, mesHoy, anioHoy) = AddModel.getCurrentDate()
            dia = diaHoy
            mes = mesHoy
            anio = anioHoy
        }
    }

    fun updateFrecuenciaSubscripcion(value: String) {
        frecuenciaSubscripcion = value
    }

    fun toggleEsSubscripcion(value: Boolean) {
        esSubscripcion = value
    }

    fun updateTipoTransaccion(value: String) {
        tipoTransaccion = value
    }

    fun createTransaction(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val amount = repository.validateAmount(cantidad)
                val transactionDate = repository.getTransactionDate(esHoy, dia, mes, anio)

                if (esSubscripcion) {
                    handleSubscription(userId, amount, transactionDate)
                } else {
                    handleRegularTransaction(userId, amount, transactionDate)
                }

                if (!hasAddedFirstTransaction) {
                    markFirstTransactionAdded()
                }

                clearTransactionFields()

            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun handleSubscription(userId: String, amount: Double, transactionDate: Date) {
        val subscription = Subscription(
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            start_date = transactionDate,
            frequency = frecuenciaSubscripcion,
            next_payment_date = repository.calculateNextPaymentDate(transactionDate, frecuenciaSubscripcion),
            created_at = FieldValue.serverTimestamp()
        )
        if (!repository.uploadSubscription(userId, subscription)) {
            _error.value = "Error al guardar la suscripción."
        }
    }

    private suspend fun handleRegularTransaction(userId: String, amount: Double, transactionDate: Date) {
        val transaction = Transaction(
            type = tipoTransaccion,
            amount = amount,
            category = categoria,
            date = transactionDate,
            created_at = FieldValue.serverTimestamp(),
            solved = false
        )

        if (!repository.isDateInFuture(transactionDate)) {
            if (transaction.type == "Gasto") applyExpense(transaction.amount)
            else applyIncome(transaction.amount)

            transaction.solved = true
        }

        if (!repository.uploadTransaction(userId, transaction)) {
            _error.value = "Error al guardar la transacción."
        }
    }

    fun updateBalance(newBalance: Double) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (userId != null) {
                    balance = newBalance
                    if(!hasEnteredBalance) {
                        hasEnteredBalance = true
                        if (!repository.updateUserField(userId, "has_entered_balance", true)) {
                            _error.value = "Error al actualizar el estado de has_entered_balance en Firestore."
                            return@launch
                        }
                    }

                    // Actualizar el saldo en Firestore
                    if (!repository.updateBalance(userId, newBalance)) {
                        _error.value = "Error al guardar el saldo en Firestore."
                        return@launch
                    }
                } else {
                    _error.value = "No se pudo obtener el ID del usuario."
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            }
        }
    }

    fun markFirstTransactionAdded() {
        hasAddedFirstTransaction = true
    }

    fun applyExpense(expense: Double) {
        val newBalance = balance - expense
        updateBalance(newBalance)
    }

    fun applyIncome(income: Double) {
        val newBalance = balance + income
        updateBalance(newBalance)
    }

    private fun clearTransactionFields() {
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

    sealed class NavigationCommand {
        data class NavigateTo(val route: String) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedUserData = repository.fetchUserData(userId)
                updateUserData(fetchedUserData)
            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateUserData(userData: UserData?) {
        _userData.value = userData
        userData?.let {
            hasEnteredBalance = it.has_entered_balance
            hasAddedFirstTransaction = it.has_added_first_transaction
        }
    }

    fun fetchBalance(userId: String) {
        viewModelScope.launch {
            try {
                val fetchedBalance = repository.fetchBalance(userId)
                updateBalanceState(fetchedBalance)
            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateBalanceState(balanceData: Balance?) {
        balance = balanceData?.current_balance ?: 0.0
    }

    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun checkAndApplyPendingTransactions(userId: String) {
        viewModelScope.launch {
            try {
                val pendingTransactions = repository.fetchPendingTransactions(userId)
                pendingTransactions.forEach { transaction ->
                    if (transaction.date != null && !transaction.solved && !repository.isDateInFuture(transaction.date)) {
                        // Aplicar el cambio al saldo
                        if (transaction.type == "Gasto") applyExpense(transaction.amount)
                        else applyIncome(transaction.amount)

                        // Marcar la transacción como resuelta
                        repository.markTransactionAsSolved(userId, transaction.id)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al verificar transacciones pendientes: ${e.message}"
            }
        }
    }
}
