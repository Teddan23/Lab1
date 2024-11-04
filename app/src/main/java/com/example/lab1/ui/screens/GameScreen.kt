package com.example.lab1.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1.R
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM

@Composable
fun GameScreen(
    vm: GameViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow) // Bakgrundsfärg för skärmen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding runt hela kolumnen
            verticalArrangement = Arrangement.Center, // Centrerar elementen vertikalt
            horizontalAlignment = Alignment.CenterHorizontally // Centrerar elementen horisontellt
        ) {
            // Rad för att visa nuvarande poäng
            Text(
                text = "Current points: 0", // Du kan uppdatera poängen senare
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp) // Mellanrum under texten
            )

            // Anropar rutnätsfunktionen för att visa en 3x3 ruta
            NBackGrid(
                onCellClick = { row, col ->
                    //TODO: Hanterar cellklick genom att anropa GameViewModel-logik eller lagra vald position
                }
            )

            // Rad för knappar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    // Todo: change this button behaviour
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
                Button(
                    onClick = {
                        // Todo: change this button behaviour
                    }) {
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

@Composable
fun NBackGrid(
    onCellClick: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .size(300.dp) // Storlek på 3x3-rutan
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Mellanrum mellan varje rad
    ) {
        for (row in 0 until 3) { // Ytter loop för rader
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 3) { // Inre loop för kolumner
                    GridCell(row, col, onCellClick)
                }
            }
        }
    }
}

@Composable
fun GridCell(
    row: Int,
    col: Int,
    onCellClick: (Int, Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp) // Storlek för varje cell
            .background(Color.White)
            .padding(4.dp)
            .clickable { onCellClick(row, col) } // Hantera klick på cellen
    ) {
        // Ingen Text här, så cellerna förblir tomma
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    Surface {
        GameScreen(FakeVM())
    }
}