package com.example.misfinanzas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.auth.FirebaseAuthService
import com.example.misfinanzas.views.HomeView
import com.example.misfinanzas.views.LoginView
import com.example.misfinanzas.views.SignUpView
import com.example.misfinanzas.views.SplashView

@Composable
fun NavGraph(firebaseAuthService: FirebaseAuthService) {
    val navController = rememberNavController()

    val userState = firebaseAuthService.currentUser.collectAsState()
    val user = userState.value

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate(AppScreens.NavigationHome.route) {
                popUpTo(AppScreens.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(AppScreens.Login.route) {
                popUpTo(AppScreens.NavigationHome.route) { inclusive = true }
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
                    navController.navigate(AppScreens.NavigationHome.route)
                },
                onNavigateToSignUp = {
                    navController.navigate(AppScreens.SignUp.route)
                }
            )
        }
        composable(AppScreens.SignUp.route) {
            SignUpView(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppScreens.NavigationHome.route){
            HomeView()
        }
    }
}