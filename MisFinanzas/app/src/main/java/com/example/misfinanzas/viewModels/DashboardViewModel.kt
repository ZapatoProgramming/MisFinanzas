package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.misfinanzas.models.DashboardModel
import com.example.misfinanzas.repositories.RoomRepository
import com.example.misfinanzas.room.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class DashboardViewModel : ViewModel() {
    private val months = DashboardModel.months
    private var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
    var selectedMonth by mutableStateOf(months[currentIndex])
        private set

    private val roomRepository = RoomRepository()

    init {
        updateSelectedMonth()
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

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return roomRepository.getAllTransactions(getCurrentUserId().toString())
    }

    private fun getCurrentUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid
}