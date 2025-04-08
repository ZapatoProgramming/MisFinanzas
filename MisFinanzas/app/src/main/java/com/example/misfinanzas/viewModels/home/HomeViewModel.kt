package com.example.misfinanzas.viewModels.home

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
import com.example.misfinanzas.models.UserData
import com.example.misfinanzas.utils.FirestoreUtils
import com.example.misfinanzas.views.home.HomeScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)

    private val _currentRoute = mutableStateOf(HomeScreens.Dashboard.route)
    val currentRoute: String get() = _currentRoute.value

    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands = _navigationCommands.asSharedFlow()

    var selectedMonth by mutableStateOf("")
        private set

    var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
        private set

    val months = DashboardModel.months

    var balance by mutableDoubleStateOf(0.0)
        private set

    var hasEnteredBalance by mutableStateOf(false)
        private set

    var hasAddedFirstTransaction by mutableStateOf(false)
        private set

    init {
        updateSelectedMonth()
    }

    fun navigateTo(route: String) {
        _currentRoute.value = route
        viewModelScope.launch {
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

    fun updateBalance(newBalance: Double) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    balance = newBalance
                    hasEnteredBalance = true

                    val balanceData = Balance(current_balance = newBalance)
                    val success = FirestoreUtils.uploadDocument("Balance", userId, balanceData)

                    if (!success) {
                        _error.value = "Error al guardar el saldo en Firestore."
                        return@launch
                    }

                    val userUpdateSuccess = FirestoreUtils.updateField(
                        collectionName = "User",
                        documentId = userId,
                        fieldName = "has_entered_balance",
                        fieldValue = true
                    )

                    if (!userUpdateSuccess) {
                        _error.value = "Error al actualizar el documento User."
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

    val message: String
        get() = when {
            !hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primer saldo y primera transacción"
            hasEnteredBalance && !hasAddedFirstTransaction -> "Ingresa tu primera transacción"
            else -> "Crea tu historial de ingresos y gastos"
        }

    private fun updateSelectedMonth() {
        selectedMonth = months[currentIndex]
    }

    var cantidad by mutableStateOf("")
        private set

    var categoria by mutableStateOf("")
        private set

    var dia by mutableStateOf("")

    var mes by mutableStateOf("")

    var anio by mutableStateOf("")

    var esSubscripcion by mutableStateOf(false)
        private set

    var frecuenciaSubscripcion by mutableStateOf("Mensual")
        private set

    var esHoy by mutableStateOf(false)
        private set

    var tipoTransaccion by mutableStateOf("Gasto")
        private set

    val opcionesFrecuencia = listOf("Mensual", "Anual")

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

    sealed class NavigationCommand {
        data class NavigateTo(val route: String) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedUserData = FirestoreUtils.fetchDocumentAs<UserData>("User", userId)

                if (fetchedUserData != null) {
                    _userData.value = fetchedUserData
                    _userData.value?.let { userData ->
                        hasEnteredBalance = userData.has_entered_balance
                        hasAddedFirstTransaction = userData.has_added_first_transaction
                    }

                }
            } catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBalance(userId: String){
        viewModelScope.launch {
            try {
                val fetchedBalance = FirestoreUtils.fetchDocumentAs<Balance>("Balance",userId)
                if (fetchedBalance != null) {
                    balance = fetchedBalance.current_balance
                } else {
                    _error.value = "No se encontró información de saldo para este usuario."
                }
            }catch (e: Exception) {
                _error.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}