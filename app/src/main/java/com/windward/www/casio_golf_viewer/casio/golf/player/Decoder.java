package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * デコーダーを制御するクラス
 */
public class Decoder extends Thread {

    private static final String TAG = Decoder.class.getSimpleName();

    private VideoInfo mVideoInfo;

    private DecoderCallback mCallback;

    private MediaExtractor mExtractor;

    private MediaCodec mCodec;

    private Surface mSurface;

    private ByteBuffer[] mInputBuffers;

    private ByteBuffer[] mOutputBuffers;

    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private boolean mOutputDone;

    private long mSeekedPresentationTime;

    private boolean mWasFrameRendered;


    /**
     * コンストラクタ
     *
     * @param surface デコード結果を描画するSurfaceインスタンス
     */
    protected Decoder(Surface surface) {
        mSurface = surface;
        mOutputDone = false;
        mSeekedPresentationTime = -1;
        mWasFrameRendered = false;
    }

    /**
     * {@link DecoderCallback}を設定する
     *
     * @param callback コールバック先の{@link DecoderCallback}インスタンス
     */
    protected void setCallback(DecoderCallback callback) {
        mCallback = callback;
    }

    /**
     * 再生開始時に呼ばれる処理
     *
     * @param path      動画ファイルのパス
     * @param videoInfo 動画ストリーム情報を格納する{@link VideoInfo}インスタンス
     * @return ステータス(正常終了 0, ファイル読み込みエラー 1, フォーマットエラー 2)
     */
    protected int openFile(@NonNull String path, @NonNull VideoInfo videoInfo) {
        if (path == null) {
            Log.e(TAG, "path must not be null");
            return VideoController.FileStatus.OPEN_ERROR.getCode();
        }
        if (videoInfo == null) {
            Log.e(TAG, "videoinfo must not be null");
            return VideoController.FileStatus.OPEN_ERROR.getCode();
        }

        mVideoInfo = videoInfo;
        int status = setupDecoderAndExtractor(path, mSurface, videoInfo);
        if (status != VideoController.FileStatus.SUCCESS.getCode()) {
            Log.e(TAG, "Unable to open: " + path);
            return status;
        }

        return VideoController.FileStatus.SUCCESS.getCode();
    }

    /**
     * 再生終了時に呼ばれる処理
     *
     * @return 0 成功、1 失敗
     */
    protected int closeFile() {
        return releaseCodecAndExtractor();
    }

