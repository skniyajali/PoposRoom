package com.niyaj.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp

val gradientColors = listOf(
    Color.Red,
    Color.Magenta,
    Color.Blue,
    Color.Cyan,
    Color.Green,
    Color.Yellow,
    Color.Red
)

fun Modifier.drawRainbowBorder(
    strokeWidth: Dp,
    durationMillis: Int,
) = composed {

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val brush = Brush.sweepGradient(gradientColors)

    Modifier.drawWithContent {

        val strokeWidthPx = strokeWidth.toPx()
        val width = size.width
        val height = size.height

        drawContent()

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)

            // Destination
            drawRect(
                color = Color.Gray,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(width - strokeWidthPx, height - strokeWidthPx),
                style = Stroke(strokeWidthPx)
            )

            // Source
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }

            restoreToCount(checkPoint)
        }
    }
}

fun Modifier.drawAnimatedBorder(
    strokeWidth: Dp,
    shape: Shape,
    brush: (Size) -> Brush = {
        Brush.sweepGradient(gradientColors)
    },
    durationMillis: Int,
) = composed {

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Modifier
        .clip(shape)
        .drawWithCache {
            val strokeWidthPx = strokeWidth.toPx()

            val outline: Outline = shape.createOutline(size, layoutDirection, this)

            val pathBounds = outline.bounds

            onDrawWithContent {
                // This is actual content of the Composable that this modifier is assigned to
                drawContent()

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    // Destination

                    // We draw 2 times of the stroke with since we want actual size to be inside
                    // bounds while the outer stroke with is clipped with Modifier.clip

                    // 🔥 Using a maskPath with op(this, outline.path, PathOperation.Difference)
                    // And GenericShape can be used as Modifier.border does instead of clip
                    drawOutline(
                        outline = outline,
                        color = Color.Gray,
                        style = Stroke(strokeWidthPx * 2)
                    )

                    // Source
                    rotate(angle) {

                        drawCircle(
                            brush = brush(size),
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    restoreToCount(checkPoint)
                }
            }
        }
}