package com.example.misfinanzas.views.home

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.misfinanzas.viewModels.home.HomeViewModel
import com.example.misfinanzas.views.add.AddView
import com.example.misfinanzas.views.dashboard.DashboardView
import com.example.misfinanzas.views.dashboard.EnterBalanceView
import com.example.misfinanzas.views.splash.SplashView
import com.google.firebase.auth.FirebaseAuth

sealed class HomeScreens(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : HomeScreens("dashboard", "Dashboard", Icons.Default.BarChart)
    object Tips : HomeScreens("tips", "Tips", Icons.Default.Lightbulb)
    object Suscriptions : HomeScreens("sucriptions", "Suscriptions", Icons.Default.Notifications)
    object Profile : HomeScreens("profile", "Profile", Icons.Default.Person)
    object Add : HomeScreens("add", "Add")
    object AddFirst : HomeScreens("addFirst", "Add")
    object EnterBalance : HomeScreens("enterBalance", "Enter Balance")
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

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchUserData(userId)
            viewModel.fetchBalance(userId)
            viewModel.checkAndApplyPendingTransactions(userId)
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
                    viewModel = viewModel
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
        HomeScreens.Suscriptions,
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

@Composable
fun TipsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Tips Screen", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Profile Screen",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSignOut() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}

@Composable
fun SuscriptionsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Suscriptions Screen", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val homeViewModel: HomeViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = viewModel.currentRoute,
        modifier = modifier
    ) {
        composable(HomeScreens.Dashboard.route) {
            DashboardView(viewModel = homeViewModel, navController = navController)
        }
        composable(HomeScreens.Tips.route) {
            TipsScreen()
        }
        composable(HomeScreens.Profile.route) {
            ProfileScreen(
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                }
            )
        }
        composable(HomeScreens.Suscriptions.route) {
            SuscriptionsScreen()
        }
        composable(HomeScreens.Add.route) {
            AddView(firstTime = false, viewModel = homeViewModel ,navController = navController)
        }
        composable(HomeScreens.AddFirst.route) {
            AddView(firstTime = true, navController = navController, viewModel = homeViewModel)
        }
        composable(HomeScreens.EnterBalance.route) {
            EnterBalanceView(viewModel = homeViewModel, navController = navController)
        }
    }
}