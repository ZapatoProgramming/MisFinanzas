package com.example.misfinanzas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.misfinanzas.viewModels.HomeViewModel
import com.example.misfinanzas.viewModels.SharedViewModel
import com.example.misfinanzas.views.AddView
import com.example.misfinanzas.views.CreateCategoryView
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
    homeViewModel: HomeViewModel,
    sharedViewModel: SharedViewModel
) {

    NavHost(
        navController = navController,
        startDestination = homeViewModel.currentRoute,
        modifier = modifier
    ) {
        composable(HomeScreens.Dashboard.route) {
            DashboardView(viewModel = sharedViewModel, navController = navController)
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
            SubscriptionsView()
        }
        composable(HomeScreens.Add.route) {
            AddView(firstTime = false, viewModel =  sharedViewModel, navController = navController)
        }
        composable(HomeScreens.AddFirst.route) {
            AddView(firstTime = true, navController = navController, viewModel = sharedViewModel)
        }
        composable(HomeScreens.EnterBalance.route) {
            EnterBalanceView(viewModel = sharedViewModel, navController = navController)
        }
        composable(HomeScreens.CreateCategory.route){
            CreateCategoryView(navController = navController, sharedViewModel = sharedViewModel)
        }
    }
}