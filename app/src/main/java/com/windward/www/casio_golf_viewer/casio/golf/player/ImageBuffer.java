package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * デコードデータのバッファリングを行うクラス
 */
public class ImageBuffer {

    /**
     * バッファ可能なフレーム数の上限
     */
    private static final int MAX_BUFFER_NUM = 20;

    /**
     * 画像をバッファするメンバ変数(Key: Presentation Time[usec], Value: 画像データ)
     */
    private SparseArray mImageList;

    /**
     * バッファ範囲の最終フレームのPresentation Time[usec]
     */
    private long mBufferRangeMax;

    /**
     * バッファ範囲の開始フレームのPresentation Time[usec]
     */
    private long mBufferRangeMin;

    /**
     * バッファリング時に先読みで使用する速度倍率<br/>
     * 変更があった場合はバッファをクリアする
     */
    private double mPrePlayRate;

    /**
     * 動画ストリーム情報が格納されている{@link VideoInfo}インスタンス
     */
    private VideoInfo mVideoInfo;

    /**
     * フレーム情報が格納されている{@link VideoFrame}インスタンス
     */
    private VideoFrame mVideoFrame;

    /**
     * コンストラクタ
     *
     * @param videoInfo 動画ストリーム情報が格納されている{@link VideoInfo}インスタンス
     */
    protected ImageBuffer(VideoInfo videoInfo) {
        if (videoInfo != null) {
            mVideoInfo = videoInfo;
        }

    }

    /**
     * デコードデータを取得する
     *
     * @param presentationTimeUs 取得したいフレームのPresentation Time[usec]
     * @param playRate           バッファリングに使用する再生速度倍率
     * @return 指定フレームの{@link VideoFrame}クラスインスタンス (データがない場合はnull)
     */
    protected VideoFrame getImage(long presentationTimeUs, double playRate) {
        return null;
    }

    /**
     * デコードデータをバッファに格納する
     *
     * @param presentationTimeUs デコードしたフレームのPresentation Time[usec]
     * @param image              デコードデータ
     */
    protected void setImage(long presentationTimeUs, ByteBuffer image) {

    }

    /**
     * バッファリングが必要なフレームをチェックする
     *
     * @param currentPtsUs 現在のPresentation Time[usec]
     * @param playRate     再生速度倍率
     * @return バッファが必要なフレームを格納した配列(何もない場合はnull)
     */
    protected ArrayList checkNeedBuffer(long currentPtsUs, double playRate) {
        return null;
    }

    /**
     * バッファを更新する
     *
     * @param currentPtsUs 現在のPresentation Time[usec]
     * @param playRate     先読みで使用する再生速度倍率
     */
    protected void updateBuffer(long currentPtsUs, double playRate) {

    }

    /**
     * バッファをクリアする
     */
    protected void clear() {

    }

}
