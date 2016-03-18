package com.windward.www.casio_golf_viewer.casio.golf.player;


import android.support.annotation.Nullable;

import java.nio.ByteBuffer;

/**
 * デコードされたフレームの情報を格納するデータクラス
 */
public class VideoFrame {

    /**
     * フレームのPresentation Time[usec]
     */
    private long mPresentationTimeUs;

    /**
     * フレームの画像データ (TODO データ形式は仮のもの)
     */
    private ByteBuffer mImage;

    /**
     * コンストラクタ
     *
     * @param presentationTimeUs フレームのPresentationTime[usec]
     * @param image              デコードされたフレーム画像
     */
    protected VideoFrame(long presentationTimeUs, ByteBuffer image) {
        mPresentationTimeUs = presentationTimeUs;
        mImage = image;
    }

    /**
     * Presentation Timeを取得する
     *
     * @return Presentation Time[usec]
     */
    public long getPresentationTimeUs() {
        return mPresentationTimeUs;
    }

    /**
     * フレーム画像を取得する
     *
     * @return フレーム画像
     */
    public @Nullable ByteBuffer getImage() {
        return mImage;
    }
}
