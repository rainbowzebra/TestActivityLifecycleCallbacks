package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlLayer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private String TAG = "GlLayer";

    public final static float ROTATE_ANGLE_0 = 0;                     // z軸回転量(0度)
    public final static float ROTATE_ANGLE_90 = (float)Math.PI/2;     // z軸回転量(90度)
    public final static float ROTATE_ANGLE_180 = (float)Math.PI;      // z軸回転量(180度)
    public final static float ROTATE_ANGLE_270 = (float)Math.PI*3/2;  // z軸回転量(270度)

    public final static int REVERSAL_DEFAULT = 0;     // 反転タイプ(デフォルト)
    public final static int REVERSAL_LEFTRIGHT = 1;   // 反転タイプ(左右反転)
    public final static int REVERSAL_UPDOWN = 2;      // 反転タイプ(上下反転)

    private final GLSurfaceView mGLSurfaceView;
    private ImageShader mImageShader;
    private GlLayerCallback mGlLayerCallback;

    private boolean mIsUseSurface;          //Surfaceを使用するか

    private int mTextureID;                 //使用テクスチャID
    private SurfaceTexture mSurfaceTexture; //使用SurfaceTexture
    private Surface mSurface;               //使用Surface
    private Bitmap mBitmap;                 //表示画像


    private float[] mSTMatrix = new float[16];      //UV行列
    private float[] mMVPMatrix = new float[16];     //最終変換行列
    private float[] mAffineMatrix = new float[16];  //移動、拡大変換保持マトリックス
    //  移動、拡大変換保持マトリックス(mAffineMatrix)
    //  [0]:x座標倍率       [1]:0               [2]:0   [3]:0
    //  [4]:0              [5]:y座標倍率        [6]:0   [7]:0
    //  [8]:0              [9]:0               [10]:1  [11]:0
    //  [12]:x座標方向移動量 [13]:y座標方向移動量 [14]:0  [15]:1

    private float mTextureAlpha;                    //透過値
    private int mReversalType;                      //反転モード
    private float mRotateAngle;                     //回転

    private float mImageAspectRatio;                //画像アスペクト
    private int mImageHeight;
    private int mImageWidth;
    private int mWindowHeight;                      //ウィンドウ高さ
    private int mWindowWidth;                       //ウィンドウ幅

    private float mMaxZoomLevel;                    //拡大率の最大

    public interface GlLayerCallback {

        void onSurfaceCreated(Surface surface);
    }

    public GlLayer(GLSurfaceView glSurfaceView) {
        this(glSurfaceView,false,null);
    }

    public GlLayer(GLSurfaceView glSurfaceView, boolean isUseSurface, GlLayerCallback glLayerCallback) {

        //パラメータ保持
        mIsUseSurface = isUseSurface;
        mGlLayerCallback = glLayerCallback;

        //GLSurfaceView初期化
        mGLSurfaceView = glSurfaceView;
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);

        //更新を自らする設定
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //透過設定
        //mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //シェーダーの生成
        mImageShader = new ImageShader();


        initValue();
    }

    /**
     * マトリックス、パラメータ初期化
     */
    private void initValue()
    {
        //UV行列
        if(!mIsUseSurface) {
            //Bitmapはなぜか上下反転するため
            Matrix.setIdentityM(mSTMatrix, 0);
            Matrix.scaleM(mSTMatrix, 0, 1, -1, 0);
        }

        //最終変換行列
        Matrix.setIdentityM(mMVPMatrix, 0);
        //拡大縮小行列
        Matrix.setIdentityM(mAffineMatrix, 0);

        mTextureAlpha = 1;
        mRotateAngle = ROTATE_ANGLE_0;
        mReversalType = REVERSAL_DEFAULT;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //テクスチャ生成
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];

        //Video側テクスチャのテクスチャパラメータの設定
        if(mIsUseSurface) {

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            mSurfaceTexture = new SurfaceTexture(mTextureID);
            mSurfaceTexture.setOnFrameAvailableListener(this);


            mSurface = new Surface(mSurfaceTexture);

            mImageShader.setupShader(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

            mGlLayerCallback.onSurfaceCreated(mSurface);
        }else {

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            mImageShader.setupShader(GLES20.GL_TEXTURE_2D);
        }


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        mWindowWidth = width;
        mWindowHeight = height;

        if(mImageHeight == 0 || mImageWidth == 0)
            setImageAspectRatio(mWindowWidth, mWindowHeight);

        updateMaxZoomLevel();
        //Log.d(TAG, "GlLayer Name:" + Thread.currentThread().getName());
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //long time = System.currentTimeMillis();

        //ビューポートの設定
        float imageRatio = mImageAspectRatio;
        if(mRotateAngle == ROTATE_ANGLE_90 || mRotateAngle == ROTATE_ANGLE_270){
            if (imageRatio > (float) mWindowHeight / mWindowWidth) {
                int vWidth = (int) (mWindowHeight / imageRatio);
                GLES20.glViewport((mWindowWidth - vWidth) / 2, 0, vWidth, mWindowHeight);
            } else {
                int vHeight = (int) (mWindowWidth * imageRatio);
                GLES20.glViewport(0, (mWindowHeight - vHeight) / 2, mWindowWidth, vHeight);
            }
        }else {
            if (imageRatio > (float) mWindowWidth / mWindowHeight) {
                int vHeight = (int) (mWindowWidth / imageRatio);
                GLES20.glViewport(0, (mWindowHeight - vHeight) / 2, mWindowWidth, vHeight);
            } else {
                int vWidth = (int) (mWindowHeight * imageRatio);
                GLES20.glViewport((mWindowWidth - vWidth) / 2, 0, vWidth, mWindowHeight);
            }
        }

        if(mIsUseSurface) {
            //以降、描画処理
            //背景クリア
           // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClearColor(231, 231, 239, 255);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

            synchronized (this) {
                //テクスチャ更新
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mSTMatrix);
            }

            mImageShader.drawByShader(GLES20.GL_TEXTURE0, mMVPMatrix, mSTMatrix, mTextureAlpha);

            //Log.d(TAG, Long.toString(System.currentTimeMillis() - time) + "ms");

        }else {
            //以降、描画処理
            //背景クリア
           //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClearColor(231, 231, 239, 255);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            synchronized (this) {
                if (mBitmap != null) {
                    //テクスチャ更新
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

                    mImageShader.drawByShader(GLES20.GL_TEXTURE0, mMVPMatrix, mSTMatrix, mTextureAlpha);
                }
            }
            //Log.d(TAG, Long.toString(System.currentTimeMillis() - time) + "ms");
        }

        GLES20.glFinish();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateImage();
    }

    /**
     * 描画画像のセット関数（Surfaceを利用しないときに使用）
     * @param bitmap 画像
     */
    public void setDrawImage(Bitmap bitmap) {
        if (mIsUseSurface == false) {
            synchronized (this) {
                mBitmap = bitmap;
            }
            updateImage();
        }
    }


    public Surface getSurface() {
        if(mIsUseSurface == false)
            return null;

        return mSurface;
    }

    /**
     * 反転タイプ設定関数
     * @param reversalType 設定する反転タイプ(デフォルト、左右反転、上下反転)
     */
    public void setReversalType(int reversalType) {

        if(reversalType != REVERSAL_DEFAULT && reversalType != REVERSAL_LEFTRIGHT && reversalType != REVERSAL_UPDOWN){
            return;
        }

        mReversalType = reversalType;
        updateImage();
    }

    /**
     * 現在の反転タイプ取得関数
     * @return 現在の反転タイプ(デフォルト、左右反転、上下反転)
     */
    public float getReversalType() {
        return mReversalType;
    }

    /**
     * 拡大倍率差分加算関数
     * @param diffZoomLevel 加算する拡大倍率
     */
    public void setZoomLevel(float diffZoomLevel) {

        float translateX = mAffineMatrix[12];
        float translateY = mAffineMatrix[13];

        //移動点の中心を拡大
        setZoomLevel(translateX, translateY, diffZoomLevel);
    }

    /**
     * 拡大倍率差分加算関数
     * @param centerX 拡大する中心点のx座標(正規化後の値)
     * @param centerY 拡大する中心点のy座標(正規化後の値)
     * @param diffZoomLevel 加算する拡大倍率
     */
    private void setZoomLevel(float centerX, float centerY, float diffZoomLevel){

        if( diffZoomLevel <= 0){
            return;
        }

        //倍率チェック 拡大率の最大値のチェック
        if(mMaxZoomLevel < mAffineMatrix[0] * diffZoomLevel){
            diffZoomLevel =  mMaxZoomLevel / mAffineMatrix[0];
        }

        Matrix.scaleM(mAffineMatrix, 0, diffZoomLevel, diffZoomLevel, 0);

        mAffineMatrix[12]=  diffZoomLevel * centerX;
        mAffineMatrix[13]=  diffZoomLevel * centerY;

        checkAffineMatrix(mAffineMatrix);

        updateImage();
    }

    /**
     * 現在の拡大倍率取得関数
     * @return zoomLevel 現在の拡大倍率
     */
    public float getZoomLevel() {
        return mAffineMatrix[0];
    }

    /**
     * 表示位置移動設定関数
     * @param diffMovementX 加算するx軸の移動距離
     * @param diffMovementY 加算するy軸の移動距離
     */
    public void setTranslate(float diffMovementX, float diffMovementY) {

        float imageRatio = mImageAspectRatio;

        float bufX = diffMovementX;
        float bufY = diffMovementY;

        switch (mReversalType)
        {
            case REVERSAL_DEFAULT:
                bufX *= 1; bufY *= 1;
                break;
            case REVERSAL_LEFTRIGHT:
                bufX *= -1; bufY *= 1;
                break;
            case REVERSAL_UPDOWN:
                bufX *= 1; bufY *= -1;
                break;
        }

        diffMovementX = (float) (bufX * Math.cos((double)mRotateAngle)) - (float) (bufY * Math.sin((double) mRotateAngle));
        diffMovementY = (float) (bufY * Math.cos((double)mRotateAngle)) + (float) (bufX * Math.sin((double) mRotateAngle));

        if(mRotateAngle == ROTATE_ANGLE_90 || mRotateAngle == ROTATE_ANGLE_270){
            if (imageRatio > (float) mWindowHeight / mWindowWidth) {
                int vWidth = (int) (mWindowHeight / imageRatio);
                Matrix.translateM(mAffineMatrix, 0, diffMovementX / vWidth * 2 / mAffineMatrix[0], -1 * diffMovementY / mWindowHeight * 2 / mAffineMatrix[5], 0);
            } else {
                int vHeight = (int) (mWindowWidth * imageRatio);
                Matrix.translateM(mAffineMatrix, 0, diffMovementX / mWindowWidth * 2 / mAffineMatrix[0], -1 * diffMovementY / vHeight * 2 / mAffineMatrix[5], 0);
            }
        }else {
            if (imageRatio > (float) mWindowWidth / mWindowHeight) {
                int vHeight = (int) (mWindowWidth / imageRatio);
                Matrix.translateM(mAffineMatrix, 0, diffMovementX / mWindowWidth * 2 / mAffineMatrix[0], -1 * diffMovementY / vHeight * 2 / mAffineMatrix[5], 0);
            } else {
                int vWidth = (int) (mWindowHeight * imageRatio);
                Matrix.translateM(mAffineMatrix, 0, diffMovementX / vWidth * 2 / mAffineMatrix[0], -1 * diffMovementY / mWindowHeight * 2 / mAffineMatrix[5], 0);
            }
        }

        checkAffineMatrix(mAffineMatrix);

        updateImage();
    }

    /**
     * 移動距離の取得関数
     * @return 現在の移動距離
     */
    public PointF getTranslate() {

        float imageRatio = mImageAspectRatio;

        if(mRotateAngle == ROTATE_ANGLE_90 || mRotateAngle == ROTATE_ANGLE_270){
            if (imageRatio > (float) mWindowHeight / mWindowWidth) {
                int vWidth = (int) (mWindowHeight / imageRatio);
                return new PointF(mMVPMatrix[12] * vWidth / getZoomLevel() / 2, -1 * mMVPMatrix[13] * mWindowHeight / getZoomLevel() / 2);
            } else {
                int vHeight = (int) (mWindowWidth * imageRatio);
                return new PointF( mMVPMatrix[12] * mWindowWidth / getZoomLevel() / 2, -1 * mMVPMatrix[13] * vHeight / getZoomLevel() / 2);
            }
        }else {
            if (imageRatio > (float) mWindowWidth / mWindowHeight) {
                int vHeight = (int) (mWindowWidth / imageRatio);
                return new PointF(mMVPMatrix[12] * mWindowWidth / getZoomLevel() / 2, -1 * mMVPMatrix[13] * vHeight / getZoomLevel() / 2);
            } else {
                int vWidth = (int) (mWindowHeight * imageRatio);
                return new PointF(mMVPMatrix[12] * vWidth / getZoomLevel() / 2, -1 * mMVPMatrix[13] * mWindowHeight / getZoomLevel() / 2);
            }
        }
    }

    /**
     * 回転量の設定関数
     * @param rotateAngle 回転タイプ(0度、90度、180度、270度の4種類)
     */
    public void setRotateAngle(float rotateAngle) {

        if(rotateAngle != ROTATE_ANGLE_0 && rotateAngle != ROTATE_ANGLE_90 && rotateAngle != ROTATE_ANGLE_180 && rotateAngle != ROTATE_ANGLE_270){
            return;
        }

        mRotateAngle = rotateAngle;
        updateImage();
    }

    /**
     * 回転量の取得関数
     * @return 現在の回転量
     */
    public float getRotateAngle() {
        return mRotateAngle;
    }

    /**
     * 透過率設定関数
     * @param alpha 透過率
     */
    public void setTextureAlpha(float alpha) {

        if(alpha < 0 || alpha > 1){
            return;
        }

        mTextureAlpha = alpha;
        updateImage();
    }

    /**
     * 現在の透過率取得関数
     * @return 現在の透過率
     */
    public float getTextureAlpha() {
        return mTextureAlpha;
    }


    /**
     * 画面サイズ変更関数
     * 　引数に0を設定した場合や何も設定しない場合、画面サイズ一杯に画像を表示
     * @param width 表示画像の横サイズ
     * @param height 表示画像の縦サイズ
     */
    public void changeImageSize(int width, int height)
    {
        mImageHeight = height;
        mImageWidth = width;

        setImageAspectRatio(width, height);
        updateMaxZoomLevel();
    }

    private void setImageAspectRatio(int width, int height)
    {
        if(width !=0 && height != 0) {
            mImageAspectRatio = (float) width / height;
        }else{
            if(mWindowWidth != 0 && mWindowHeight != 0)
                mImageAspectRatio = (float)mWindowWidth/mWindowHeight;
            else
                mImageAspectRatio = 0;
        }
    }

    /**
     * 最大倍率設定関数
     * 　画像の縦方向と横方向のピクセル数で、大きいほうを最大倍率として採用
     */
    private void updateMaxZoomLevel(){

        if(mImageWidth != 0 && mImageHeight != 0) {
            mMaxZoomLevel = Math.max(mImageWidth, mImageHeight);
        }else{
            mMaxZoomLevel = Math.max(mWindowWidth, mWindowHeight);
        }

    }

    /**
     * 境界条件のチェック
     * @param m チェックするマトリックス
     */
    private void checkAffineMatrix(float[] m) {

        //拡大率の最小値のチェック
         if (m[0] < 1 || m[5] < 1) {
            m[0] = m[5] = 1;
        }

        //移動領域チェック
        float xMax = m[0] - 1;
        float yMax = m[5] - 1;

        if (xMax < m[12]) {
            m[12] = xMax;
        } else if (-1 * xMax > m[12]) {
            m[12] = -xMax;
        }

        if (yMax < m[13]) {
            m[13] = yMax;
        } else if (-1 * yMax > m[13]) {
            m[13] = -yMax;
        }
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * 描画の更新要求
     */
    private void updateImage(){
        updateVPMatrix();
        mGLSurfaceView.requestRender();
    }

    /**
     * 最終変換マトリックス更新
     */
    private void updateVPMatrix(){

        float[] bufMatrix = mAffineMatrix.clone();

        /**
         * 反転処理
         *  左右反転時
         *  　(1)X座標倍率:　現倍率の-1倍　
         *  　(2)移動量:0°or180°時　現x座標の移動量に対して-1倍　90°or270°時　現y座標の移動量に対して-1倍　
         *  上下反転時
         *  　(1)y座標倍率　-1倍
         *  　(2)移動量:0°or180°時　現y座標の移動量に対して-1倍　90°or270°時　現x座標の移動量に対して-1倍　
         */
        switch (mReversalType)
        {
            case REVERSAL_DEFAULT:
                bufMatrix[0] *= 1; bufMatrix[5] *= 1;
                break;
            case REVERSAL_LEFTRIGHT:
                bufMatrix[0] *= -1; bufMatrix[5] *= 1;
                if(mRotateAngle == ROTATE_ANGLE_90 || mRotateAngle == ROTATE_ANGLE_270){
                    bufMatrix[12] *= 1; bufMatrix[13] *= -1;
                }else{
                    bufMatrix[12] *= -1; bufMatrix[13] *= 1;
                }

                break;
            case REVERSAL_UPDOWN:
                bufMatrix[0] *= 1; bufMatrix[5] *= -1;
                if(mRotateAngle == ROTATE_ANGLE_90 || mRotateAngle == ROTATE_ANGLE_270){
                    bufMatrix[12] *= -1; bufMatrix[13] *= 1;
                }else{
                    bufMatrix[12] *= 1; bufMatrix[13] *= -1;
                }
                break;
            default:
                bufMatrix[0] *= 1; bufMatrix[5] *= 1;
                break;
        }


        /**
         * 回転処理
         * (1)移動量の回転処理
         * (2)画像の回転処理(mMVPMatrixに書き込みを含む)
         */
        float bufX = bufMatrix[12];
        float bufY = bufMatrix[13];

        bufMatrix[12] = (float) (bufX * Math.cos((double)mRotateAngle)) - (float) (bufY * Math.sin((double) mRotateAngle));
        bufMatrix[13] = (float) (bufY * Math.cos((double)mRotateAngle)) + (float) (bufX * Math.sin((double) mRotateAngle));

        Matrix.rotateM(mMVPMatrix,0 ,bufMatrix ,0 , mRotateAngle * 180 / (float)Math.PI ,0 ,0 ,1 );
    }
}
