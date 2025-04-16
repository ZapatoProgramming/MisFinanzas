package com.example.misfinanzas.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misfinanzas.viewModels.SubscriptionsViewModel

@Composable
fun SubscriptionsView(viewModel: SubscriptionsViewModel = viewModel()) {
    val subscriptions = viewModel.getAllSubscriptions().collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Transacciones", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(subscriptions.value.size) { subscription ->
                Text(
                    text = "${subscriptions.value[subscription]}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}