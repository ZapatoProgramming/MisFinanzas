package com.example.misfinanzas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.views.home.HomeView
import com.example.misfinanzas.views.login.LoginView
import com.example.misfinanzas.views.signup.SignUpView
import com.example.misfinanzas.views.splash.SplashView

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val firebaseAuthService = FirebaseAuthService()
    val isLoggedInState = firebaseAuthService.isLoggedIn.collectAsState()
    val isLoggedIn = isLoggedInState.value

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> {
                if (navController.currentDestination?.route != AppScreens.NavigationHome.route) {
                    navController.navigate(AppScreens.NavigationHome.route)
                }
            }
            false -> {
                if (navController.currentDestination?.route != AppScreens.Login.route) {
                    navController.navigate(AppScreens.Login.route)
                }
            }
            null -> {
                navController.navigate(AppScreens.Splash.route)
            }
        }
    }
    NavHost(navController = navController, startDestination = AppScreens.Login.route) {
        composable(AppScreens.Splash.route){
            SplashView()
        }
        composable(AppScreens.Login.route) {
            LoginView(
                onLoginSuccess = {
                    // Navegar a la pantalla principal después del inicio de sesión
                    navController.navigate(AppScreens.NavigationHome.route)
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
                    navController.navigate(AppScreens.Login.route)
                },
                onNavigateToLogin = {
                    // Volver a la pantalla de inicio de sesión
                    navController.popBackStack()
                }
            )
        }
        composable(AppScreens.NavigationHome.route){
            HomeView()
        }
    }
}