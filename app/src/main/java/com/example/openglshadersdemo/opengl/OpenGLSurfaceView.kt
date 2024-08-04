package com.example.openglshadersdemo.opengl

import android.content.Context
import android.opengl.GLSurfaceView

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: OpenGLSurfaceViewRenderer

    init {
        setEGLContextClientVersion(3)
        renderer = OpenGLSurfaceViewRenderer()
        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setXRotation(angle: Float) {
        renderer.xAngle = angle
        requestRender()
    }

    fun setYRotation(angle: Float) {
        renderer.yAngle = angle
        requestRender()
    }

    fun setZRotation(angle: Float) {
        renderer.zAngle = angle
        requestRender()
    }
}