package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lab1.ui.theme.Lab1Theme
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instantiate the viewmodel
                    val gameViewModel: GameVM = viewModel(
                        factory = GameVM.Factory
                    )

                    // Create a NavController
                    val navController = rememberNavController()

                    // Define the navigation graph
                    NavGraph(navController, gameViewModel.userPreferencesRepository)
                }
            }
        }
    }
}