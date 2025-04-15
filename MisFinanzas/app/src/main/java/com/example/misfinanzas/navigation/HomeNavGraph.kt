package com.example.misfinanzas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.misfinanzas.viewModels.HomeViewModel
import com.example.misfinanzas.views.AddView
import com.example.misfinanzas.views.DashboardView
import com.example.misfinanzas.views.EnterBalanceView
import com.example.misfinanzas.views.HomeScreens
import com.example.misfinanzas.views.ProfileView
import com.example.misfinanzas.views.SubscriptionsView
import com.example.misfinanzas.views.TipsView
import com.google.firebase.auth.FirebaseAuth

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
            TipsView()
        }
        composable(HomeScreens.Profile.route) {
            ProfileView(
                onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                }
            )
        }
        composable(HomeScreens.Subscriptions.route) {
            SubscriptionsView(viewModel = homeViewModel)
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