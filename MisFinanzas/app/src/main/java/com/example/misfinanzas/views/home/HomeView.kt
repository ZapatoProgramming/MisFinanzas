package com.example.misfinanzas.home.HomeView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeView() {
    // Obtener el mes actual (0 = Enero, 11 = Diciembre)
    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    var currentIndex by remember { mutableStateOf(currentMonth) }
    var selectedMonth by remember { mutableStateOf(months[currentIndex]) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de mes
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (currentIndex > 0) {
                    currentIndex--
                    selectedMonth = months[currentIndex]
                }
            }) {
                Text("◀", color = Color.White, fontSize = 20.sp)
            }
            Text(selectedMonth, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                if (currentIndex < months.size - 1) {
                    currentIndex++
                    selectedMonth = months[currentIndex]
                }
            }) {
                Text("▶", color = Color.White, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Resumen de ingresos, gastos y saldo
        //--------------------------------------------------------------------------------------------------------------
        /* Falta hacer que el apartado de ingresos, gasto y saldo se actualize con la base de datos en tiempo real ahora
        ahora solo es un ejemplo visual */
        //--------------------------------------------------------------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFFEFEBD1), shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ingreso", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Gastos", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Saldo", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón central grande
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFEBD1))
                .clickable { }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 100.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Crea tu primer historial de ingresos y de gastos",
                color = Color.White,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    // vamos
}
