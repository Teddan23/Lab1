package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lab1.Routes
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController : NavController
) {
    val highscore by vm.highscore.collectAsState()
    val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()




    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(32.dp),
                text = "High-Score = $highscore",
                style = MaterialTheme.typography.headlineLarge
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        modifier = Modifier.padding(bottom = 16.dp),
                        onClick = {
                            vm.setGameType(GameType.Audio)
                            navController.navigate(Routes.GAME)
                            vm.startGame()
                        }) {
                        Text(text = "Test Audio")
                    }
                    Button(
                        modifier = Modifier.padding(bottom = 16.dp),
                        onClick = {
                            vm.setGameType(GameType.Visual)
                            navController.navigate(Routes.GAME)
                            vm.startGame()
                        }) {
                        Text(text = "Test Visual")
                    }
                    Button(
                        modifier = Modifier.padding(bottom = 50.dp),
                        onClick = {
                            vm.setGameType(GameType.AudioVisual)
                            navController.navigate(Routes.GAME)
                            vm.startGame()
                        }) {
                        Text(text = "Test both")
                    }
                    Button(
                        onClick = {
                            navController.navigate(Routes.SETTINGS)
                        }) {
                        Text(text = "Settings")
                    }
                }
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Hello there! :3 (makka)",
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val fakeNavController = rememberNavController()
    Surface {
        HomeScreen(FakeVM(), navController = fakeNavController)
    }
}