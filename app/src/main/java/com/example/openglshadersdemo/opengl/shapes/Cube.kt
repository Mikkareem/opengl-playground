package com.example.openglshadersdemo.opengl.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import androidx.compose.ui.graphics.Color
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Cube {
    private val coordinates = floatArrayOf(
        // Back Face
        // First line
        -0.5f, 0.5f, 0.5f, // Top-left
        0.5f, 0.5f, 0.5f, // Top-right
        // Second line
        0.5f, 0.5f, 0.5f, // Top-right
        0.5f, -0.5f, 0.5f, // Bottom-right
        // Third line
        0.5f, -0.5f, 0.5f, // Bottom-right
        -0.5f, -0.5f, 0.5f, // Bottom-left
        // Fourth line
        -0.5f, -0.5f, 0.5f, // Bottom-left
        -0.5f, 0.5f, 0.5f,  // Top-left


        // Front Face
        // First line
        -0.5f, 0.5f, -0.5f, // Top-left
        0.5f, 0.5f, -0.5f, // Top-right
        // Second line
        0.5f, 0.5f, -0.5f, // Top-right
        0.5f, -0.5f, -0.5f, // Bottom-right
        // Third line
        0.5f, -0.5f, -0.5f, // Bottom-right
        -0.5f, -0.5f, -0.5f, // Bottom-left
        // Fourth line
        -0.5f, -0.5f, -0.5f, // Bottom-left
        -0.5f, 0.5f, -0.5f,  // Top-left

        // Left
        // First Line
        -0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, 0.5f,
        // Second Line
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,

        // Right
        // First Line
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,
        // Second Line
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
    )

    private val vertexShaderCode = """
        #version 300 es
        layout(location = 0) in vec3 aPos;
        uniform mat4 mvp;
        
        void main() {
            gl_Position = mvp * vec4(aPos, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        uniform vec4 color;
        
        out vec4 o_Color;
        
        void main() {
            o_Color = color;
        }
    """.trimIndent()

    private val vertexBuffer = ByteBuffer.allocateDirect(coordinates.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(coordinates)
            position(0)
        }
    }

    private var program: Int

    private var color: FloatArray = Color.Magenta.let {
        floatArrayOf(it.red, it.green, it.blue, it.alpha)
    }

    init {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)

            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(it, GLES30.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == 0) {
                val errorMsg = GLES30.glGetProgramInfoLog(it)
                GLES30.glDeleteProgram(it)
                throw RuntimeException("Error linking program: $errorMsg")
            }
        }

        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)

            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)

            if (compileStatus[0] == 0) {
                val errorMsg = GLES30.glGetShaderInfoLog(shader)
                GLES30.glDeleteShader(shader)
                throw java.lang.RuntimeException("Error compiling shader: $errorMsg")
            }
        }
    }

    fun draw(mvp: FloatArray) {
        GLES30.glUseProgram(program)

        GLES30.glGetUniformLocation(program, "color").also {
            GLES30.glUniform4fv(it, 1, color, 0)
        }

        GLES30.glGetUniformLocation(program, "mvp").also {
            GLES30.glUniformMatrix4fv(it, 1, false, mvp, 0)
        }

        GLES30.glDrawArrays(GLES30.GL_LINES, 0, coordinates.size / 3)

        val factor = 0.00211f
        var scaleFactor = factor
        for (i in 0..30) {
            val scale = FloatArray(16)
            Matrix.setIdentityM(scale, 0)
            Matrix.scaleM(scale, 0, 1f + scaleFactor, 1f + scaleFactor, 1f)

            val result = FloatArray(16)
            Matrix.multiplyMM(result, 0, mvp, 0, scale, 0)

            GLES30.glGetUniformLocation(program, "mvp").also {
                GLES30.glUniformMatrix4fv(it, 1, false, result, 0)
            }

            GLES30.glDrawArrays(GLES30.GL_LINES, 0, coordinates.size / 3)
            scaleFactor += factor
        }
    }
}