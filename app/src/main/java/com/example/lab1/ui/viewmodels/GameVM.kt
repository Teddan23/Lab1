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
import com.example.lab1.Routes
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
    val eventInterval: Int
    val eventCount: Int
    val possibleAudioOutput: Int
    val visualGameMode: Int

    fun setGameType(gameType: GameType)
    fun startGame()

    fun checkMatch(gameButtonType: GameButtonType)

    fun updateNBack(newValue: Int)
    fun updateEventInterval(newValue: Int)
    fun updateEventCount(newValue: Int)

    fun updateVisualGameMode(newMode: Int)
    fun updatePossibleAudioOutput(newValue: Int)

    fun saveSettings()

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

    private val _nBack = mutableStateOf(2)
    override val nBack: Int get() = _nBack.value

    private var job: Job? = null
    private val _eventInterval = mutableStateOf(2000)                    //2sekunder
    override val eventInterval: Int get() = _eventInterval.value

    private val _eventCount = mutableStateOf(10)
    override val eventCount: Int get() = _eventCount.value

    private val nBackHelper = NBackHelper()                                     // Helper that generate the event array
    private var VisualArray = emptyArray<Int>()
    private var AudioArray = emptyArray<Int>()
    private var VisualArrayPosition = -1
    private var AudioArrayPosition = -1
    private var _isVisualButtonClicked = mutableStateOf(true)
    override val isVisualButtonClicked: State<Boolean> get() = _isVisualButtonClicked
    private var _isAudioButtonClicked = mutableStateOf(true)
    override val isAudioButtonClicked: State<Boolean> get() = _isAudioButtonClicked

    private var _possibleAudioOutput = mutableStateOf(10)
    override val possibleAudioOutput: Int get() = _possibleAudioOutput.value

    private var _visualGameMode = mutableStateOf(3)
    override val visualGameMode: Int get() = _visualGameMode.value

    private var tts: TextToSpeech? = null

    private var _visualButtonColor = mutableStateOf(Color.Gray)
    private var _audioButtonColor = mutableStateOf(Color.Gray)
    override val visualButtonColor: State<Color> get() = _visualButtonColor
    override val audioButtonColor: State<Color> get() = _audioButtonColor

    override fun setGameType(gameType: GameType) {
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun updateNBack(newValue: Int) {
        _nBack.value = newValue
    }

    override fun updateEventInterval(newValue: Int) {
        _eventInterval.value = newValue
    }

    override fun updateEventCount(newValue: Int) {
        _eventCount.value = newValue
    }

    override fun updateVisualGameMode(newMode: Int) {
        _visualGameMode.value = newMode
    }

    override fun updatePossibleAudioOutput(newValue: Int) {
        _possibleAudioOutput.value = newValue
    }

    override fun saveSettings() {
        viewModelScope.launch {
            userPreferencesRepository.saveUserSettings(
                nBackValue = nBack,
                eventInterval = (eventInterval / 1000),
                eventCount = eventCount,
                visualGameMode = visualGameMode,
                possibleAudioOutput = possibleAudioOutput
            )
        }
    }


    override fun startGame() {
        job?.cancel()                                           // Cancel any existing game loop

        _score.value = 0

            when(gameState.value.gameType){
                GameType.Audio -> {
                    AudioArrayPosition = -1
                    AudioArray = nBackHelper.generateNBackString(eventCount, possibleAudioOutput, 30, nBack, 3).toList().toTypedArray()
                }
                GameType.Visual -> {
                    VisualArrayPosition = -1
                    VisualArray = nBackHelper.generateNBackString(eventCount, visualGameMode*visualGameMode, 30, nBack, 1).toList().toTypedArray()
                }
                GameType.AudioVisual -> {
                    VisualArrayPosition = -1
                    AudioArrayPosition = -1
                    VisualArray = nBackHelper.generateNBackString(eventCount, visualGameMode*visualGameMode, 30, nBack, 1).toList().toTypedArray()
                    AudioArray = nBackHelper.generateNBackString(eventCount, possibleAudioOutput, 30, nBack, 3).toList().toTypedArray()
                }

            }

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame()
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(VisualArray)
            }
            updateHighScoreIfNeeded(_score.value)
        }

    }

    override fun checkMatch(gameButtonType: GameButtonType) {

        Log.d("GameVM", "Current score: ${_score.value}")

        when(gameButtonType){
            GameButtonType.Visual -> {
                if(VisualArrayPosition - nBack >= 0){
                    _isVisualButtonClicked.value = true
                    if(VisualArray[VisualArrayPosition - nBack] == this.gameState.value.visualEventValue){
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
                    if(AudioArray[AudioArrayPosition - nBack] == gameState.value.audioEventValue){
                        _score.value = _score.value + 1
                        _audioButtonColor.value = Color.Green.copy(alpha = 0.4f)
                    }
                    else{
                        _audioButtonColor.value = Color.Red.copy(alpha = 0.5f)
                    }
                }
            }
        }
    }


    private suspend fun runAudioGame() {
        for (value in AudioArray) {

            resetButtons()
            AudioArrayPosition++
            speakOut(value)
            _gameState.value = _gameState.value.copy(
                audioEventValue = value,
                eventCounter = AudioArrayPosition+1)
            delay(eventInterval.toLong())
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        for (value in events) {
            resetButtons()
            VisualArrayPosition++
            _gameState.value = _gameState.value.copy(
                visualEventValue = value,
                eventCounter = VisualArrayPosition+1)
            delay(eventInterval.toLong())
        }

    }

    private fun resetButtons(){
        _isVisualButtonClicked.value = false
        _isAudioButtonClicked.value = false
    }

    private suspend fun runAudioVisualGame(){
        for(value in VisualArray){
            resetButtons()
            VisualArrayPosition++
            AudioArrayPosition++
            speakOut(AudioArray[AudioArrayPosition])
            _gameState.value = _gameState.value.copy(
                visualEventValue = value,
                audioEventValue = AudioArray[AudioArrayPosition],
                eventCounter = VisualArrayPosition+1)
            delay(eventInterval.toLong())
        }
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
            tts?.setLanguage(Locale.US)
        } else {
            Log.e("GameVM", "TTS Initialization failed")
        }
    }

    fun updateHighScoreIfNeeded(newScore: Int) {
        if (newScore > _highscore.value) {
            _highscore.value = newScore
            viewModelScope.launch {
                userPreferencesRepository.saveHighScore(newScore)
            }
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
        tts = TextToSpeech(context, this)
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.nBackFlow.collect {
                _nBack.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.eventIntervalFlow.collect {
                _eventInterval.value = it*1000
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.eventCountFlow.collect {
                _eventCount.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.visualGameModeFlow.collect{
                _visualGameMode.value = it
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.audioOutputFlow.collect{
                _possibleAudioOutput.value = it
            }
        }


        Log.d("MAKKA", "Read the settings: $_nBack, $_eventInterval, $_eventCount, $_visualGameMode, $_possibleAudioOutput")

    }
}

enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    val gameType: GameType = GameType.Visual,
    val visualEventValue: Int = -1,
    val audioEventValue: Int = -1,
    val eventCounter: Int = 0
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
    override val eventCount: Int get() = 1
    override val eventInterval: Int get() = 1
    override var possibleAudioOutput: Int = 10
    override val visualGameMode: Int = 3

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch(gameButtonType: GameButtonType) {
    }

    override fun updateNBack(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun updateEventInterval(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun updateEventCount(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun updateVisualGameMode(newMode: Int) {
        TODO("Not yet implemented")
    }

    override fun updatePossibleAudioOutput(newValue: Int) {
        TODO("Not yet implemented")
    }

    override fun saveSettings() {
        TODO("Not yet implemented")
    }
}