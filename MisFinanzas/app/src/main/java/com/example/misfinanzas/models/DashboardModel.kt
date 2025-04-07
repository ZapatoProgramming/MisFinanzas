package com.example.misfinanzas.models

object DashboardModel {
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    fun getCurrentMonthIndex(): Int {
        return java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    }
}