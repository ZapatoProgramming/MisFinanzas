package com.example.misfinanzas.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object SignUp : AppScreens("signup")
}