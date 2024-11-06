package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lab1.GameButtonType
import com.example.lab1.GameApplication
import com.example.lab1.NBackHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import androidx.compose.ui.graphics.Color
import java.util.Locale

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int
    val isVisualButtonClicked: State<Boolean>
    val isAudioButtonClicked: State<Boolean>
    val visualButtonColor: State<Color>
    val audioButtonColor: State<Color>

    fun setGameType(gameType: GameType)
    fun startGame()

    fun checkMatch(gameButtonType: GameButtonType)
}

class GameVM(
    val userPreferencesRepository: UserPreferencesRepository,
    val context: Context
): GameViewModel, ViewModel(), OnInitListener {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 2

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var VisualArray = emptyArray<Int>()  // Array with all events
    private var AudioArray = emptyArray<Int>()
    private var VisualArrayPosition = -1
    private var AudioArrayPosition = -1
    private var _isVisualButtonClicked = mutableStateOf(true)
    override val isVisualButtonClicked: State<Boolean> get() = _isVisualButtonClicked
    private var _isAudioButtonClicked = mutableStateOf(true)
    override val isAudioButtonClicked: State<Boolean> get() = _isAudioButtonClicked

    private var tts: TextToSpeech? = null

    private var _visualButtonColor = mutableStateOf(Color.Gray)
    private var _audioButtonColor = mutableStateOf(Color.Gray)
    override val visualButtonColor: State<Color> get() = _visualButtonColor
    override val audioButtonColor: State<Color> get() = _audioButtonColor

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        _score.value = 0


        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        when(gameState.value.gameType){
            GameType.Audio -> {
                AudioArrayPosition = -1
                AudioArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
            }
            GameType.Visual -> {
                VisualArrayPosition = -1
                VisualArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
            }
            GameType.AudioVisual -> {
                VisualArrayPosition = -1
                AudioArrayPosition = -1
                VisualArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
                AudioArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
            }
        }
        //VisualArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        //Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")
        //AudioArray = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()



        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame()
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(VisualArray)
            }
            // Todo: update the highscore
        }
    }

    override fun checkMatch(gameButtonType: GameButtonType) {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */

        Log.d("GameVM", "Current score: ${_score.value}")

        when(gameButtonType){
            GameButtonType.Visual -> {
                if(VisualArrayPosition - nBack >= 0){
                    _isVisualButtonClicked.value = true
                    if(VisualArray[VisualArrayPosition - nBack] == this.gameState.value.eventValue){
                        _score.value = _score.value + 1
                        _visualButtonColor.value = Color.Green.copy(alpha = 0.4f)
                    }
                    else{
                        _visualButtonColor.value = Color.Red.copy(alpha = 0.5f)
                    }
                }
            }
            GameButtonType.Audio -> {
                if(AudioArrayPosition - nBack >= 0){
                    _isAudioButtonClicked.value = true
                    if(AudioArray[AudioArrayPosition - nBack] == gameState.value.eventValue){
                        _score.value = _score.value + 1
                        _audioButtonColor.value = Color.Green.copy(alpha = 0.5f)
                    }
                    else{
                        _audioButtonColor.value = Color.Red.copy(alpha = 0.5f)
                    }
                }
            }
        }
    }


    private suspend fun runAudioGame() {
        // Todo: Make work for Basic grade

        for (value in AudioArray) {

            resetButtons()
            AudioArrayPosition++
            // Spela upp ljudet med TTS för varje värde i AudioArray
            speakOut(value)  // Detta spelar upp A, B osv.
            _gameState.value = _gameState.value.copy(eventValue = value)
            // Vänta lite mellan varje ljud, t.ex. 2 sekunder
            delay(eventInterval)
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        // Todo: Replace this code for actual game code
        //delay(eventInterval)
        for (value in events) {
            //Log.d("GameVM", "Setting eventValue to $value")  // Lägg till denna logg
            resetButtons()
            VisualArrayPosition++
            _gameState.value = _gameState.value.copy(eventValue = value)
            delay(eventInterval)
        }

    }

    private fun resetButtons(){
        _isVisualButtonClicked.value = false
        _isAudioButtonClicked.value = false
    }

    private fun runAudioVisualGame(){
        // Todo: Make work for Higher grade
    }

    private fun speakOut(value: Int) {
        val textToSpeak = when (value) {
            1 -> "A"
            2 -> "B"
            3 -> "C"
            4 -> "D"
            5 -> "E"
            6 -> "F"
            7 -> "G"
            8 -> "H"
            9 -> "I"
            10 -> "J"
            11 -> "K"
            12 -> "L"
            13 -> "M"
            14 -> "N"
            15 -> "O"
            16 -> "P"
            17 -> "Q"
            18 -> "R"
            19 -> "S"
            20 -> "T"
            21 -> "U"
            22 -> "V"
            23 -> "W"
            24 -> "X"
            25 -> "Y"
            26 -> "Z"
            // Lägg till fler regler om det finns fler värden
            else -> "Unknown"
        }

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Ange språk för TTS (t.ex. engelska)
            tts?.setLanguage(Locale.US)
        } else {
            Log.e("GameVM", "TTS Initialization failed")
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                if (application !is GameApplication) {
                    throw IllegalStateException("Expected GameApplication but got ${application?.javaClass?.simpleName}")
                }
                GameVM(application.userPreferencesRespository, application.applicationContext)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        tts = TextToSpeech(context, this)
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }

    }
}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1,  // The value of the array string
    //val currentGameState: CurrentGameState = CurrentGameState.HomeSite
)

class FakeVM: GameViewModel{
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2
    override val isVisualButtonClicked: State<Boolean> get() = mutableStateOf(true)
    override val isAudioButtonClicked: State<Boolean> get() = mutableStateOf(true)
    override val visualButtonColor: State<Color> get() = mutableStateOf(Color.Green)
    override val audioButtonColor: State<Color> get() = mutableStateOf(Color.Red)

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch(gameButtonType: GameButtonType) {
    }
}