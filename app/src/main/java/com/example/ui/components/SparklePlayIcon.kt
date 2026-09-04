package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Generate Video Icon: A play button nested inside a sparkling AI star shape.
 */
@Composable
fun SparklePlayIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    sparkleColor: Color = Color(0xFFFACC15),
    playColor: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f

        // Draw 4-pointed sparkle star
        val starPath = Path().apply {
            moveTo(cx, 0f)
            // top to right curve
            quadraticTo(cx, cy, w, cy)
            // right to bottom curve
            quadraticTo(cx, cy, cx, h)
            // bottom to left curve
            quadraticTo(cx, cy, 0f, cy)
            // left to top curve
            quadraticTo(cx, cy, cx, 0f)
            close()
        }

        drawPath(
            path = starPath,
            brush = Brush.linearGradient(
                colors = listOf(sparkleColor, Color(0xFFEC4899), Color(0xFF8B5CF6))
            )
        )

        // Draw Play Triangle centered
        val playPath = Path().apply {
            val triW = w * 0.28f
            val triH = h * 0.32f
            val startX = cx - triW / 2.5f
            val startY = cy - triH / 2f

            moveTo(startX, startY)
            lineTo(startX + triW, cy)
            lineTo(startX, startY + triH)
            close()
        }

        drawPath(
            path = playPath,
            color = playColor
        )
    }
}
