package com.sagar.snake.presentation.snake

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SnakeScreenViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(SnakeScreenState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: SnakeScreenEvent) {
        when (event) {

            SnakeScreenEvent.OnStart -> {
                _uiState.update { it.copy(gameState = GameState.STARTED) }
            }

            SnakeScreenEvent.OnPause -> {
                _uiState.update { it.copy(gameState = GameState.PAUSED) }
            }

            SnakeScreenEvent.OnExit -> {

            }

            SnakeScreenEvent.OnRestart -> {
                _uiState.value = SnakeScreenState()
            }
        }
    }
}