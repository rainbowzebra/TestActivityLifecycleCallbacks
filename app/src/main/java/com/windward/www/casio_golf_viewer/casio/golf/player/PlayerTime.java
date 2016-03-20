package com.windward.www.casio_golf_viewer.casio.golf.player;

public class PlayerTime {

	private int mPlayerId;		//プレイヤーID
	private long mOffsetNs;		//オフセット値
	private long mTotalTimeNs;	//トータル時間[ns]


	/**
	 * コンストラクタ
	 * @param playerId プレイヤーID
	 * @param offsetNs オフセット値[ns]
	 * @param totalTimeNs トータル時間[ns]
	 */
	protected PlayerTime(int playerId, long offsetNs, long totalTimeNs) {
		mPlayerId = playerId;
		mOffsetNs = offsetNs;
		mTotalTimeNs = totalTimeNs;
	}

	/**
	 * プレイヤーID取得
	 * @return プレイヤーID
	 */
	public int getPlayerId() {
		return mPlayerId;
	}

	/**
	 * オフセット値取得
	 * @return オフセット値[ns]
	 */
	public long getOffsetNs() {
		return mOffsetNs;
	}

	/**
	 * トータル時間取得
	 * @return トータル時間[ns]
	 */
	public long getTotalTimeNs() {
		return mTotalTimeNs;
	}

}
