package com.example.misfinanzas.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

@Composable
fun PieChart(
    data: Map<String, Pair<Int, String>>, // Nuevo formato: nombre -> (monto, color)
    radiusOuter: Dp = 50.dp,
    chartBarWidth: Dp = 35.dp,
    animDuration: Int = 1000,
) {
    val totalSum = data.values.sumOf { it.first } // Suma de los montos
    val floatValue = mutableListOf<Float>()

    // Calcular el ángulo de cada segmento
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.first.toFloat() / totalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    // Animación del tamaño del gráfico
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // Animación de rotación del gráfico
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // Reproducir la animación solo una vez
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gráfico circular usando Canvas
        Box(
            modifier = Modifier.size(animateSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
                    .rotate(animateRotation)
            ) {
                // Dibujar cada segmento del gráfico
                floatValue.forEachIndexed { index, value ->
                    val color = Color(data.values.elementAt(index).second.toColorInt())
                    drawArc(
                        color = color,
                        startAngle = lastValue,
                        sweepAngle = value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }

        // Mostrar los detalles del gráfico
        DetailsPieChart(
            data = data
        )
    }
}

@Composable
fun DetailsPieChart(
    data: Map<String, Pair<Int, String>> // Nuevo formato: nombre -> (monto, color)
) {
    Column(
        modifier = Modifier
            .padding(top = 80.dp)
            .fillMaxWidth()
    ) {
        data.forEach { (categoryName, categoryData) ->
            val (amount, colorHex) = categoryData
            DetailsPieChartItem(
                data = Pair(categoryName, amount),
                color = Color(colorHex.toColorInt())
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<String, Int>,
    height: Dp = 8.dp,
    color: Color
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 10.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),  // Añadir padding en el Row
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Caja con color ajustado dinámicamente
            Box(
                modifier = Modifier
                    .size(height)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            // Columna que contiene el texto
            Column(
                modifier = Modifier
                    .weight(1f)  // Ajusta automáticamente el espacio disponible para el texto
                    .padding(start = 16.dp)
            ) {
                // Texto de la categoría (String) ajustado en una sola línea
                Text(
                    text = data.first,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()  // El texto ocupará el espacio disponible
                        .padding(end = 8.dp),  // Espaciado para evitar que se pegue al borde
                    maxLines = 1,  // Impide que el texto se divida en varias líneas
                )
                // Texto del valor (Int)
                Text(
                    text = data.second.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()  // El texto ocupará el espacio disponible
                )
            }
        }
    }
}


