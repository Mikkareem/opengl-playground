package com.example.openglshadersdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.openglshadersdemo.opengl.OpenGLSurfaceView
import com.example.openglshadersdemo.ui.theme.OpenGLShadersDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenGLShadersDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {

                        var sliderPosition by remember { mutableFloatStateOf(1.0001f) }

                        AndroidView(
                            factory = {
                                OpenGLSurfaceView(context = it).apply {
                                    setZComponent(sliderPosition)
                                }
                            },
                            update = {
                                it.setZComponent(sliderPosition)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.5f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Magenta)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Slider(
                                    value = sliderPosition,
                                    onValueChange = { sliderPosition = it },
                                    valueRange = 0f..50f
                                )
                                Text(sliderPosition.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}