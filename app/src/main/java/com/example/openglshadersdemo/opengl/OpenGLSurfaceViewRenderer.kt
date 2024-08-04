package com.example.openglshadersdemo.opengl

import android.opengl.GLES30
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import com.example.openglshadersdemo.opengl.shapes.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLSurfaceViewRenderer : Renderer {
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val xRotationMatrix = FloatArray(16)
    private val yRotationMatrix = FloatArray(16)
    private val zRotationMatrix = FloatArray(16)

    @Volatile
    var xAngle: Float = 0f

    @Volatile
    var yAngle: Float = 0f

    @Volatile
    var zAngle: Float = 0f

    private lateinit var cube: Cube

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 20f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT)

        val scratch = FloatArray(16)

        val rotationMatrix = getRotationMatrix()
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -6f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        cube.draw(scratch)
    }

    private fun getRotationMatrix(): FloatArray {
        Matrix.setRotateM(xRotationMatrix, 0, xAngle, 1f, 0f, 0f)
        Matrix.setRotateM(yRotationMatrix, 0, yAngle, 0f, 1f, 0f)
        Matrix.setRotateM(zRotationMatrix, 0, zAngle, 0f, 0f, 1f)
        val xyRotation = FloatArray(16)
        Matrix.multiplyMM(xyRotation, 0, xRotationMatrix, 0, yRotationMatrix, 0)
        val xyzRotation = FloatArray(16)
        Matrix.multiplyMM(xyzRotation, 0, xyRotation, 0, zRotationMatrix, 0)
        return xyzRotation
    }
}