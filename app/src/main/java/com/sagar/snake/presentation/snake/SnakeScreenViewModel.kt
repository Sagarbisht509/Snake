package com.sagar.snake.presentation.snake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagar.snake.data_store.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnakeScreenViewModel @Inject constructor(
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SnakeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        getPreviousScore()
    }

    private fun getPreviousScore() = viewModelScope.launch {
        localDataStore.getScore().collectLatest { bestScore ->
            _uiState.update { it.copy(bestScore = bestScore) }
        }
    }

    fun changeGameState() = viewModelScope.launch {
        when (uiState.value.gameState) {
            GameState.IDLE -> onEvent(event = SnakeScreenEvent.OnStart)
            GameState.STARTED -> onEvent(event = SnakeScreenEvent.OnPause)
            GameState.PAUSED -> onEvent(event = SnakeScreenEvent.OnStart)
        }
    }

    fun onEvent(event: SnakeScreenEvent) {
        when (event) {

            SnakeScreenEvent.OnStart -> {
                _uiState.update { it.copy(gameState = GameState.STARTED) }
                viewModelScope.launch {
                    while (uiState.value.gameState == GameState.STARTED) {
                        val delayMillis = when (uiState.value.snakeCoordinates.size) {
                            in 1..5 -> 700L
                            in 6..10 -> 500L
                            else -> 300L
                        }
                        delay(delayMillis)
                        _uiState.value = updateGame(uiState.value)
                    }
                }
            }

            SnakeScreenEvent.OnPause -> _uiState.update { it.copy(gameState = GameState.PAUSED) }

            SnakeScreenEvent.OnRestart -> _uiState.value =
                SnakeScreenState(bestScore = uiState.value.bestScore)

            is SnakeScreenEvent.OnDirectionChanged -> {
                val currentDirection = _uiState.value.currentDirection
                if (!areOppositeDirections(currentDirection, event.direction)) {
                    _uiState.value = _uiState.value.copy(currentDirection = event.direction)
                }
            }
        }
    }

    private fun areOppositeDirections(dir1: Direction, dir2: Direction): Boolean {
        return when (dir1) {
            Direction.UP -> dir2 == Direction.DOWN
            Direction.DOWN -> dir2 == Direction.UP
            Direction.LEFT -> dir2 == Direction.RIGHT
            Direction.RIGHT -> dir2 == Direction.LEFT
        }
    }

    private suspend fun updateGame(gameState: SnakeScreenState): SnakeScreenState {

        val currentHead = gameState.snakeCoordinates.first()

        val newHead = when (gameState.currentDirection) {
            Direction.LEFT -> Coordinate(x = currentHead.x - 1, y = (currentHead.y))
            Direction.RIGHT -> Coordinate(x = currentHead.x + 1, y = (currentHead.y))
            Direction.UP -> Coordinate(x = currentHead.x, y = (currentHead.y - 1))
            Direction.DOWN -> Coordinate(x = currentHead.x, y = (currentHead.y + 1))
        }

        // game over
        if (gameState.snakeCoordinates.contains(newHead) || isTouchingBoundary(newHead)) {
            if (uiState.value.score > uiState.value.bestScore) {
                localDataStore.saveScore(uiState.value.score)
            }
            return gameState.copy(isGameOver = true)
        }

        var newSnakeCoordinates = mutableListOf(newHead) + gameState.snakeCoordinates
        val newFoodCoordinate =
            if (newHead == gameState.currentFoodCoordinate) SnakeScreenState.generateRandomFoodCoordinate()
            else gameState.currentFoodCoordinate

        if (newHead != gameState.currentFoodCoordinate) {
            newSnakeCoordinates = newSnakeCoordinates.toMutableList()
            newSnakeCoordinates.removeAt(newSnakeCoordinates.size - 1)
        }

        return gameState.copy(
            snakeCoordinates = newSnakeCoordinates,
            currentFoodCoordinate = newFoodCoordinate
        )
    }

    private fun isTouchingBoundary(newHead: Coordinate): Boolean =
        newHead.x < 0 || newHead.x > 19 || newHead.y < 0 || newHead.y > 19

}