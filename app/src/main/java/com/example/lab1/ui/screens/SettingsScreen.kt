package com.example.lab1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: GameViewModel, navController: NavController) {

    // Används för att hålla de nya värdena lokalt i skärmen
    var nBackValue by remember { mutableStateOf(vm.nBack) }
    var eventIntervalValue by remember { mutableStateOf((vm.eventInterval / 1000).toInt()) }
    var eventCountValue by remember { mutableStateOf(vm.eventCount) }

    var possibleAudioOutputValue by remember { mutableStateOf(vm.possibleAudioOutput) }

    var selectedVisualMode by remember { mutableStateOf(vm.visualGameMode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFC7DEE4))
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color(0xFFE0F7FA))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Visar och redigerar n-back värdet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("n-back Value:", fontSize = 20.sp)
                        Row {
                            IconButton(onClick = { if (nBackValue > 1) nBackValue-- }) {
                                Text("-", fontSize = 20.sp)
                            }
                            Text("$nBackValue", fontSize = 20.sp)
                            IconButton(onClick = { nBackValue++ }) {
                                Text("+", fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visar och redigerar Event Interval värdet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Event Interval (s):", fontSize = 20.sp)
                        Row {
                            IconButton(onClick = { if (eventIntervalValue > 1) eventIntervalValue-- }) {
                                Text("-", fontSize = 20.sp)
                            }
                            Text("$eventIntervalValue", fontSize = 20.sp)
                            IconButton(onClick = { eventIntervalValue++ }) {
                                Text("+", fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visar och redigerar Event Count värdet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Number of Events:", fontSize = 20.sp)
                        Row {
                            IconButton(onClick = { if (eventCountValue > 0) eventCountValue-- }) {
                                Text("-", fontSize = 20.sp)
                            }
                            Text("$eventCountValue", fontSize = 20.sp)
                            IconButton(onClick = { eventCountValue++ }) {
                                Text("+", fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Visual Game Mode:", fontSize = 20.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (selectedVisualMode > 2) selectedVisualMode-- }
                        ) {
                            Text("-", fontSize = 20.sp)
                        }
                        Text("$selectedVisualMode x $selectedVisualMode", fontSize = 20.sp)
                        IconButton(
                            onClick = { if (selectedVisualMode < 6) selectedVisualMode++ }
                        ) {
                            Text("+", fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Max Letters (Antal bokstäver)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Max Letters:", fontSize = 20.sp)
                        Row {
                            IconButton(onClick = { if (possibleAudioOutputValue > 2) possibleAudioOutputValue-- }) {
                                Text("-", fontSize = 20.sp)
                            }
                            Text("$possibleAudioOutputValue", fontSize = 20.sp)
                            IconButton(onClick = { if (possibleAudioOutputValue < 26) possibleAudioOutputValue++ }) {
                                Text("+", fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Save Changes-knapp
                    Button(
                        onClick = {
                            // Uppdatera värden i GameVM
                            vm.updateNBack(nBackValue)
                            vm.updateEventInterval(eventIntervalValue * 1000L)
                            vm.updateEventCount(eventCountValue)
                            vm.updateVisualGameMode(selectedVisualMode)
                            vm.updatePossibleAudioOutput(possibleAudioOutputValue)
                            navController.popBackStack()  // Gå tillbaka efter att ha sparat
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    )
}
