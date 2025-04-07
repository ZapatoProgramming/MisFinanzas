package com.example.misfinanzas.viewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.misfinanzas.models.DashboardModel

class DashboardViewModel : ViewModel() {

    var selectedMonth by mutableStateOf("")
        private set

    var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
        private set

    val months = DashboardModel.months

    var balance by mutableDoubleStateOf(0.0)
        private set

    var hasEnteredBalance by mutableStateOf(false)
        private set

    var hasAddedFirstExpense by mutableStateOf(false)
        private set

    init {
        updateSelectedMonth()
    }

    private fun updateSelectedMonth() {
        selectedMonth = months[currentIndex]
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
        balance = newBalance
        hasEnteredBalance = true
    }

    fun markFirstExpenseAdded() {
        hasAddedFirstExpense = true
    }

    val message: String
        get() = when {
            !hasEnteredBalance && !hasAddedFirstExpense -> "Ingresa tu primer saldo y primer transaccion"
            hasEnteredBalance && !hasAddedFirstExpense -> "Ingresa tu primer transaccion"
            else -> "Crea tu historial de ingresos y gastos"
        }
}