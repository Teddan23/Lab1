package com.example.lab1

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository
import androidx.datastore.preferences.preferencesDataStore




private const val APP_PREFERENCES_NAME = "game_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_NAME
)

/*
* Custom app entry point for manual dependency injection
 */
class GameApplication: Application() {
    lateinit var userPreferencesRespository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRespository = UserPreferencesRepository(dataStore)
    }
}