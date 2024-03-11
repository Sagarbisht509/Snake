package com.sagar.snake.presentation.snake

import kotlin.random.Random

data class SnakeScreenState(
    val isLoading: Boolean = false,
    val score: Int = 0,
    val direction: Direction = Direction.RIGHT
) {
    companion object {
        fun generateRandomFoodCoordinate(): Coordinate {
            return Coordinate(
                x = Random.nextInt(from = 1, until = 19),
                y = Random.nextInt(from = 1, until = 29)
            )
        }
    }
}

data class Coordinate(
    val x: Int,
    val y: Int
)

enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT
}