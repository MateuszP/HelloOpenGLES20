/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 2.0.
 */
public class Triangle {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords1[] = {
            // in counterclockwise order:
            0.0f,  0.5f, 0.0f,   // top
            -0.5f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, 0.5f    // bottom right
    };
    static float triangleCoords2[] = {
            // in counterclockwise order:
            0.0f,  0.5f, 0.0f,   // top
            0.0f, 0.0f, 0.5f,   // bottom left
            0.5f, 0.0f, 0.0f    // bottom right
    };
    static float triangleCoords3[] = {
            // in counterclockwise order:
            0.0f,  0.5f, 0.0f,   // top
            0.5f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, -0.5f    // bottom right
    };
    static float triangleCoords4[] = {
            // in counterclockwise order:
            0.0f,  0.5f, 0.0f,   // top
            -0.5f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, -0.5f    // bottom right
    };
    static float triangleCoords5[] = {
            // in counterclockwise order:
            0.0f,  0.0f, -0.5f,   // top
            -0.5f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, 0.5f    // bottom right
    };
    static float triangleCoords6[] = {
            // in counterclockwise order:
            0.0f,  0.0f, 0.5f,   // top
            0.5f, 0.0f, 0.0f,   // bottom left
            0.0f, 0.0f, -0.5f    // bottom right
    };
    private final int vertexCount = triangleCoords1.length / COORDS_PER_VERTEX;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color1[] = { 0.502f, 0.000f, 0.000f };
    float color2[] = { 0.184f, 0.310f, 0.310f };
    float color3[] = { 0.000f, 0.000f, 0.804f };
    float color4[] = { 0.196f, 0.804f, 0.196f };
    float color5[] = { 0.804f, 0.361f, 0.361f };
    float color6[] = { 0.416f, 0.353f, 0.804f };

    int configuration;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Triangle(int coords) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords1.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        this.configuration=coords;
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        if(coords==1){
            vertexBuffer.put(triangleCoords1);
        } else if(coords==2) {
            vertexBuffer.put(triangleCoords2);
        }else if(coords==3) {
            vertexBuffer.put(triangleCoords3);
        }else if(coords==4) {
            vertexBuffer.put(triangleCoords4);
        }else if(coords==5) {
            vertexBuffer.put(triangleCoords5);
        }else if(coords==6) {
            vertexBuffer.put(triangleCoords6);
        }
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        if(this.configuration==1){
            GLES20.glUniform4fv(mColorHandle, 1, color1, 0);
        }
        if(this.configuration==2){
            GLES20.glUniform4fv(mColorHandle, 1, color2, 0);
        }
        if(this.configuration==3){
            GLES20.glUniform4fv(mColorHandle, 1, color3, 0);
        }
        if(this.configuration==4){
            GLES20.glUniform4fv(mColorHandle, 1, color4, 0);
        }
        if(this.configuration==5){
            GLES20.glUniform4fv(mColorHandle, 1, color5, 0);
        }
        if(this.configuration==6){
            GLES20.glUniform4fv(mColorHandle, 1, color6, 0);
        }
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public int getVertexCount() {
        return vertexCount;
    }
}