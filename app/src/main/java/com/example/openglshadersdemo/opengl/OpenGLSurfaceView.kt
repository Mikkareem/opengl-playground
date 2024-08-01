package com.example.openglshadersdemo.opengl

import android.content.Context
import android.opengl.GLSurfaceView

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: OpenGLSurfaceViewRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = OpenGLSurfaceViewRenderer()
        setRenderer(renderer)
    }
}