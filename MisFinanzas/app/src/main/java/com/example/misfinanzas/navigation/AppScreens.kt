package com.example.misfinanzas.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object SignUp : AppScreens("signup")
    object NavigationHome : AppScreens("navigationHome")
    object Splash : AppScreens("splash")
}