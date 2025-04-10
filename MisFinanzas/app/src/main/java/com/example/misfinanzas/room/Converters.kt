package com.example.misfinanzas.room

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    // Convertir Date a Long (representación en milisegundos)
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    // Convertir Long a Date
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}