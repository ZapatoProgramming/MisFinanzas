package com.example.misfinanzas.viewModels

import android.util.Log
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

    private val _totalExpense = MutableStateFlow(0)

    // Exponer estados inmutables
    val monthTransactions: StateFlow<List<TransactionEntity>> get() = _monthTransactions

    private val _expensesByCategory = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())

    private val _incomesByCategory = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())

    private val _incomesByCategoryThisMonth = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())
    val incomesByCategoryThisMonth: StateFlow<Map<String, Pair<Int, String>>> get() = _incomesByCategoryThisMonth

    private val _expensesByCategoryThisMonth = MutableStateFlow<Map<String, Pair<Int, String>>>(emptyMap())
    val expensesByCategoryThisMonth: StateFlow<Map<String, Pair<Int, String>>> get() = _expensesByCategoryThisMonth

    private val _expensesThisMonth = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val expensesThisMonth: StateFlow<List<TransactionEntity>> get() = _expensesThisMonth

    private val _incomesThisMonth = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val incomesThisMonth: StateFlow<List<TransactionEntity>> get() = _incomesThisMonth

    private val roomRepository = RoomRepository()
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val _categoriesState = MutableStateFlow<List<Category>>(emptyList())
    private val categoriesState: StateFlow<List<Category>> get() = _categoriesState

    private val _estimatedBalance = MutableStateFlow(0.0)
    val estimatedBalance: StateFlow<Double> get() = _estimatedBalance

    private val _balanceReal = MutableStateFlow(0.0)

    init {
        updateSelectedMonth()
            viewModelScope.launch {
                refresh()
            }
    }

    suspend fun refresh(){
        if (userId != null) {
        roomRepository.getBalanceByUserId(userId)
            ?.let { updateEstimatedBalanceWithRealBalance(it.initial_balance)
               Log.d("Balance","initial balance "+it.initial_balance)
                Log.d("Balance","current balance "+it.current_balance)}
        fetchCategories(userId)
        getAllTransactions(userId).collect { list ->
            _allTransactions.value = list
            refreshFilteredData(list)
            updateEstimatedBalanceWithRealBalance(_balanceReal.value)
        }
        }
    }

    private fun updateEstimatedBalanceWithRealBalance(balanceReal: Double) {
        _balanceReal.value = balanceReal
        Log.d("Balance", balanceReal.toString())
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

        // Obtener el índice del mes seleccionado
        val currentMonthIndex = months.indexOf(currentMonthFilter)

        // Filtrar transacciones desde el primer mes hasta el mes seleccionado (inclusive)
        val filtered = transactions.filter { transaction ->
            val transactionMonthIndex = transaction.date?.getMonthName()?.let { months.indexOf(it) } ?: -1
            transactionMonthIndex in 0..currentMonthIndex
        }
        // Calcular transacciones solo del mes actual (no acumuladas)
        val filteredOnlyThisMonth = transactions.filter { transaction ->
            val transactionMonth = transaction.date?.getMonthName()
            transactionMonth == selectedMonth
        }

        _expensesThisMonth.value = filteredOnlyThisMonth.filter { it.type == "Gasto" }
        _incomesThisMonth.value = filteredOnlyThisMonth.filter { it.type == "Ingreso" }

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

        // Ingresos solo del mes actual agrupados por categoría
        _incomesByCategoryThisMonth.value = filteredOnlyThisMonth
            .filter { it.type == "Ingreso" }
            .groupBy { it.category }
            .mapValues { entry ->
                val totalAmount = entry.value.sumOf { it.amount }.toInt()
                val categoryColor = categories[entry.key]?.color ?: "#61CBB3"
                Pair(totalAmount, categoryColor)
            }

        // Gastos solo del mes actual agrupados por categoría
        _expensesByCategoryThisMonth.value = filteredOnlyThisMonth
            .filter { it.type == "Gasto" }
            .groupBy { it.category }
            .mapValues { entry ->
                val totalAmount = entry.value.sumOf { it.amount }.toInt()
                val categoryColor = categories[entry.key]?.color ?: "#61CBB3"
                Pair(totalAmount, categoryColor)
            }

        _monthTransactions.value = filteredOnlyThisMonth
        _expensesByCategory.value = expenseByCategory
        _incomesByCategory.value = incomeByCategory

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