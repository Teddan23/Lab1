package com.example.lab1

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lab1.ui.screens.GameScreen
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

@Composable
fun NavGraph(navController: NavHostController, userPreferencesRepository: UserPreferencesRepository) {
    NavHost(navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(vm = GameVM(userPreferencesRepository), navController = navController) // Pass UserPreferencesRepository to GameVM
        }
        composable(Routes.GAME) {
            GameScreen(vm = GameVM(userPreferencesRepository)) // Pass UserPreferencesRepository to GameVM
        }
    }
}

// Define your routes
object Routes {
    const val HOME = "home"
    const val GAME = "game"
}