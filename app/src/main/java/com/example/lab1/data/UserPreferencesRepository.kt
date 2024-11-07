package mobappdev.example.nback_cimpl.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * This repository provides a way to interact with the DataStore api,
 * with this API you can save key:value pairs
 *
 * Currently this file contains only one thing: getting the highscore as a flow
 * and writing to the highscore preference.
 * (a flow is like a waterpipe; if you put something different in the start,
 * the end automatically updates as long as the pipe is open)
 *
 * Date: 25-08-2023
 * Version: Skeleton code version 1.0
 * Author: Yeetivity
 *
 */


class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val HIGHSCORE = intPreferencesKey("highscore")
        val N_BACK_KEY = intPreferencesKey("n_back_value")
        val EVENT_INTERVAL_KEY = intPreferencesKey("event_interval")
        val EVENT_COUNT_KEY = intPreferencesKey("event_count")
        val VISUAL_MODE_KEY = intPreferencesKey("visual_mode")
        val AUDIO_OUTPUT_KEY = intPreferencesKey("audio_output")
        const val TAG = "UserPreferencesRepo"
    }

    val highscore: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[HIGHSCORE] ?: 0
        }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[HIGHSCORE] = score
        }
    }

    suspend fun saveUserSettings(

        nBackValue: Int,
        eventInterval: Int,
        eventCount: Int,
        visualGameMode: Int,
        possibleAudioOutput: Int
    ) {
        //Log.d(TAG, "Saving userSetings: $nBackValue, $eventInterval, $eventCount, $visualGameMode, $possibleAudioOutput")
        dataStore.edit { preferences ->
            preferences[N_BACK_KEY] = nBackValue
            preferences[EVENT_INTERVAL_KEY] = eventInterval
            preferences[EVENT_COUNT_KEY] = eventCount
            preferences[VISUAL_MODE_KEY] = visualGameMode
            preferences[AUDIO_OUTPUT_KEY] = possibleAudioOutput
        }
    }

    val nBackFlow: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading nBack", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val value = preferences[N_BACK_KEY] ?: 5
            Log.d(TAG, "nBackFlow value: $value")  // Add the log here
            value
        }

    val eventIntervalFlow: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading eventInterval", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[EVENT_INTERVAL_KEY] ?: 1000 }

    val eventCountFlow: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading eventCount", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[EVENT_COUNT_KEY] ?: 10 }

    val visualGameModeFlow: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading visualGameMode", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[VISUAL_MODE_KEY] ?: 3 }

    val audioOutputFlow: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading possibleAudioOutput", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[AUDIO_OUTPUT_KEY] ?: 2 }
}