package com.example.muscleiq.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.ui.viewmodel.MuscleStatus

@Composable
fun MuscleHeatmap(
    viewMode: String, // "front" or "back"
    getMuscleColor: (String) -> Color,
    onMuscleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(viewMode) {
                detectTapGestures { offset ->
                    val scaleX = size.width / 200f
                    val scaleY = size.height / 400f
                    val touchX = offset.x / scaleX
                    val touchY = offset.y / scaleY
                    val point = Offset(touchX, touchY)

                    val hitMuscle = if (viewMode == "front") {
                        hitTestFront(point)
                    } else {
                        hitTestBack(point)
                    }
                    if (hitMuscle != null) {
                        onMuscleClick(hitMuscle)
                    }
                }
            }
    ) {
        val scaleX = size.width / 200f
        val scaleY = size.height / 400f

        // Extension to scale paths
        fun Path.scalePath(): Path {
            val scaledPath = Path()
            scaledPath.addPath(this)
            val matrix = androidx.compose.ui.graphics.Matrix()
            matrix.scale(scaleX, scaleY)
            scaledPath.transform(matrix)
            return scaledPath
        }
        
        fun drawScaledRect(x: Float, y: Float, width: Float, height: Float, color: Color, rx: Float = 0f) {
            drawRoundRect(
                color = color,
                topLeft = Offset(x * scaleX, y * scaleY),
                size = Size(width * scaleX, height * scaleY),
                cornerRadius = CornerRadius(rx * scaleX, rx * scaleY)
            )
        }
        
        fun drawScaledCircle(cx: Float, cy: Float, r: Float, color: Color) {
            drawCircle(
                color = color,
                radius = r * scaleX, // Approximation
                center = Offset(cx * scaleX, cy * scaleY)
            )
        }

        if (viewMode == "front") {
            // Head & Neck
            drawScaledCircle(100f, 30f, 18f, Dark300)
            drawScaledRect(92f, 45f, 16f, 15f, Dark300)

            // Front Delts
            val leftDelt = Path().apply { moveTo(65f, 60f); quadraticBezierTo(75f, 55f, 92f, 60f); lineTo(92f, 80f); lineTo(60f, 80f); close() }
            drawPath(leftDelt.scalePath(), getMuscleColor("Front Delts"))
            val rightDelt = Path().apply { moveTo(135f, 60f); quadraticBezierTo(125f, 55f, 108f, 60f); lineTo(108f, 80f); lineTo(140f, 80f); close() }
            drawPath(rightDelt.scalePath(), getMuscleColor("Front Delts"))

            // Chest
            val chest = Path().apply { moveTo(70f, 80f); lineTo(130f, 80f); lineTo(130f, 115f); quadraticBezierTo(100f, 125f, 70f, 115f); close() }
            drawPath(chest.scalePath(), getMuscleColor("Chest"))

            // Abs
            drawScaledRect(80f, 120f, 40f, 50f, getMuscleColor("Abs"), 4f)

            // Obliques
            val leftOblique = Path().apply { moveTo(70f, 120f); lineTo(78f, 120f); lineTo(78f, 170f); lineTo(65f, 160f); close() }
            drawPath(leftOblique.scalePath(), getMuscleColor("Obliques"))
            val rightOblique = Path().apply { moveTo(130f, 120f); lineTo(122f, 120f); lineTo(122f, 170f); lineTo(135f, 160f); close() }
            drawPath(rightOblique.scalePath(), getMuscleColor("Obliques"))

            // Biceps
            drawScaledRect(50f, 85f, 18f, 40f, getMuscleColor("Biceps"), 8f)
            drawScaledRect(132f, 85f, 18f, 40f, getMuscleColor("Biceps"), 8f)

            // Forearms
            drawScaledRect(45f, 130f, 14f, 45f, getMuscleColor("Forearms"), 6f)
            drawScaledRect(141f, 130f, 14f, 45f, getMuscleColor("Forearms"), 6f)

            // Pelvis
            val pelvis = Path().apply { moveTo(65f, 175f); lineTo(135f, 175f); lineTo(120f, 200f); lineTo(80f, 200f); close() }
            drawPath(pelvis.scalePath(), Dark300)

            // Quads
            drawScaledRect(65f, 205f, 30f, 75f, getMuscleColor("Quads"), 10f)
            drawScaledRect(105f, 205f, 30f, 75f, getMuscleColor("Quads"), 10f)

            // Calves
            drawScaledRect(68f, 290f, 24f, 60f, getMuscleColor("Calves"), 8f)
            drawScaledRect(108f, 290f, 24f, 60f, getMuscleColor("Calves"), 8f)
        } else {
            // Head & Neck
            drawScaledCircle(100f, 30f, 18f, Dark300)
            drawScaledRect(92f, 45f, 16f, 15f, Dark300)

            // Upper Back
            val upperBack = Path().apply { moveTo(75f, 55f); lineTo(125f, 55f); lineTo(110f, 90f); lineTo(90f, 90f); close() }
            drawPath(upperBack.scalePath(), getMuscleColor("Upper Back"))

            // Rear Delts
            val leftRearDelt = Path().apply { moveTo(55f, 60f); quadraticBezierTo(65f, 55f, 75f, 60f); lineTo(75f, 80f); lineTo(50f, 80f); close() }
            drawPath(leftRearDelt.scalePath(), getMuscleColor("Rear Delts"))
            val rightRearDelt = Path().apply { moveTo(145f, 60f); quadraticBezierTo(135f, 55f, 125f, 60f); lineTo(125f, 80f); lineTo(150f, 80f); close() }
            drawPath(rightRearDelt.scalePath(), getMuscleColor("Rear Delts"))

            // Lats
            val leftLat = Path().apply { moveTo(65f, 85f); lineTo(95f, 95f); lineTo(95f, 150f); lineTo(70f, 120f); close() }
            drawPath(leftLat.scalePath(), getMuscleColor("Lats"))
            val rightLat = Path().apply { moveTo(135f, 85f); lineTo(105f, 95f); lineTo(105f, 150f); lineTo(130f, 120f); close() }
            drawPath(rightLat.scalePath(), getMuscleColor("Lats"))

            // Lower Back
            drawScaledRect(85f, 155f, 30f, 20f, Dark300)

            // Triceps
            drawScaledRect(48f, 85f, 16f, 42f, getMuscleColor("Triceps"), 8f)
            drawScaledRect(136f, 85f, 16f, 42f, getMuscleColor("Triceps"), 8f)

            // Forearms
            drawScaledRect(42f, 132f, 14f, 45f, getMuscleColor("Forearms"), 6f)
            drawScaledRect(144f, 132f, 14f, 45f, getMuscleColor("Forearms"), 6f)

            // Glutes
            val leftGlute = Path().apply { moveTo(60f, 180f); lineTo(100f, 180f); lineTo(100f, 220f); quadraticBezierTo(80f, 230f, 60f, 210f); close() }
            drawPath(leftGlute.scalePath(), getMuscleColor("Glutes"))
            val rightGlute = Path().apply { moveTo(140f, 180f); lineTo(100f, 180f); lineTo(100f, 220f); quadraticBezierTo(120f, 230f, 140f, 210f); close() }
            drawPath(rightGlute.scalePath(), getMuscleColor("Glutes"))

            // Hamstrings
            drawScaledRect(65f, 225f, 28f, 65f, getMuscleColor("Hamstrings"), 10f)
            drawScaledRect(107f, 225f, 28f, 65f, getMuscleColor("Hamstrings"), 10f)

            // Calves
            drawScaledRect(68f, 295f, 24f, 60f, getMuscleColor("Calves"), 8f)
            drawScaledRect(108f, 295f, 24f, 60f, getMuscleColor("Calves"), 8f)
        }
    }
}

