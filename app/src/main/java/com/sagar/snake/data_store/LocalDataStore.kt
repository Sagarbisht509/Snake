package com.sagar.snake.data_store

import kotlinx.coroutines.flow.Flow

interface LocalDataStore {

    suspend fun getScore(): Flow<Int>

    suspend fun saveScore(newScore: Int)
}