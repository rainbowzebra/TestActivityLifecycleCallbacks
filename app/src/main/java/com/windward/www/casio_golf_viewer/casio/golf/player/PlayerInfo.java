package com.windward.www.casio_golf_viewer.casio.golf.player;

public class PlayerInfo {

	private int mPlayerId;			// プレイヤーID
	private long mCurrentPtsUs;		// グループ作成時の現在時刻[μs]
	private double mCaptureRate;	//　撮影フレームレート
	private long mVideoDurationUs;	// 動画時間[μs]

	/**
	 * コンストラクタ
	 * @param playerId プレイヤーID
	 * @param currentPtsUs グループ作成時の現在時刻[μs]
	 * @param captureRate 撮影フレームレート
	 * @param videoDurationUs 動画時間[μs]
	 */
	public PlayerInfo(int playerId, long currentPtsUs, double captureRate, long videoDurationUs) {
		mPlayerId = playerId;
		mCurrentPtsUs = currentPtsUs;
		mCaptureRate = captureRate;
		mVideoDurationUs = videoDurationUs;
	}

	/**
	 * プレイヤーID取得
	 * @return プレイヤーID
	 */
	protected int getPlayerId() {
		return mPlayerId;
	}

	/**
	 * 現在時刻取得
	 * @return 現在時刻[μs]
	 */
	protected long getCurrentPtsUs() {
		return mCurrentPtsUs;
	}

	/**
	 * 撮影フレームレート取得
	 * @return 撮影フレームレート
	 */
	protected double getCaptureRate(){ return mCaptureRate; }

	/**
	 * 動画時間取得
	 * @return 動画時間[μs]
	 */
	protected long getVideoDurationUs() {
		return mVideoDurationUs;
	}
}
