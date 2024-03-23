package com.sagar.snake.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LocalDataStore {

    companion object {
        val userScoreKey = intPreferencesKey(name = "user_score_key")
    }

    override suspend fun getScore(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[userScoreKey] ?: 0
        }
    }

    override suspend fun saveScore(newScore: Int) {
        dataStore.edit { preferences ->
            preferences[userScoreKey] = newScore
        }
    }
}