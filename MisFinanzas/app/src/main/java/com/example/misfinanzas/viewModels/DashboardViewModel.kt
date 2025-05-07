package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.models.Category
import com.example.misfinanzas.models.DashboardModel
import com.example.misfinanzas.models.DashboardModel.getMonthName
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.repositories.TransactionRepository
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

    private val repository = TransactionRepository()

    // Estados para manejar datos
    private val _allTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _monthTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())

    private val _totalIncome = MutableStateFlow(0)
    val totalIncome: StateFlow<Int> get() = _totalIncome

    private val _totalExpense = MutableStateFlow(0)
    val totalExpense: StateFlow<Int> get() = _totalExpense

    // Exponer estados inmutables
    val monthTransactions: StateFlow<List<TransactionEntity>> get() = _monthTransactions

    private val _expensesByCategory = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())
    val expensesByCategory: StateFlow<Map<String, Pair<Int, String>>> get() = _expensesByCategory

    private val _incomesByCategory = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())
    val incomesByCategory: StateFlow<Map<String, Pair<Int, String>>> get() = _incomesByCategory

    private val roomRepository = RoomRepository()
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val _categoriesState = MutableStateFlow<List<Category>>(emptyList())
    private val categoriesState: StateFlow<List<Category>> get() = _categoriesState

    private val _estimatedBalance = MutableStateFlow(0.0)
    val estimatedBalance: StateFlow<Double> get() = _estimatedBalance

    private val _balanceReal = MutableStateFlow(0.0)

    init {
        updateSelectedMonth()
        if (userId != null) {
            viewModelScope.launch {
                fetchCategories(userId)
                getAllTransactions(userId).collect { list ->
                    _allTransactions.value = list
                    refreshFilteredData(list)
                    updateEstimatedBalanceWithRealBalance(_balanceReal.value)
                }
            }
        }
    }

    fun updateEstimatedBalanceWithRealBalance(balanceReal: Double) {
        _balanceReal.value = balanceReal
        val estimated = balanceReal + (_totalIncome.value - _totalExpense.value)
        _estimatedBalance.value = estimated
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

        // Filtrar transacciones del mes seleccionado
        val filtered = transactions.filter { transaction ->
            transaction.date?.getMonthName() == currentMonthFilter
        }

        // Obtener las categorías actuales
        val categories = categoriesState.value.associateBy { it.name }

        // Calcular totales por categoría para Gastos
        val expenseByCategory = filtered
            .filter { it.type == "Gasto" }
            .groupBy { it.category }
            .mapValues { entry ->
                val totalAmount = entry.value.sumOf { it.amount }.toInt()
                val categoryColor = categories[entry.key]?.color ?: "#61CBB3"
                Pair(totalAmount, categoryColor)
            }

        // Calcular totales por categoría para Ingresos
        val incomeByCategory = filtered
            .filter { it.type == "Ingreso" }
            .groupBy { it.category }
            .mapValues { entry ->
                val totalAmount = entry.value.sumOf { it.amount }.toInt()
                val categoryColor = categories[entry.key]?.color ?: "#61CBB3"
                Pair(totalAmount, categoryColor)
            }

        // Actualizar estados
        _monthTransactions.value = filtered
        _expensesByCategory.value = expenseByCategory
        _incomesByCategory.value = incomeByCategory

        // Actualizar totales de Ingresos y Gastos
        _totalIncome.value = incomeByCategory.values.sumOf { it.first }
        _totalExpense.value = expenseByCategory.values.sumOf { it.first }
    }

    private fun getAllTransactions(userId: String): Flow<List<TransactionEntity>> {
        return roomRepository.getAllTransactions(userId)
    }

    private fun fetchCategories(userId: String) = viewModelScope.launch{
        val categoriesDocuments = repository.fetchCategories(userId)
        if (categoriesDocuments != null) {
            _categoriesState.value = categoriesDocuments
            refreshFilteredData(_allTransactions.value)
        }
    }
}