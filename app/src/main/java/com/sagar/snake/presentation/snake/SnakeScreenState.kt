package com.sagar.snake.presentation.snake

import kotlin.random.Random


data class SnakeScreenState(
    val bestScore: Int = 0,
    val gameState: GameState = GameState.IDLE,
    val currentFoodCoordinate: Coordinate = generateRandomFoodCoordinate(),
    val currentDirection: Direction = Direction.UP,
    val snakeCoordinates: List<Coordinate> = listOf(Coordinate(x = 6, y = 5)),
    val isGameOver: Boolean = false,
) {
    val score: Int
        get() = snakeCoordinates.size - 1

    val gameStateText: String
        get() = when (gameState) {
            GameState.STARTED -> { "Pause" }
            GameState.PAUSED -> { "Resume" }
            GameState.IDLE -> { "Start" }
        }

    companion object {
        fun generateRandomFoodCoordinate(): Coordinate {
            return Coordinate(
                x = Random.nextInt(from = 0, until = 19),
                y = Random.nextInt(from = 0, until = 19)
            )
        }
    }
}

data class Coordinate(
    val x: Int,
    val y: Int
)

enum class GameState {
    PAUSED,
    STARTED,
    IDLE
}

enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT
}