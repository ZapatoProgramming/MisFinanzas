package com.example.misfinanzas.navigation

sealed class AppScreens(val route: String) {
    data object Login : AppScreens("login")
    data object SignUp : AppScreens("signup")
    data object NavigationHome : AppScreens("navigationHome")
    data object Splash : AppScreens("splash")
}