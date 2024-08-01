package com.example.openglshadersdemo.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: OpenGLSurfaceRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = OpenGLSurfaceRenderer()
        setRenderer(renderer)

//        renderMode = RENDERMODE_WHEN_DIRTY
    }

    class OpenGLSurfaceRenderer: Renderer {

        private lateinit var triangle: Triangle
        private lateinit var square: Square

        private val vPMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val rotationMatrix = FloatArray(16)

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0.4334f, 0f, 0f, 1f)
            triangle = Triangle()
            square = Square()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            val aspect = width.toFloat() / height.toFloat()
            Matrix.frustumM(projectionMatrix, 0, -aspect, aspect, -1f, 1f, 3f, 7f)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            val scratch = FloatArray(16)

            val time = SystemClock.uptimeMillis() % 4000L
            val angle = 0.090f * time.toInt()
            Matrix.setRotateM(rotationMatrix, 0, angle, 1f, 1f, 0f)

            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 6f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)
            square.draw()
            triangle.draw(scratch)
        }
    }
}

class Triangle {
    companion object {
        const val COORDS_PER_VERTEX = 3
        val triangleCoords = floatArrayOf(    // in counterclockwise order:
            0.0f, 0.62200844f, 0.0f,      // top
            -0.5f, -0.31100425f, 0.0f,    // bottom left
            0.5f, -0.31100425f, 0.0f      // bottom right
        )
    }

    private val color = floatArrayOf(
        0.543436f,
        0.290128f,
        0.3892f,
        1f,
    )

    private var program: Int

    private var positionHandle = 0
    private var colorHandle = 0
    private var vpMatrixHandle = 0

    private var vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private var vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(triangleCoords)
            position(0)
        }
    }

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String) : Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw(vPMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            vpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix").also { vpMatrixHandle ->
                GLES20.glUniformMatrix4fv(vpMatrixHandle, 1, false, vPMatrix, 0)
            }

            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}

class Square {
    companion object {
        private const val COORDS_PER_VERTEX = 3
        private val squareCoords = floatArrayOf(
            -0.5f,  0.5f, 0.0f,  // 0
            -0.5f, -0.5f, 0.0f,  // 1
             0.5f, -0.5f, 0.0f,  // 2
             0.5f,  0.5f, 0.0f,  // 3
        )
        private val indices = shortArrayOf(
            0, 1, 2,
            0, 2, 3
        )
    }

    private val color = floatArrayOf(
        0.543436f,
        1f,
        0.3892f,
        1f,
    )

    private var program: Int

    private var positionHandle = 0
    private var colorHandle = 0

    private var vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(squareCoords)
            position(0)
        }
    }

    private val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
        order(ByteOrder.nativeOrder())
        asShortBuffer().apply {
            put(indices)
            position(0)
        }
    }

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String) : Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw() {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            colorHandle = GLES20.glGetUniformLocation(program, "vColor").also { colorHandle ->
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the Squares using
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}