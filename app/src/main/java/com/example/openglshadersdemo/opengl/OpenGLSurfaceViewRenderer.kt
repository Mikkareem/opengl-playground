package com.example.openglshadersdemo.opengl

import android.opengl.GLES30
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import com.example.openglshadersdemo.opengl.shapes.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLSurfaceViewRenderer : Renderer {

    private lateinit var cube: Cube

    @Volatile
    var z: Float = 0f

    @Volatile
    var angle: Float = 45f

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.4334f, 0f, 0f, 1f)
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 200f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -8f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Create a rotation transformation
        Matrix.setRotateM(rotationMatrix, 0, angle, 1f, 0f, 1f)

        // Calculate the Model-View-Projection matrix
        val scratch = FloatArray(16)
        Matrix.multiplyMM(scratch, 0, viewMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, scratch, 0)

        cube.draw(vPMatrix)
    }
}