package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by kato-hy on 2015/12/11.
 */
class ImageShader {

    private String TAG = "ImageShader";

    private int mVertexShader;
    private int mFragmentShader;

    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int muAlphaHandle;
    private int muRatioHandle;
    private int maPositionHandle;
    private int maTextureCoordHandle;
    private int msTextureHandle;

    private float mWindowAspectRatio;//width/height

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private int mProgram;
    private FloatBuffer mTriangleVertices;


    protected ImageShader(){}

    private final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    " gl_Position = uMVPMatrix * aPosition;\n" +
                    " vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private static  String DEFAULT_FRAGMENT_SHADER_CODE =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform float uAlpha;\n"+
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    " vec4 color = texture2D(sTexture, vTextureCoord); \n" +
                    " gl_FragColor = vec4(color.r, color.g, color.b, color.a * uAlpha ); \n" +
                    "}\n";

    //頂点データとテクスチャ座標 (UV マッピング) の構造体配列形式
    private final float[] mTriangleVerticesData = new float[] {
            // X, Y, Z, U, V
            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,	// 左上
            1.0f,  1.0f, 0.0f, 1.0f, 1.0f,	// 右上
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,	// 左下
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f	// 右下
    };

    public void setWindowSize(float ratio)
    {
        mWindowAspectRatio = ratio;
    }


    public void setupShader(int textureTarget) {

        releaseShader();

        String fragmentShader = createFragmentShaderSourceOESIfNeed(textureTarget);

        mProgram = createProgram(VERTEX_SHADER_CODE, fragmentShader);
        if (mProgram == 0) {
            return;
        }

        //変数の設定
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureCoordHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        msTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        checkGlError("glGetAttribLocation sTexture");
        if (msTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for sTexture");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        muAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
        checkGlError("glGetAttribLocation uAlpha");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uAlpha");
        }

        //頂点配列の設定
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    /**
     * このシェーダーオブジェクトの構成を破棄します。
     */
    public void releaseShader() {
        GLES20.glDeleteProgram(mProgram);
        mProgram = 0;
        GLES20.glDeleteShader(mVertexShader);
        mVertexShader = 0;
        GLES20.glDeleteShader(mFragmentShader);
        mFragmentShader = 0;
    }

    /**
     * 描画します。
     *
     * @param texUnit テクスチャユニット番号
     * @param mvpMatrix MVP マトリックス(アフィン変換の行列)
     * @param stMatrix S/T マトリックス
     * @param alpha アルファ値
     */
    public void drawByShader(int texUnit,  float[] mvpMatrix, float[] stMatrix, float alpha) {



        // 背景とのブレンド方法を設定
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	// 単純なアルファブレンド

        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        //Position
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");

        //Texture
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureCoordHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maTextureCoordHandle");
        GLES20.glEnableVertexAttribArray(maTextureCoordHandle);
        checkGlError("glEnableVertexAttribArray maTextureCoordHandle");


        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, stMatrix, 0);
        //GLES20.glUniform1f(muRatioHandle, texAspectRatio / mWindowAspectRatio);
        GLES20.glUniform1f(muAlphaHandle, alpha);
        GLES20.glUniform1i(msTextureHandle, texUnit - GLES20.GL_TEXTURE0);

        //描画
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");

//		GLES20.glDisableVertexAttribArray(getHandle("aPosition"));
//		GLES20.glDisableVertexAttribArray(getHandle("aTextureCoord"));
//		GLES20.glBindTexture(GL_TEXTURE_2D, 0);
//		GLES20.glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            Log.e(TAG, "Could not create shader " + shaderType + ":" + source);
        }

        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
        }

        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        // バーテックスシェーダのコンパイル
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (mVertexShader == 0) {
            return 0;
        }
        // フラグメントシェーダのコンパイル
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (mFragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();

        if (program != 0) {
            // プログラムへバーテックスシェーダを関連付け
            GLES20.glAttachShader(program, mVertexShader);
            checkGlError("glAttachShader");
            // プログラムへフラグメントシェーダを関連付け
            GLES20.glAttachShader(program, mFragmentShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus,0);
            if (linkStatus[0] != GLES20.GL_TRUE) {

                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }


    /**
     * 指定された GLSL ソースコードをコンパイルしてプログラムオブジェクトを構成します。
     */
    private static String createFragmentShaderSourceOESIfNeed(final int texTarget) {
        if (texTarget == GLES11Ext.GL_TEXTURE_EXTERNAL_OES) {
            return new StringBuilder()
                    .append("#extension GL_OES_EGL_image_external : require\n")
                    .append(DEFAULT_FRAGMENT_SHADER_CODE.replace("sampler2D", "samplerExternalOES"))
                    .toString();
        }
        return DEFAULT_FRAGMENT_SHADER_CODE;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
