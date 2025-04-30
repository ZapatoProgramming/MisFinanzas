package com.example.misfinanzas.models

import java.util.Calendar
import java.util.Date

object DashboardModel {
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    fun getCurrentMonthIndex(): Int {
        return Calendar.getInstance().get(Calendar.MONTH)
    }

    fun Date.getMonthName(): String {
        val calendar = Calendar.getInstance().apply { time = this@getMonthName }
        val monthIndex = calendar.get(Calendar.MONTH)
        val monthNames = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return monthNames.getOrNull(monthIndex) ?: "Unknown"
    }
}