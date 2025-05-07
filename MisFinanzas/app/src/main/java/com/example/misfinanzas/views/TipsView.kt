package com.example.misfinanzas.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TipsView() {
    val tipsList = listOf(
        "📊 Regla 70/20/10: Usa el 70% de tus ingresos para gastos, 20% para ahorro y 10% para pagar deudas o invertir.",
        "💰 Ahorra al menos el 20% de tu sueldo mensual.",
        "🧾 Haz un presupuesto mensual y cúmplelo.",
        "🥪 Evita gastos innecesarios como comida diaria fuera de casa.",
        "📉 No gastes más de lo que ganas.",
        "📆 Revisa tus finanzas una vez por semana.",
        "🏦 Ten un fondo de emergencia de al menos 3 meses de salario.",
        "💳 Evita usar la tarjeta de crédito para cosas que no puedes pagar de contado.",
        "📚 Aprende sobre educación financiera constantemente."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Para permitir scroll
    ) {
        for (tip in tipsList) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = tip,
                    fontSize = 16.sp
                )
            }
        }
    }
}