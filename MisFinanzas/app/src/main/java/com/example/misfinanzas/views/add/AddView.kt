package com.example.misfinanzas.views.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.misfinanzas.viewModels.home.HomeViewModel
import com.example.misfinanzas.views.home.HomeScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddView(
    viewModel: HomeViewModel,
    firstTime: Boolean = false,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }
    var firstTransactionAdded = viewModel.hasAddedFirstTransaction

    val buttonColor = when (viewModel.tipoTransaccion) {
        "Ingreso" -> MaterialTheme.colorScheme.primary
        "Gasto" -> Color.Red
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        if (firstTime) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tipo de transacción",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8F8F2),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor) // Color dinámico
                ) {
                    Text(viewModel.tipoTransaccion, color = Color.White)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    listOf("Gasto", "Ingreso").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                viewModel.updateTipoTransaccion(opcion)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Título dinámico según el tipo de transacción
            Text(
                text = "Ingresar ${viewModel.tipoTransaccion}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8F8F2)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = viewModel.cantidad,
                onValueChange = { viewModel.updateCantidad(it) },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = viewModel.categoria,
                onValueChange = { viewModel.updateCategoria(it) },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Es de hoy?", color = Color.White)
                Checkbox(
                    checked = viewModel.esHoy,
                    onCheckedChange = { viewModel.toggleEsHoy(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = viewModel.dia,
                    onValueChange = { if (!viewModel.esHoy) viewModel.dia = it },
                    label = { Text("Día") },
                    enabled = !viewModel.esHoy,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = viewModel.mes,
                    onValueChange = { if (!viewModel.esHoy) viewModel.mes = it },
                    label = { Text("Mes") },
                    enabled = !viewModel.esHoy,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = viewModel.anio,
                    onValueChange = { if (!viewModel.esHoy) viewModel.anio = it },
                    label = { Text("Año") },
                    enabled = !viewModel.esHoy,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Es una suscripción?", color = Color.White)
                Checkbox(
                    checked = viewModel.esSubscripcion,
                    onCheckedChange = { viewModel.toggleEsSubscripcion(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.esSubscripcion) {
                Text("Frecuencia de suscripción", color = Color.White)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(viewModel.frecuenciaSubscripcion, color = Color.White)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth().background(Color.White)
                    ) {
                        viewModel.opcionesFrecuencia.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.updateFrecuenciaSubscripcion(opcion)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (firstTime) {
                    Button(
                        onClick = { navController.navigate(HomeScreens.Dashboard.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(
                            "Aún no deseo agregar un gasto",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    onClick = {
                        navController.navigate(HomeScreens.Dashboard.route)
                        if(!firstTransactionAdded) viewModel.markFirstTransactionAdded()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            viewModel.createTransaction(userId)
                            navController.navigate(HomeScreens.Dashboard.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}