package com.example.misfinanzas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.misfinanzas.models.DashboardModel

class DashboardViewModel : ViewModel() {
    private val months = DashboardModel.months
    private var currentIndex by mutableIntStateOf(DashboardModel.getCurrentMonthIndex())
    var selectedMonth by mutableStateOf(months[currentIndex])
        private set

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
}