package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.DashboardModel
import com.example.misfinanzas.models.DashboardModel.getMonthName
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.room.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val months = DashboardModel.months
    private var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
    var selectedMonth by mutableStateOf(months[currentIndex])
        private set

    // Estados para manejar datos
    private val _allTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _monthTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _expensesByCategory = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _incomesByCategory = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _totalIncome = MutableStateFlow(0)
    val totalIncome: StateFlow<Int> get() = _totalIncome

    private val _totalExpense = MutableStateFlow(0)
    val totalExpense: StateFlow<Int> get() = _totalExpense

    // Exponer estados inmutables
    val monthTransactions: StateFlow<List<TransactionEntity>> get() = _monthTransactions
    val expensesByCategory: StateFlow<Map<String, Int>> get() = _expensesByCategory
    val incomesByCategory: StateFlow<Map<String, Int>> get() = _incomesByCategory

    private val roomRepository = RoomRepository()
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        updateSelectedMonth()
        if (userId != null) {
            viewModelScope.launch {
                getAllTransactions(userId).collect { list ->
                    _allTransactions.value = list
                    refreshFilteredData(list)
                }
            }
        }
    }

    fun navigateToPreviousMonth() {
        if (currentIndex > 0) {
            currentIndex--
            updateSelectedMonthAndFilter()
        }
    }

    fun navigateToNextMonth() {
        if (currentIndex < months.lastIndex) {
            currentIndex++
            updateSelectedMonthAndFilter()
        }
    }

    private fun updateSelectedMonth() {
        selectedMonth = months[currentIndex]
    }

    private fun updateSelectedMonthAndFilter() {
        updateSelectedMonth()
        refreshFilteredData(_allTransactions.value)
    }

    private fun refreshFilteredData(transactions: List<TransactionEntity>) {
        val currentMonthFilter = selectedMonth

        val filtered = transactions.filter { transaction ->
            transaction.date?.getMonthName() == currentMonthFilter
        }

        // Actualizar estado filtrado para transacciones del mes
        _monthTransactions.value = filtered

        // Calcular totales
        val expenseByCategory = filtered
            .filter { it.type == "Gasto" }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }

        val incomeByCategory = filtered
            .filter { it.type == "Ingreso" }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }

        _expensesByCategory.value = expenseByCategory
        _incomesByCategory.value = incomeByCategory

        // 🆕 Actualizar totales de Ingresos y Gastos
        _totalIncome.value = incomeByCategory.values.sum()
        _totalExpense.value = expenseByCategory.values.sum()
    }

    private fun getAllTransactions(userId: String): Flow<List<TransactionEntity>> {
        return roomRepository.getAllTransactions(userId)
    }
}