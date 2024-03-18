package com.sagar.snake.presentation.snake

import kotlin.random.Random

data class SnakeScreenState(
    val isLoading: Boolean = false,
    val score: Int = 0,
    val gameState: GameState = GameState.IDLE,
    val currentFoodCoordinate: Coordinate = generateRandomFoodCoordinate(),
    val currentDirection: Direction = Direction.UP,
    val snakeCoordinates: List<Coordinate> = listOf(Coordinate(x = 6, y = 5)),
    val isGameOver: Boolean = false
) {
    companion object {
        fun generateRandomFoodCoordinate(): Coordinate {
            return Coordinate(
                x = Random.nextInt(from = 1, until = 19),
                y = Random.nextInt(from = 1, until = 19)
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