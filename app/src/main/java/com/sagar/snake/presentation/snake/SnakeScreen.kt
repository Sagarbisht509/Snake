package com.sagar.snake.presentation.snake

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sagar.snake.R
import com.sagar.snake.ui.theme.honkFontFamily

@Composable
fun SnakeScreen(
    viewModel: SnakeScreenViewModel
) {

    val activity = (LocalContext.current as? Activity)

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    LaunchedEffect(key1 = uiState.isGameOver) {
        if (uiState.isGameOver) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
    }

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
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = if (uiState.isGameOver) .5f else 1f)
                .navigationBarsPadding()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.score),
                    fontFamily = honkFontFamily,
                    fontSize = 22.sp
                )
                AnimatedCounter(score = uiState.score)
                Text(
                    text = " / ",
                    fontFamily = honkFontFamily
                )
                AnimatedCounter(score = if (uiState.score < uiState.bestScore) uiState.bestScore else uiState.score)
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = Color(0xFF5B9179))
                    .aspectRatio(ratio = 4 / 4f)
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

            Buttons(
                onDirectionChange = { direction ->
                    viewModel.onEvent(
                        event = SnakeScreenEvent.OnDirectionChanged(direction = direction)
                    )
                },
                enabled = !uiState.isGameOver,
                gameStateText = uiState.gameStateText,
                onClick = { viewModel.changeGameState() }
            )
        }

        if (uiState.isGameOver) {
            GameOverAnimation(
                isGameOver = uiState.isGameOver,
                onExitClicked = { activity?.finish() },
                onResetClicked = { viewModel.onEvent(event = SnakeScreenEvent.OnRestart) }
            )
        }
    }
}

@Composable
fun GameOverAnimation(
    isGameOver: Boolean,
    onExitClicked: () -> Unit,
    onResetClicked: () -> Unit
) {
    val transition = updateTransition(targetState = isGameOver, label = "gameOverTransition")
    val fontSize by transition.animateDp(
        label = "fontSizeTransition"
    ) { gameOver ->
        if (gameOver) 50.dp else 0.dp
    }

    val scale by transition.animateFloat(
        label = "scaleTransition"
    ) { gameOver ->
        if (gameOver) 1.2f else 0f
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.game_over),
            style = TextStyle(
                fontFamily = honkFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = with(LocalDensity.current) { fontSize.toSp() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.exit),
                contentDescription = stringResource(R.string.exit),
                modifier = Modifier
                    .size(50.dp)
                    .clickable(onClick = onExitClicked)
            )

            Image(
                painter = painterResource(id = R.drawable.reset),
                contentDescription = stringResource(R.string.reset),
                modifier = Modifier
                    .size(60.dp)
                    .clickable(onClick = onResetClicked)
            )
        }
    }
}

@Composable
fun Buttons(
    onDirectionChange: (Direction) -> Unit,
    gameStateText: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
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
            enabled = enabled,
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "up", tint = Color.White)
        }
        Row {
            Button(
                onClick = { onDirectionChange(Direction.LEFT) },
                enabled = enabled,
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "left",
                    tint = Color.White
                )
            }
            Text(
                text = gameStateText,
                fontFamily = honkFontFamily,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(15.dp)
                    .clickable(onClick = onClick, enabled = enabled)
            )
            Button(
                onClick = { onDirectionChange(Direction.RIGHT) },
                enabled = enabled,
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "right",
                    tint = Color.White
                )
            }
        }
        Button(
            onClick = { onDirectionChange(Direction.DOWN) },
            enabled = enabled,
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "down", tint = Color.White)
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
                alpha = .0f,
                center = Offset(x = (i + 0.5f) * (radius * 2), y = (j + 0.5f) * (radius * 2))
            )
        }
    }
}

@Composable
fun AnimatedCounter(
    score: Int,
    modifier: Modifier = Modifier
) {
    var oldScore by remember { mutableIntStateOf(score) }
    SideEffect {
        oldScore = score
    }

    Row(modifier = modifier) {
        val oldScoreString = oldScore.toString()
        val scoreString = score.toString()

        for (i in scoreString.indices) {
            val newChar = scoreString[i]
            val oldChar = oldScoreString.getOrNull(i)

            val char = if (newChar == oldChar) oldScoreString[i] else scoreString[i]

            AnimatedContent(
                targetState = char,
                label = "counter",
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }
            ) {
                Text(
                    text = it.toString(),
                    softWrap = false,
                    fontFamily = honkFontFamily,
                    fontSize = 28.sp
                )
            }
        }
    }
}

//////* *************
//@Preview(showSystemUi = true)
@Composable
fun PreviewLightMode() {
    SnakeScreen(viewModel = hiltViewModel())
}

//@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDarkMode() {
    SnakeScreen(viewModel = hiltViewModel())
}