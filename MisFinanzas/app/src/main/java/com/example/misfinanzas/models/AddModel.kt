package com.example.misfinanzas.models

import java.util.Calendar

object AddModel {
    fun getCurrentDate(): Triple<String, String, String> {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        return Triple(day, month, year)
    }
}