    /**
     * 指定されたPresentationTimeに最も近いIフレームをデコードする
     *
     * @param presentationTimeUs シーク先フレームのPresentationTime[usec]
     * @return デコード画像を含むVideoFrameクラスインスタンス(デコード失敗の場合はnull)
     */
    protected @Nullable VideoFrame decodeIFrame(long presentationTimeUs) {
        if (!isValidPresentationTime(presentationTimeUs)) {
            return null;
        }

        mExtractor.seekTo(presentationTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        mSeekedPresentationTime = mExtractor.getSampleTime();
        mWasFrameRendered = false;

        // デコード待ち
        while (true) {
            if (mWasFrameRendered) {
                return new VideoFrame(mSeekedPresentationTime, null);
            }
        }
    }

    /**
     * 範囲内のすべてのI/P/Bフレームをデコード(startとendの時間が同じならシングルフレームデコード)
     *
     * @param startTimeUs デコード範囲の先頭フレームのPresentation Time[usec]
     * @param endTimeUs   デコード範囲の最終フレームのPresentation Time[usec]
     */
    protected void decodeFrames(long startTimeUs, long endTimeUs) {

    }

    /**
     * Presentation Timeが有効な値であるかチェックする
     *
     * @param presentationTimeUs Presentation Time[usec]
     * @return 有効ならtrue, そうでなければfalse
     */
    private boolean isValidPresentationTime(long presentationTimeUs) {
        if (presentationTimeUs < 0) {
            Log.d(TAG, "Error: presentationTimeUs " + presentationTimeUs + " is negative");
            return false;
        }
        if (presentationTimeUs > mVideoInfo.getDurationUs()) {
            Log.d(TAG, "Error: presentationTimeUs " + presentationTimeUs + " is over duration");
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        mInputBuffers = mCodec.getInputBuffers();
        mOutputBuffers = mCodec.getOutputBuffers();

        while (!this.isInterrupted()) {
            synchronized (this) {
                while (!mWasFrameRendered) {
                    if (this.isInterrupted()) {
                        break;
                    }
                    feedInputBuffer();
                    drainOutputBuffer();
                }
            }
        }
        mOutputDone = true;
    }

    /**
     * MediaCodecへデコードするフレームをInputする
     *
     * @return Inputできたらtrue、そうでなければfalse
     */
    private boolean feedInputBuffer() {
        try {
            int inputIndex = mCodec.dequeueInputBuffer(0);
            if (inputIndex < 0) {
                return false;
            }
            ByteBuffer inputBuffer = mInputBuffers[inputIndex];
            int chunkSize = mExtractor.readSampleData(inputBuffer, 0);
            if (chunkSize < 0) {
                mExtractor.seekTo(mSeekedPresentationTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                chunkSize = mExtractor.readSampleData(inputBuffer, 0);
            }

            long presentationTimeUs = mExtractor.getSampleTime();
            mCodec.queueInputBuffer(inputIndex, 0, chunkSize, presentationTimeUs, 0);
            mExtractor.advance();

        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * MediaCodecの出力データを取得する
     *
     * @return 出力データがさらに取得できそうならtrue、そうでなければfalse
     */
    @SuppressWarnings("deprecation")
    private boolean drainOutputBuffer() {
        if (mOutputDone) {
            return false;
        }

        try {
            int outputIndex = mCodec.dequeueOutputBuffer(mBufferInfo, 0);
            if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.d(TAG, "decoder output format changed: " + mCodec.getOutputFormat());
                return true;
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.d(TAG, "decoder output buffers changed");
                mOutputBuffers = mCodec.getOutputBuffers();
                return true;
            } else if (outputIndex < 0) {
                return false;
            }

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                mOutputDone = true;
                return false;
            }

            boolean renderFlag = (mBufferInfo.presentationTimeUs == mSeekedPresentationTime);
            mCodec.releaseOutputBuffer(outputIndex, renderFlag);
            if (renderFlag) {
                mWasFrameRendered = true;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * デコーダーをセットアップして起動させる
     *
     * @param filePath  動画ファイルのパス
     * @param surface   描画するSurfaceインスタンス
     * @param videoInfo 動画情報を格納するVideoInfoインスタンス
     * @return ステータス (0 成功、1 ファイル読み取りエラー 2 フォーマットエラー)
     */
    private int setupDecoderAndExtractor(String filePath, Surface surface, VideoInfo videoInfo) {
        if (this.getState() != State.NEW) {
            Log.e(TAG, "State of Decoder Thread is Not NEW! : State." + this.getState());
            releaseCodecAndExtractor();
            return VideoController.FileStatus.OPEN_ERROR.getCode();
        }

        mExtractor = new MediaExtractor();
        try {
            mExtractor.setDataSource(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return VideoController.FileStatus.OPEN_ERROR.getCode();
        }
        int i = selectTrack(mExtractor);
        if (i < 0) {
            Log.e(TAG, "No avairable video track found in " + filePath);
            return VideoController.FileStatus.FORMAT_ERROR.getCode();
        }
        mExtractor.selectTrack(i);
        MediaFormat format = mExtractor.getTrackFormat(i);

        videoInfo.setDurationUs(format.getLong(MediaFormat.KEY_DURATION));
        videoInfo.setWidth(format.getInteger(MediaFormat.KEY_WIDTH));
        videoInfo.setHeight(format.getInteger(MediaFormat.KEY_HEIGHT));
        try {
            videoInfo.setCaptureRate(format.getInteger(MediaFormat.KEY_CAPTURE_RATE));
        } catch (Exception e) {
            double captureRate = calcurateCaptureRate();
            if (captureRate < 0) {
                captureRate = 30; //撮影レートが読み取れない場合は仮で30fpsとする
            }
            videoInfo.setCaptureRate(captureRate);
        }

        try {
            mCodec = MediaCodec.createDecoderByType(format.getString("mime"));
            mCodec.configure(format, surface, null, 0);
            mCodec.start();
        } catch (Exception e) {
            Log.e(TAG, filePath + "\n" + e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "Decoder Setup Failed!");
            return VideoController.FileStatus.OPEN_ERROR.getCode();
        }

        return VideoController.FileStatus.SUCCESS.getCode();
    }

    /**
     * ストリーム中の最初の動画トラックのインデックスを取得する
     *
     * @param mediaExtractor MediaExtractorインスタンス
     * @return 動画トラック番号(動画が見つからない場合は-1)
     */
    private static int selectTrack(MediaExtractor mediaExtractor) {
        int j = mediaExtractor.getTrackCount();
        for (int i = 0; i < j; i++) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            String mime = format.getString("mime");
            if (mime.startsWith("video/")) {
                Log.d(TAG, "Extractor selected track " + i + " (" + mime + "): " + format);
                return i;
            }
        }

        return -1;
    }

    /**
     * デコード終了時にデコーダーを解放する
     *
     * @return 0 成功、1 失敗
     */
    private int releaseCodecAndExtractor() {
        this.interrupt();

        while (mCodec != null || mExtractor != null) {
            if (!mOutputDone) {
                continue;
            }

            if (mCodec != null) {
                flushDecoderData();
                mCodec.stop();
                try {
                    mCodec.release();
                    mCodec = null;
                } catch (Exception e) {
                    Log.e(TAG, e.toString() + "\n" + e.getMessage());
                    e.printStackTrace();
                    return 1;
                }
            }

            if (mExtractor != null) {
                try {
                    mExtractor.release();
                    mExtractor = null;
                } catch (Exception e) {
                    Log.e(TAG, e.toString() + "\n" + e.getMessage());
                    e.printStackTrace();
                    return 1;
                }
            }
        }

        return 0;
    }

    /**
     * デコーダのバッファをクリアする
     */
    private void flushDecoderData() {
        mCodec.flush();
    }

    /**
     * 1フレームの時間の長さから撮影フレームレートを算出する
     *
     * @return 撮影フレームレート(fps)
     */
    private double calcurateCaptureRate() {
        long originalPosition = mExtractor.getSampleTime();

        mExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        long currentFramePts = mExtractor.getSampleTime();
        long nextFramePts = -1;

        int count = 0;
        while (count < 20) {
            mExtractor.advance();
            nextFramePts = mExtractor.getSampleTime();
            if (nextFramePts <= currentFramePts) {
                break;
            }

            count++;
            currentFramePts = nextFramePts;
        }

        // MediaExtractorのポインタを元の位置に戻す
        mExtractor.seekTo(originalPosition, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

        if (count == 0) {
            return -1;
        }
        double captureRate = (double) 1000000 / (nextFramePts / count);
        captureRate = (double) Math.round(captureRate * 100) / 100; //小数第3位で四捨五入
        return captureRate;
    }
}
