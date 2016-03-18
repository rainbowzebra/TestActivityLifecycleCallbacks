package com.windward.www.casio_golf_viewer.casio.golf.player;

/**
 * 動画ストリーム情報を扱うデータクラス
 */
public class VideoInfo {

    private double mCaptureRate;    // 撮影フレームレート

    private long mDurationUs;       // トータル時間[μsec]

    private int mHeight;            // 高さ

    private int mWidth;             // 幅

    /**
     * コンストラクタ
     */
    public VideoInfo() {
        mCaptureRate = -1;
        mDurationUs = -1;
        mHeight = -1;
        mWidth = -1;
    }

    /**
     * 撮影フレームレートを取得
     *
     * @return 撮影フレームレート(fps)
     */
    public double getCaptureRate() {
        return mCaptureRate;
    }

    /**
     * 撮影時間をマイクロ秒単位で取得
     *
     * @return 撮影時間[usec]
     */
    public long getDurationUs() {
        return mDurationUs;
    }

    /**
     * 動画の縦のピクセル数を取得
     *
     * @return 縦のピクセル数
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * 動画の横のピクセル数を取得
     *
     * @return 横のピクセル数
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * 動画の総フレーム数を取得
     *
     * @return 総フレーム数 (撮影レートと再生時間が設定されていない場合は-1を返す)
     */
    public int getNumberOfFrames() {
        if (mCaptureRate < 0 || mDurationUs < 0) {
            return -1;
        }
        return (int) Math.round(mDurationUs * mCaptureRate / (1000 * 1000));
    }

    /**
     * 撮影フレームレートを設定
     *
     * @param captureRate 動画の撮影フレームレート[fps] (0未満の場合は値は設定されない)
     */
    protected void setCaptureRate(double captureRate) {
        if (captureRate >= 0) {
            mCaptureRate = captureRate;
        }
    }

    /**
     * 動画の撮影時間を設定
     *
     * @param durationUs 撮影時間[usec] (0未満の場合は値は設定されない)
     */
    protected void setDurationUs(long durationUs) {
        if (durationUs >= 0) {
            mDurationUs = durationUs;
        }
    }

    /**
     * 動画の縦のピクセル数を設定
     *
     * @param height 縦のピクセル数 (0未満の場合は値は設定されない)
     */
    protected void setHeight(int height) {
        if (height >= 0) {
            mHeight = height;
        }
    }

    /**
     * 動画の横のピクセル数を設定
     *
     * @param width 横のピクセル数 (0未満の場合は値は設定されない)
     */
    protected void setWidth(int width) {
        if (width >= 0) {
            mWidth = width;
        }
    }
}
