package com.example.misfinanzas.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.navigation.HomeNavGraph
import com.example.misfinanzas.viewModels.HomeViewModel
import com.example.misfinanzas.viewModels.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class HomeScreens(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Dashboard : HomeScreens("dashboard", "Dashboard", Icons.Default.BarChart)
    data object Tips : HomeScreens("tips", "Tips", Icons.Default.Lightbulb)
    data object Subscriptions : HomeScreens("subscriptions", "Subscriptions", Icons.Default.Notifications)
    data object Profile : HomeScreens("profile", "Profile", Icons.Default.Person)
    data object Add : HomeScreens("add", "Add")
    data object AddFirst : HomeScreens("addFirst", "Add")
    data object EnterBalance : HomeScreens("enterBalance", "Enter Balance")
}

@Composable
fun HomeView(viewModel: HomeViewModel = viewModel()) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.navigationCommands.collect { command ->
            when (command) {
                is HomeViewModel.NavigationCommand.NavigateTo -> {
                    navController.navigate(command.route)
                }
                is HomeViewModel.NavigationCommand.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val sharedViewModel : SharedViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val isLoading by sharedViewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null) {
            sharedViewModel.fetchUserData(userId)
            sharedViewModel.checkData()
        }
    }

    Scaffold(
        bottomBar = { CustomBottomNavigationBar( viewModel = viewModel) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if(isLoading){
                SplashView()
            }
            else {
                HomeNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    homeViewModel = viewModel,
                    sharedViewModel = sharedViewModel
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    viewModel: HomeViewModel
) {
    val screens = listOf(
        HomeScreens.Dashboard,
        HomeScreens.Tips,
        HomeScreens.Add,
        HomeScreens.Subscriptions,
        HomeScreens.Profile
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        screens.take(2).forEach { screen ->
            NavigationBarItem(screen, viewModel.currentRoute, viewModel)
        }
        val addSelected = viewModel.currentRoute == screens[2].route
        val iconSize = 54.dp
        Box(
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    viewModel.navigateTo(HomeScreens.Add.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add",
                tint = if (addSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier.size(iconSize)
            )
        }
        screens.takeLast(2).forEach { screen ->
            NavigationBarItem(screen, viewModel.currentRoute, viewModel)
        }
    }
}

@Composable
private fun NavigationBarItem(
    screen: HomeScreens,
    currentRoute: String,
    viewModel: HomeViewModel
) {
    val isSelected = currentRoute == screen.route
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                if (!isSelected) {
                    viewModel.navigateTo(screen.route)
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