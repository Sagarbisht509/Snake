package com.sagar.snake.presentation.snake

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagar.snake.R

@Composable
fun SnakeScreen(
    viewModel: SnakeScreenViewModel
) {

    val uiState by viewModel.uiState.collectAsState()

    val foodImage = ImageBitmap.imageResource(id = R.drawable.apple)
    val snakeHead = when (uiState.currentDirection) {
        Direction.RIGHT -> ImageBitmap.imageResource(id = R.drawable.snake_head_right)
        Direction.DOWN -> ImageBitmap.imageResource(id = R.drawable.snake_head_down)
        Direction.LEFT -> ImageBitmap.imageResource(id = R.drawable.snake_head_left)
        Direction.UP -> ImageBitmap.imageResource(id = R.drawable.snake_head_up)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "background image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                MultiStyleText(
                    text1 = "Score ",
                    color1 = Color(0xFF5B9179),
                    text2 = (uiState.snakeCoordinates.size - 1).toString(),
                    color2 = Color(0xFF5B9179)
                )

                MultiStyleText(
                    text1 = "Best ",
                    color1 = Color(0xFF5B9179),
                    text2 = "",
                    color2 = Color(0xFF5B9179)
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 4 / 3f)
            ) {
                val cellSize = size.width / 20

                drawGameBoard(
                    radius = cellSize / 2,
                    circleColor = Color.Gray,
                    gridWidth = 20,
                    gridHeight = 20
                )

                drawFood(
                    foodImage = foodImage,
                    cellSize = cellSize.toInt(),
                    coordinate = uiState.currentFoodCoordinate
                )

                drawSnake(
                    snakeHeadImage = snakeHead,
                    cellSize = cellSize,
                    snake = uiState.snakeCoordinates
                )
            }

            Buttons(onDirectionChange = { direction ->
                viewModel.onEvent(
                    event = SnakeScreenEvent.OnDirectionChanged(direction = direction)
                )
            })
        }

        GameOverAnimation()
    }
}

@Composable
fun GameOverAnimation() {
//
}

@Composable
fun Buttons(onDirectionChange: (Direction) -> Unit) {
    val buttonSize = Modifier.size(64.dp)
    val buttonColor = Color(0xFF5B9179)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Button(
            onClick = { onDirectionChange(Direction.UP) },
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up")
        }
        Row {
            Button(
                onClick = { onDirectionChange(Direction.LEFT) },
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "left")
            }
            Spacer(modifier = buttonSize)
            Button(
                onClick = { onDirectionChange(Direction.RIGHT) },
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "right")
            }
        }
        Button(
            onClick = { onDirectionChange(Direction.DOWN) },
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down")
        }
    }
}

private fun DrawScope.drawSnake(
    snakeHeadImage: ImageBitmap,
    cellSize: Float,
    snake: List<Coordinate>
) {
    val cellSizeInt = cellSize.toInt()
    snake.forEachIndexed { index, coordinate ->
        val radius = when (index) {
            snake.lastIndex -> cellSize / 4
            snake.size - 2 -> cellSize / 3
            else -> cellSize / 2.3f
        }
        if (index == 0) {
            drawImage(
                image = snakeHeadImage,
                dstOffset = IntOffset(
                    x = (coordinate.x * cellSizeInt) - (cellSizeInt / 4), // Adjusted x offset
                    y = (coordinate.y * cellSizeInt) - (cellSizeInt / 4)  // Adjusted y offset
                ),
                dstSize = IntSize(
                    cellSizeInt * 3 / 2,
                    cellSizeInt * 3 / 2
                )
            )
        } else {
            drawCircle(
                color = Color(0xFF5B9179),
                center = Offset(
                    x = (coordinate.x + 0.5f) * cellSize,
                    y = (coordinate.y + 0.5f) * cellSize
                ),
                radius = radius
            )
        }
    }
}

private fun DrawScope.drawFood(
    foodImage: ImageBitmap,
    cellSize: Int,
    coordinate: Coordinate
) {
    drawImage(
        image = foodImage,
        dstOffset = IntOffset(
            x = (coordinate.x * cellSize),
            y = (coordinate.y * cellSize)
        ),
        dstSize = IntSize(cellSize, cellSize)
    )
}

private fun DrawScope.drawGameBoard(
    radius: Float,
    circleColor: Color,
    gridWidth: Int,
    gridHeight: Int
) {
    for (i in 0 until gridWidth) {
        for (j in 0 until gridHeight) {
            drawCircle(
                color = circleColor,
                radius = radius,
                alpha = .1f,
                center = Offset(x = (i + 0.5f) * (radius * 2), y = (j + 0.5f) * (radius * 2))
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun preview() {
    SnakeScreen(viewModel = SnakeScreenViewModel())
}

@Composable
fun MultiStyleText(
    text1: String = "Score:",
    color1: Color = Color(0xFF5B9179),
    text2: String = "12",
    color2: Color = Color(0xFF5B9179),
) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = color1, fontSize = 20.sp)) {
                append(text1)
            }
            withStyle(
                style = SpanStyle(
                    color = color2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            ) {
                append(text2)
            }
        }
    )
}