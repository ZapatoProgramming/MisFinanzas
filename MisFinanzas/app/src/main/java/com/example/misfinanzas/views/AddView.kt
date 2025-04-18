package com.example.misfinanzas.views

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
import com.example.misfinanzas.repositories.TransactionRepository
import com.example.misfinanzas.viewModels.AddViewModel
import com.example.misfinanzas.viewModels.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddView(
    viewModel: SharedViewModel,
    firstTime: Boolean = false,
    navController: NavController,
    addViewModel: AddViewModel = viewModel()
) {
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var frequencyMenuExpanded by remember { mutableStateOf(false) }
    var categoriesExpanded by remember { mutableStateOf(false)}

    val categories by viewModel.categoriesNamesState.collectAsState()
    var selectedCategory by remember { mutableStateOf("") }


    val buttonColor = when (addViewModel.transactionType) {
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
                    onClick = { typeMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(addViewModel.transactionType, color = Color.White)
                }
                DropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    listOf("Gasto", "Ingreso").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion, color = Color.Black) },
                            onClick = {
                                addViewModel.updateTransactionType(opcion)
                                typeMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresar ${addViewModel.transactionType}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8F8F2)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = { categoriesExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCategory.ifEmpty { "Seleccionar Categoría" }, color = Color.White)
                }
                DropdownMenu(
                    expanded = categoriesExpanded,
                    onDismissRequest = { categoriesExpanded = false },
                    modifier = Modifier.fillMaxWidth().background(Color.White)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, color = Color.Black) },
                            onClick = {
                                selectedCategory = category
                                addViewModel.updateCategory(category)
                                categoriesExpanded = false
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("+ ", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(
                                    "Agregar nueva categoría",
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                    color = Color.Black
                                )
                            }
                        },
                        onClick = {
                            categoriesExpanded = false
                            navController.navigate(HomeScreens.CreateCategory.route)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = addViewModel.description,
                onValueChange = { addViewModel.updateDescription(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = addViewModel.amount,
                onValueChange = { addViewModel.updateAmount(it) },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Es de hoy?", color = Color.White)
                Checkbox(
                    checked = addViewModel.isToday,
                    onCheckedChange = { addViewModel.toggleIsToday(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = addViewModel.day,
                    onValueChange = { if (!addViewModel.isToday) addViewModel.day = it },
                    label = { Text("Día") },
                    enabled = !addViewModel.isToday,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = addViewModel.month,
                    onValueChange = { if (!addViewModel.isToday) addViewModel.month = it },
                    label = { Text("Mes") },
                    enabled = !addViewModel.isToday,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = addViewModel.year,
                    onValueChange = { if (!addViewModel.isToday) addViewModel.year = it },
                    label = { Text("Año") },
                    enabled = !addViewModel.isToday,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Es una suscripción?", color = Color.White)
                Checkbox(
                    checked = addViewModel.isSubscription,
                    onCheckedChange = { addViewModel.toggleIsSubscription(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (addViewModel.isSubscription) {
                Text("Frecuencia de suscripción", color = Color.White)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { frequencyMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(addViewModel.subscriptionFrequency, color = Color.White)
                    }
                    DropdownMenu(
                        expanded = frequencyMenuExpanded,
                        onDismissRequest = { frequencyMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth().background(Color.White)
                    ) {
                        addViewModel.frequencyOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.Black) },
                                onClick = {
                                    addViewModel.updateSubscriptionFrequency(option)
                                    frequencyMenuExpanded = false
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
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            addViewModel.createTransaction(userId)
                            val repository = TransactionRepository()
                            val date = repository.getTransactionDate(addViewModel.isToday,
                                addViewModel.day, addViewModel.month, addViewModel.year)
                            viewModel.applyTransactionIfNeeded(addViewModel.transactionType,
                                addViewModel.amount, date)
                            viewModel.userHasAddedFirstTransaction(userId)
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