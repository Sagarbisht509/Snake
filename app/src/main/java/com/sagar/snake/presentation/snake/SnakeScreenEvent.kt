package com.sagar.snake.presentation.snake

sealed class SnakeScreenEvent {
    object OnPause : SnakeScreenEvent()
    object OnStart : SnakeScreenEvent()
    object OnRestart : SnakeScreenEvent()
    object OnExit : SnakeScreenEvent()
}