package com.example.misfinanzas.views.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.misfinanzas.R
import com.example.misfinanzas.viewModels.signup.SignUpViewModel

@Composable
fun SignUpView(
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToLogin: () -> Unit
){
    // Estado del formulario de inicio de sesión
    val signupForm by viewModel.signupForm.collectAsState()
    // Mensajes de error o éxito
    val signupMessage by viewModel.signupMessage.collectAsState()
    // Estado de visibilidad de la contraseña
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()

    val isPasswordVisibleTwo by viewModel.isPasswordVisibleTwo.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(onClick = {
                onNavigateToLogin()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Ícono de flecha hacia atrás
                    contentDescription = "Volver al Login", // Descripción para accesibilidad
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.logo), // Referencia a la imagen en drawable
            contentDescription = "logo", // Descripción para accesibilidad
            modifier = Modifier.size(150.dp) // Tamaño de la imagen
        )

        // Campo de correo electrónico
        TextField(
            value = signupForm.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        TextField(
            value = signupForm.password,
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

        // Campo de contraseña
        TextField(
            value = signupForm.confirmPassword,
            onValueChange = { viewModel.updatePasswordTwo(it) },
            label = { Text("Confirmar Contraseña") },
            singleLine = true,
            visualTransformation = if (isPasswordVisibleTwo) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Ícono de visibilidad de contraseña
                IconButton(onClick = { viewModel.togglePasswordVisibilityTwo() }) {
                    Icon(
                        imageVector = if (isPasswordVisibleTwo) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (isPasswordVisibleTwo) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
