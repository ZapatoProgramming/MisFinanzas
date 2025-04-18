package com.example.misfinanzas.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.misfinanzas.components.PieChart
import com.example.misfinanzas.room.TransactionEntity
import com.example.misfinanzas.viewModels.DashboardViewModel
import com.example.misfinanzas.viewModels.SharedViewModel

@Composable
fun DashboardView(viewModel: SharedViewModel, navController: NavController,
                  dashboardViewModel: DashboardViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize().fillMaxHeight().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { dashboardViewModel.navigateToPreviousMonth() }) {
                Text("◀", color = Color.White, fontSize = 20.sp)
            }
            Text(dashboardViewModel.selectedMonth, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { dashboardViewModel.navigateToNextMonth() }) {
                Text("▶", color = Color.White, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.tertiary, shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ingreso", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Gastos", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("0", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Saldo", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("${viewModel.balance}", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        if(!(viewModel.hasEnteredBalance && viewModel.hasAddedFirstTransaction)) {
            FloatingActionButton(
                onClick = {
                    when {
                        !viewModel.hasEnteredBalance -> {
                            navController.navigate(HomeScreens.EnterBalance.route)
                        }

                        !viewModel.hasAddedFirstTransaction -> {
                            navController.navigate(HomeScreens.AddFirst.route)
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = "+",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    viewModel.message,
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else{
            val transactions = dashboardViewModel.getAllTransactions().collectAsState(initial = emptyList())
            val groupedByCategoryWhereTypeIsExpense = transactions.value
                .filter { it.type == "Gasto" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }

            val groupedByCategoryWhereTypeIsIncome = transactions.value
                .filter { it.type == "Ingreso" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount }.toInt() }
                Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                // Primer PieChart ocupando la mitad del ancho
                Box(
                    modifier = Modifier
                        .weight(1f)  // Ocupa la mitad del ancho
                        .padding(8.dp)  // Opcional: agregar un poco de espacio alrededor
                ) {
                    PieChart(data = groupedByCategoryWhereTypeIsExpense)  // Usando tu PieChart con datos de Gasto
                }

                // Segundo PieChart ocupando la otra mitad del ancho
                Box(
                    modifier = Modifier
                        .weight(1f)  // Ocupa la otra mitad del ancho
                        .padding(8.dp)  // Opcional: agregar un poco de espacio alrededor
                ) {
                    PieChart(data = groupedByCategoryWhereTypeIsIncome)  // Usando tu PieChart con datos de Ingreso
                }
            }

                TransactionCards(transactions = transactions)
        }
    }
}

@Composable
fun EnterBalanceView(viewModel: SharedViewModel = viewModel(), navController: NavController) {
    var balance by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atras",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
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
                text = "Ingresa tu saldo actual",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Saldo", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateBalance(balance.toDoubleOrNull() ?: 0.0)
                    navController.navigate(HomeScreens.AddFirst.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun TransactionCards(transactions: State<List<TransactionEntity>>){
    transactions.value.forEach { transaction ->
        TransactionCard(transaction = transaction)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TransactionCard(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = transaction.category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = transaction.description,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tipo: ${transaction.type}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Monto: $${transaction.amount}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}