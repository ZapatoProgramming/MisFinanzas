package com.example.misfinanzas.views.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.misfinanzas.R
import com.example.misfinanzas.viewModels.login.LoginViewModel

@Composable
fun LoginView(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), // ViewModel para la lógica de inicio de sesión
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    // Estado del formulario de inicio de sesión
    val loginForm by viewModel.loginForm.collectAsState()
    // Mensajes de error o éxito
    val loginMessage by viewModel.loginMessage.collectAsState()
    // Estado de visibilidad de la contraseña
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.logo), // Referencia a la imagen en drawable
            contentDescription = "logo", // Descripción para accesibilidad
            modifier = Modifier.size(300.dp) // Tamaño de la imagen
        )

        // Campo de correo electrónico
        TextField(
            value = loginForm.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        TextField(
            value = loginForm.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Ícono de visibilidad de contraseña
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de inicio de sesión
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp,
                    top = 8.dp, bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Iniciar Sesión")
        }

        // Enlace para recuperar contraseña
        TextButton(onClick = { /* Navegar a la pantalla de recuperación de contraseña */ }) {
            Text(text = "¿Olvidó su contraseña?",
                color = MaterialTheme.colorScheme.tertiary )

        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onNavigateToSignUp,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Crear Cuenta")
        }

        // Mostrar mensajes de error o éxito
        if (!loginMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = loginMessage!!,
                color = if (loginMessage == "Inicio de sesión exitoso.") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}