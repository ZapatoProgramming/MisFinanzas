package com.example.misfinanzas.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misfinanzas.views.HomeScreens
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    val navigationCommands = _navigationCommands.asSharedFlow()

    private val _currentRoute = mutableStateOf(HomeScreens.Dashboard.route)
    val currentRoute: String get() = _currentRoute.value

    fun navigateTo(route: String) {
        _currentRoute.value = route
        viewModelScope.launch { _navigationCommands.emit(NavigationCommand.NavigateTo(route)) }
    }

    sealed class NavigationCommand {
        data class NavigateTo(val route: String) : NavigationCommand()
        data object PopBackStack : NavigationCommand()
    }

}