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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeView() {
    var selectedMonth by remember { mutableStateOf("Enero") }
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    var currentIndex by remember { mutableStateOf(0) }

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
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFFEFEBD1), shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ingreso",  fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Gastos",  fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Saldo", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0",  fontWeight = FontWeight.Bold, color = Color.Black)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Texto de indicación
        Text("Crea tu primer historial de ingresos y de gastos", color = Color.White, fontSize = 30.sp)

        Spacer(modifier = Modifier.weight(1f))

        // Hay que poner la barra de navegación inferior en una clase aparte para no estar creandola cada vez que se crea una vista



    }
}