// Very basic hit testing using bounding boxes of the unscaled SVG coordinates
fun hitTestFront(p: Offset): String? {
    if (Rect(60f, 55f, 92f, 80f).contains(p) || Rect(108f, 55f, 140f, 80f).contains(p)) return "Front Delts"
    if (Rect(70f, 80f, 130f, 125f).contains(p)) return "Chest"
    if (Rect(80f, 120f, 120f, 170f).contains(p)) return "Abs"
    if (Rect(65f, 120f, 78f, 170f).contains(p) || Rect(122f, 120f, 135f, 170f).contains(p)) return "Obliques"
    if (Rect(50f, 85f, 68f, 125f).contains(p) || Rect(132f, 85f, 150f, 125f).contains(p)) return "Biceps"
    if (Rect(45f, 130f, 59f, 175f).contains(p) || Rect(141f, 130f, 155f, 175f).contains(p)) return "Forearms"
    if (Rect(65f, 205f, 95f, 280f).contains(p) || Rect(105f, 205f, 135f, 280f).contains(p)) return "Quads"
    if (Rect(68f, 290f, 92f, 350f).contains(p) || Rect(108f, 290f, 132f, 350f).contains(p)) return "Calves"
    return null
}

fun hitTestBack(p: Offset): String? {
    if (Rect(75f, 55f, 125f, 90f).contains(p)) return "Upper Back"
    if (Rect(50f, 55f, 75f, 80f).contains(p) || Rect(125f, 55f, 150f, 80f).contains(p)) return "Rear Delts"
    if (Rect(65f, 85f, 95f, 150f).contains(p) || Rect(105f, 85f, 135f, 150f).contains(p)) return "Lats"
    if (Rect(48f, 85f, 64f, 127f).contains(p) || Rect(136f, 85f, 152f, 127f).contains(p)) return "Triceps"
    if (Rect(42f, 132f, 56f, 177f).contains(p) || Rect(144f, 132f, 158f, 177f).contains(p)) return "Forearms"
    if (Rect(60f, 180f, 100f, 230f).contains(p) || Rect(100f, 180f, 140f, 230f).contains(p)) return "Glutes"
    if (Rect(65f, 225f, 93f, 290f).contains(p) || Rect(107f, 225f, 135f, 290f).contains(p)) return "Hamstrings"
    if (Rect(68f, 295f, 92f, 355f).contains(p) || Rect(108f, 295f, 132f, 355f).contains(p)) return "Calves"
    return null
}
