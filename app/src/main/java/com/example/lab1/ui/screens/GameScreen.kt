package com.example.lab1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lab1.GameButtonType
import com.example.lab1.R
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.ui.viewmodels.VisualGameMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    vm: GameViewModel,
    navController: NavController
) {
    // Observera gameState som tidigare
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current points: $score") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0F7FA))
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current visualEventValue: ${gameState.visualEventValue}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Current audioEventValue: ${gameState.audioEventValue}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // NBackGrid använder LazyVerticalGrid istället för Column
                    if (gameState.gameType != GameType.Audio) {
                        NBackGrid(gameState = gameState)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (gameState.gameType != GameType.Visual) {
                            Button(
                                onClick = { vm.checkMatch(GameButtonType.Audio) },
                                enabled = !vm.isAudioButtonClicked.value,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Blue,
                                    disabledContainerColor = vm.audioButtonColor.value
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.sound_on),
                                    contentDescription = "Sound",
                                    modifier = Modifier
                                        .height(48.dp)
                                        .aspectRatio(3f / 2f)
                                )
                            }
                        }
                        if (gameState.gameType != GameType.Audio) {
                            Button(
                                onClick = { vm.checkMatch(GameButtonType.Visual) },
                                enabled = !vm.isVisualButtonClicked.value,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Blue,
                                    disabledContainerColor = vm.visualButtonColor.value
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.visual),
                                    contentDescription = "Visual",
                                    modifier = Modifier
                                        .height(48.dp)
                                        .aspectRatio(3f / 2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NBackGrid(gameState: GameState) {
    // Bestäm storleken på grid beroende på spelets visualGameMode
    val mapBuilder: Int = if (gameState.visualGameMode == VisualGameMode.ThreeXThree) 3 else 5

    LazyVerticalGrid(
        columns = GridCells.Fixed(mapBuilder), // Ställ in antalet kolumner
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentPadding = PaddingValues(8.dp), // Lägg till padding mellan gridcellerna
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mapBuilder * mapBuilder) { index ->
            // Skicka vidare ID:t (index) och eventValue för varje cell
            GridCell(id = index + 1, eventValue = gameState.visualEventValue)
        }
    }
}

@Composable
fun GridCell(
    id: Int,
    eventValue: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f) // Gör cellerna kvadratiska
            .background(if (id == eventValue) Color.Blue else Color.White)
            .padding(4.dp)
    ) {
        // Cellinnehåll kan vara här, till exempel en text eller ikon
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    val fakeNavController = rememberNavController()
    Surface {
        GameScreen(FakeVM(), navController = fakeNavController)
    }
}
