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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        AndroidView(
                            factory = {
                                OpenGLSurfaceView(context = it)
                            },
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(.5f)
                        )

                        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Green))
                    }
                }
            }
        }
    }
}