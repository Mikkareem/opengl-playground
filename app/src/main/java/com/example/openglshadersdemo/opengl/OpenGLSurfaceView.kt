package com.example.openglshadersdemo.opengl

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: OpenGLSurfaceViewRenderer

    private var previousX = 0f
    private var previousY = 0f

    init {
        setEGLContextClientVersion(3)
        renderer = OpenGLSurfaceViewRenderer()
        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setZComponent(z: Float) {
        renderer.z = z
        requestRender()
    }

    private fun move(x: Float, y: Float) {
        val dx: Float = x
        val dy: Float = y

        val touchScaleFactor = height / width

        val angle = ((dx + dy) * touchScaleFactor).let {
            if (x - previousX > 0) it else it * -1f
        }

        renderer.angle += angle
        requestRender()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x / width
        val y = event.y / height

        when (event.action) {
            MotionEvent.ACTION_MOVE -> move(x, y)
        }

        previousX = x
        previousY = y

        return true
    }
}