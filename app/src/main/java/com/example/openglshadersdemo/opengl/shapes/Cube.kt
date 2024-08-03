package com.example.openglshadersdemo.opengl.shapes

import android.opengl.GLES30
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Cube {
    companion object {
        private const val COORDS_PER_VERTEX = 3
    }

    private var positionHandle = 0
    private val vbo = IntArray(2)
    private val vao = IntArray(1)

    private val coordinates = floatArrayOf(
        // Front
        -1f, 1f, -1f,   // 0
        -1f, -1f, -1f,   // 1
        1f, -1f, -1f,   // 2
        1f, 1f, -1f,   // 3

        // Back
        -1f, 1f, 1f,   // 4
        -1f, -1f, 1f,   // 5
        1f, -1f, 1f,   // 6
        1f, 1f, 1f,   // 7

        // Top
        -1f, -1f, 1f,   // 8
        -1f, -1f, -1f,   // 9
        1f, -1f, -1f,   // 10
        1f, -1f, 1f,   // 11

        // Bottom
        -1f, 1f, 1f,   // 12
        -1f, 1f, -1f,   // 13
        1f, 1f, -1f,   // 14
        1f, 1f, 1f,   // 15

        // Left
        -1f, -1f, 1f,   // 16
        -1f, -1f, -1f,   // 17
        -1f, 1f, -1f,   // 18
        -1f, 1f, 1f,   // 19

        // Right
        1f, -1f, 1f,   // 20
        1f, -1f, -1f,   // 21
        1f, 1f, -1f,   // 22
        1f, 1f, 1f,   // 23
    )
    private val indices = shortArrayOf(
        0, 1, 2,
        0, 2, 3,

        4, 5, 6,
        4, 6, 7,

        8, 9, 10,
        8, 10, 11,

        12, 13, 14,
        12, 14, 15,

        16, 17, 18,
        16, 18, 19,

        20, 21, 22,
        20, 22, 23
    )

    private val vertexBuffer = ByteBuffer.allocateDirect(coordinates.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(coordinates)
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
        attribute vec3 vPosition;
        uniform mat4 mvp;
        void main() {
            gl_Position = mvp * vec4(vPosition, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    private var program: Int

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

        positionHandle = GLES30.glGetAttribLocation(program, "vPosition")

        GLES30.glGenBuffers(2, vbo, 0)
        GLES30.glGenVertexArrays(1, vao, 0)

        GLES30.glBindVertexArray(vao[0])

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            coordinates.size * 4,
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[1])
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * 2,
            indexBuffer,
            GLES30.GL_STATIC_DRAW
        )

        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            COORDS_PER_VERTEX * 4,
            0
        )

        // Unbind VAO
        GLES30.glBindVertexArray(0)
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

        GLES30.glBindVertexArray(vao[0])

        GLES30.glGetUniformLocation(program, "vColor").also {
            GLES30.glUniform4fv(it, 1, floatArrayOf(0f, 1f, 1f, 1f), 0)
        }

        GLES30.glGetUniformLocation(program, "mvp").also {
            val rotationMatrix = FloatArray(16)
            Matrix.setIdentityM(rotationMatrix, 0)
//            Matrix.setRotateM(rotationMatrix, 0, 45f, 1f,0f,0f)
//            Matrix.translateM(rotationMatrix, 0, 0f, 1f, 0f)
//            Matrix.scaleM(rotationMatrix, 0, .5f, .5f, 1f)

            val result = FloatArray(16)
            Matrix.multiplyMM(result, 0, mvp, 0, rotationMatrix, 0)

            GLES30.glUniformMatrix4fv(it, 1, false, result, 0)
        }

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.size, GLES30.GL_UNSIGNED_SHORT, 0)
    }
}