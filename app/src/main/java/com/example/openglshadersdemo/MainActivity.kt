package com.example.openglshadersdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.openglshadersdemo.opengl.OpenGLSurfaceView
import com.example.openglshadersdemo.ui.theme.OpenGLShadersDemoTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenGLShadersDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    var xAngle by remember { mutableFloatStateOf(0f) }
                    var yAngle by remember { mutableFloatStateOf(0f) }
                    var zAngle by remember { mutableFloatStateOf(0f) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AndroidView(
                            factory = {
                                OpenGLSurfaceView(context = it).apply {
                                    setXRotation(xAngle)
                                    setYRotation(yAngle)
                                    setZRotation(zAngle)
                                }
                            },
                            update = {
                                it.setXRotation(xAngle)
                                it.setYRotation(yAngle)
                                it.setZRotation(zAngle)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.5f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Green)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Equalizer(
                                            title = "X",
                                            angle = xAngle,
                                            onAngleChange = { xAngle = it }
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Equalizer(
                                            title = "Y",
                                            angle = yAngle,
                                            onAngleChange = { yAngle = it }
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Equalizer(
                                            title = "Z",
                                            angle = zAngle,
                                            onAngleChange = { zAngle = it }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Equalizer(
    title: String,
    angle: Float,
    modifier: Modifier = Modifier,
    onAngleChange: (Float) -> Unit
) {
    Column(modifier = modifier) {
        Text(title)
        Box(modifier = Modifier.aspectRatio(1f)) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.Gray,
                    style = Stroke(width = 10.dp.toPx()),
                )
            }
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            onAngleChange(
                                calculateAngleBetween(
                                    p1 = change.position,
                                    origin = Offset(size.width / 2f, size.height / 2f)
                                )
                            )
                        }
                    }
            ) {
                val radiansFactor = (22f / 7f) / 180f
                val x = (size.width / 2f) * cos(angle * radiansFactor)
                val y = (size.width / 2f) * sin(angle * radiansFactor)
                drawCircle(
                    color = Color.Blue,
                    style = Stroke(width = 10.dp.toPx()),
                    radius = 8.dp.toPx(),
                    center = Offset(center.x + x, center.y + y)
                )
            }
        }
    }
}

private fun calculateAngleBetween(p1: Offset, origin: Offset): Float {
    val x = p1.x - origin.x
    val y = p1.y - origin.y
    val angleInRadian = atan2(y, x)
    val angleInDegree = angleInRadian * (180f / (22f / 7f))
    return angleInDegree
}