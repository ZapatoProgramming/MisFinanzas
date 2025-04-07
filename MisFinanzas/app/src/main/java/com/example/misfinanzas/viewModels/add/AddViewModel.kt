package com.example.misfinanzas.viewModels.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.misfinanzas.models.AddModel

class AddViewModel : ViewModel() {
    var cantidad by mutableStateOf("")
        private set
    var categoria by mutableStateOf("")
        private set
    var dia by mutableStateOf("")
    var mes by mutableStateOf("")
    var anio by mutableStateOf("")
    var esSubscripcion by mutableStateOf(false)
        private set
    var frecuenciaSubscripcion by mutableStateOf("Mensual")
        private set
    var esHoy by mutableStateOf(false)
        private set
    var tipoTransaccion by mutableStateOf("Gasto")
        private set

    val opcionesFrecuencia = listOf("Mensual", "Anual")

    fun updateCantidad(value: String) {
        cantidad = value
    }

    fun updateCategoria(value: String) {
        categoria = value
    }

    fun toggleEsHoy(value: Boolean) {
        esHoy = value
        if (value) {
            val (diaHoy, mesHoy, anioHoy) = AddModel.getCurrentDate()
            dia = diaHoy
            mes = mesHoy
            anio = anioHoy
        }
    }

    fun updateFrecuenciaSubscripcion(value: String) {
        frecuenciaSubscripcion = value
    }

    fun toggleEsSubscripcion(value: Boolean) {
        esSubscripcion = value
    }

    fun updateTipoTransaccion(value: String) {
        tipoTransaccion = value
    }

}