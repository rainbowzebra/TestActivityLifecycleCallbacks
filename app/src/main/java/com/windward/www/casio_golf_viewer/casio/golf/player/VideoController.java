package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Surface;

/**
 * 動画コントローラークラス
 */
public class VideoController {

    private static final String TAG = VideoController.class.getSimpleName();

    /**
     * デコーダーを管理する{@link Decoder}インスタンス
     */
    private Decoder mDecoder;

    /**
     * デコード画像のバッファを行う{@link ImageBuffer}インスタンス
     */
    private ImageBuffer mImageBuffer;

    /**
     * ファイルオープン時のステータスコード<br/>
     * <br/>
     * 0 成功<br/>
     * 1 ファイルオープンエラー<br/>
     * 2 フォーマットエラー
     */
    public enum FileStatus {
        SUCCESS(0),
        OPEN_ERROR(1),
        FORMAT_ERROR(2);

        private final int code;

        FileStatus(int code) {
            this.code = code;
        }

        /**
         * ステータスコードを取得する
         *
         * @return 0 成功、1 ファイルオープンエラー、2 フォーマットエラー
         */
        public int getCode() {
            return this.code;
        }
    }

    /**
     * コンストラクタ
     *
     * @param surface 動画を描画する{@link Surface}のインスタンス
     */
    public VideoController(@NonNull Surface surface) {
        mDecoder = new Decoder(surface);
    }

    /**
     * 動画ファイルを開いて、動画情報の格納とデコーダーの初期設定と起動を行う<br/>
     * <br/>
     * ファイルを開き直す場合はVideoControllerインスタンスを作り直す必要がある
     *
     * @param path      動画ファイルのパス
     * @param videoInfo 動画情報を格納する{@link VideoInfo}のインスタンス
     * @return 0 成功、1 ファイルオープンエラー、2 フォーマットエラー
     */
    public int openFile(@NonNull String path, @NonNull VideoInfo videoInfo) {
        int status = mDecoder.openFile(path, videoInfo);
        if (status == FileStatus.SUCCESS.getCode()) {
            mDecoder.start();
//            mImageBuffer = new ImageBuffer(videoInfo);
        }

        return status;
    }

    /**
     * デコーダーを終了する<br/>
     * <br/>
     * このメソッドの実行後に再びデコードを行う場合にはVideoControllerインスタンスを作り直す必要がある
     *
     * @return 0 成功、1 失敗
     */
    public int closeFile() {
        return mDecoder.closeFile();
    }

    /**
     * 指定フレームのデータをバッファから取得する
     *
     * @param presentationTimeUs 取得したいフレームのPresentation Time[usec]
     * @param playRate           再生速度倍率 (ImageBuffer.updateBufferでバッファリングする時に使用する)
     * @return デコード画像を含む {@link VideoFrame}のインスタンス (データがない場合はnull)
     */
    public @Nullable VideoFrame getVideoFrame(long presentationTimeUs, double playRate) {
        return mDecoder.decodeIFrame(presentationTimeUs);
    }

    /**
     * 指定されたPresentation Time近傍のIフレームのデータをデコーダーから直接取得する
     *
     * @param presentationTimeUs デコードしたいフレームのPresentation Time[usec]
     * @param playRate           再生速度倍率 (ImageBuffer.updateBufferでバッファリングする時に使用する)
     * @return デコード画像を含む {@link VideoFrame}のインスタンス (デコード失敗の場合はnull)
     */
    public @Nullable VideoFrame getVideoKeyFrame(long presentationTimeUs, double playRate) {
        return mDecoder.decodeIFrame(presentationTimeUs);
    }

}