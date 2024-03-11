package com.sagar.snake.presentation.snake

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sagar.snake.R

@Preview(showSystemUi = true)
@Composable
fun SnakeScreen(

) {

    val foodImage = ImageBitmap.imageResource(id = R.drawable.apple)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {

            Text(
                text = "Score: 0",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                .aspectRatio(ratio = 2 / 3f)
                /*.pointerInput(state.gameState) {
                    if (state.gameState != GameState.STARTED) {
                        return@pointerInput
                    }
                    detectTapGestures { offset ->
                        onEvent(SnakeGameEvent.UpdateDirection(offset, size.width))
                    }
                }*/
            ) {
                val cellSize = size.width / 20

                drawGameBoard(
                    radius = cellSize / 2,
                    circleColor = Color.Gray,
                    gridWidth = 20,
                    gridHeight = 30
                )

                drawFood(
                    foodImage = foodImage,
                    cellSize = cellSize.toInt(),
                    coordinate = Coordinate(15, 24)
                )
            }

            Buttons()
        }
    }
}

@Composable
fun Buttons() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { },
            border = BorderStroke(1.dp, color = Color.Green),
            modifier = Modifier.weight(.5f)
        ) {
            Text(
                text = "Pause",
                color = Color.Green,
                //fontFamily = Quicksand,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green
            ),
            modifier = Modifier.weight(.5f)
        ) {
            Text(
                text = "Start",
                //fontFamily = Quicksand,
                fontSize = 16.sp,
                color = Color.White
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
                center = Offset(x = (i + 0.5f) * (radius * 2), y = (j + 0.5f) * (radius * 2))
            )
        }
    }
}