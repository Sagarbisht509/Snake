package com.sagar.snake.presentation.snake

sealed class SnakeScreenEvent {
    object OnPause : SnakeScreenEvent()
    object OnStart : SnakeScreenEvent()
    object OnRestart : SnakeScreenEvent()

    data class OnDirectionChanged(val direction: Direction): SnakeScreenEvent()
}