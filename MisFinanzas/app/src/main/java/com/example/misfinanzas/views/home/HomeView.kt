package com.example.misfinanzas.home.HomeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.views.add.AddView
import com.example.misfinanzas.views.dashboard.DashboardView

sealed class HomeScreens(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : HomeScreens("dashboard", "Dashboard", Icons.Default.BarChart)
    object Tips : HomeScreens("tips", "Tips", Icons.Default.Lightbulb)
    object Suscriptions : HomeScreens("sucriptions", "Suscriptions", Icons.Default.Notifications)
    object Profile : HomeScreens("profile", "Profile", Icons.Default.Person)
    object Add : HomeScreens("add", "Add")
}

@Composable
fun HomeView() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { CustomBottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // Aplicamos el innerPadding al contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Usamos el innerPadding aquí
        ) {
            HomeNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    val screens = listOf(
        HomeScreens.Dashboard,
        HomeScreens.Tips,
        HomeScreens.Add,
        HomeScreens.Suscriptions,
        HomeScreens.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        // Primeros dos ítems (izquierda)
        screens.take(2).forEach { screen ->
            NavigationBarItem(screen, currentRoute, navController)
        }
        val addSelected = currentRoute == screens[2].route
        // Botón central destacado
        val iconSize = 54.dp
        Box(
            modifier = Modifier
                .size(iconSize) // Tamaño mayor para el botón central
                .clickable {
                    navController.navigate(HomeScreens.Add.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle, // Ícono de Material Icons
                contentDescription = "Add",
                tint = if (addSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier.size(iconSize) // Ícono más grande
            )
        }

        // Últimos dos ítems (derecha)
        screens.takeLast(2).forEach { screen ->
            NavigationBarItem(screen, currentRoute, navController)
        }
    }
}

@Composable
private fun NavigationBarItem(
    screen: HomeScreens,
    currentRoute: String?,
    navController: NavHostController
) {
    val isSelected = currentRoute == screen.route
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                if (!isSelected) {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
    ) {
        Icon(
            imageVector = screen.icon!!,
            contentDescription = screen.title,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TipsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Tips Screen", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile Screen", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun SuscriptionsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Suscriptions Screen", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun HomeNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = HomeScreens.Dashboard.route,
        modifier = modifier
    ) {
        composable(HomeScreens.Dashboard.route) {
            DashboardView()
        }
        composable(HomeScreens.Tips.route) {
            TipsScreen()
        }
        composable(HomeScreens.Profile.route) {
            ProfileScreen()
        }
        composable(HomeScreens.Suscriptions.route) {
            SuscriptionsScreen()
        }
        composable(HomeScreens.Add.route) {
            AddView()
        }
    }
}