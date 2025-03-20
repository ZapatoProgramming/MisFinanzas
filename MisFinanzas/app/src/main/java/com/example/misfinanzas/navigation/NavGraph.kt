package com.example.misfinanzas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.views.login.LoginView
import com.example.misfinanzas.views.signup.SignUpView

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.Login.route) {
        composable(AppScreens.Login.route) {
            LoginView(
                onLoginSuccess = {
                    // Navegar a la pantalla principal después del inicio de sesión
                },
                onNavigateToSignUp = {
                    // Navegar a la pantalla de registro
                    navController.navigate(AppScreens.SignUp.route)
                }
            )
        }
        composable(AppScreens.SignUp.route) {
            SignUpView(
                onSignUpSuccess = {
                    // Navegar a la pantalla principal después del registro
                },
                onNavigateToLogin = {
                    // Volver a la pantalla de inicio de sesión
                    navController.popBackStack()
                }
            )
        }
    }
}