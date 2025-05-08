package com.example.misfinanzas.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.misfinanzas.components.ColorPickerDialog
import com.example.misfinanzas.viewModels.CreateCategoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CreateCategoryView(navController: NavController, viewModel: CreateCategoryViewModel = viewModel()) {
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
    var showColorPicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.White) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear Nueva Categoría",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para el nombre de la categoría
        TextField(
            value = viewModel.categoryName,
            onValueChange = { viewModel.updateCategoryName(it) },
            label = { Text("Nombre de la categoría") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de color
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Color de la categoría:", modifier = Modifier.weight(1f))
            IconButton(
                onClick = { showColorPicker = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Seleccionar color",
                    tint = selectedColor
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para la descripción de la categoría
        TextField(
            value = viewModel.categoryDescription,
            onValueChange = { viewModel.updateCategoryDescription(it) },
            label = { Text("Descripción de la categoría") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar la categoría
        Button(
            onClick = {
                viewModel.updateCategoryColor(selectedColor.toHexString())
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Guardar Categoría", color = Color.White)
        }

        if (showColorPicker) {
            ColorPickerDialog(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    selectedColor = color
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }

        Text(selectedColor.toHexString())
    }
}



// Función para convertir Color a String hexadecimal
fun Color.toHexString(): String {
    return String.format(
        "#%02X%02X%02X",
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt()
    )
}