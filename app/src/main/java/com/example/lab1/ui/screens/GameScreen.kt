package com.example.lab1.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType

@Composable
fun GameScreen(
    vm: GameViewModel,
    navController: NavController
) {
    // Observera gameState som tidigare
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current points: $score", // Uppdatera denna om du har en variabel för poäng
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )



            //if (gameState.eventValue != -1) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Current eventValue is: ${gameState.eventValue}",
                    textAlign = TextAlign.Center
                )

            //}

            if(gameState.gameType != GameType.Audio){
                NBackGrid(eventValue = gameState.eventValue)
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
                            containerColor = Color.Blue, // För enabled-knappen
                            disabledContainerColor = vm.audioButtonColor.value // Färg för disabled-knappen
                        )){
                        Icon(
                            painter = painterResource(id = R.drawable.sound_on),
                            contentDescription = "Sound",
                            modifier = Modifier
                                .height(48.dp)
                                .aspectRatio(3f / 2f)
                        )
                    }
                }
                if (gameState.gameType != GameType.Audio){
                    Button(
                        onClick = { vm.checkMatch(GameButtonType.Visual)},
                        enabled = !vm.isVisualButtonClicked.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue, // För enabled-knappen
                            disabledContainerColor = vm.visualButtonColor.value // Färg för disabled-knappen
                        ) ){
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

@Composable
fun NBackGrid(eventValue: Int) {
    Column(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var id = 1
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until 3) {
                    // Skicka vidare det aktuella eventValue till varje GridCell
                    GridCell(id = id, eventValue = eventValue)
                    id += 1
                }
            }
        }
    }
}

@Composable
fun GridCell(
    id: Int,
    eventValue: Int
) {
    // Debug-utskrift för att bekräfta när GridCell uppdateras
    //println("GridCell ID: $id, EventValue: $eventValue")

    // Ändra bakgrundsfärgen dynamiskt baserat på eventValue
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(if (id == eventValue) Color.Blue else Color.White)
            .padding(4.dp)
    ) {
        // Cellinnehåll kan vara tomt eller lägga till mer UI om det behövs
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