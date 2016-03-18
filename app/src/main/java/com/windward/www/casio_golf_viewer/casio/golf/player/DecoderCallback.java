package com.windward.www.casio_golf_viewer.casio.golf.player;

/**
 * デコード完了などDecoderクラスのイベント通知用のInterface
 */
public interface DecoderCallback {

    /**
     * 範囲指定デコードでデコード完了したフレームの情報を返す関数
     *
     * @param videoFrame デコード完了したフレームの{@link VideoFrame}インスタンス
     */
    void decodeFinish(VideoFrame videoFrame);

}
