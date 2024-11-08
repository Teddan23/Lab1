package com.example.lab1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lab1.GameButtonType
import com.example.lab1.R
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameState
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    vm: GameViewModel,
    navController: NavController
) {
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val visualGameMode by remember { mutableStateOf(vm.visualGameMode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Current points: $score") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
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
                        text = "${gameState.eventCounter}/${vm.eventCount}",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (gameState.gameType != GameType.Audio) {
                        NBackGrid(gameState = gameState, visualGameMode = visualGameMode)
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
fun NBackGrid(gameState: GameState, visualGameMode: Int) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(visualGameMode),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(visualGameMode * visualGameMode) { index ->
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
            .aspectRatio(1f)
            .background(if (id == eventValue) Color.Blue else Color.White)
            .padding(4.dp)
    ) {
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
