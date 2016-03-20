package com.windward.www.casio_golf_viewer.casio.golf.player;

import java.util.ArrayList;

public class GroupTime {

	private String TAG = "GroupTime";

	private int mGroupId;  							// グループID
	private long mCurrentPtsNs;						// 現在の時刻
	private double mPlayRate;  						// 再生倍率
	private long mTotalTimeNs; 						// グループのトータル時間(ns)
	private ArrayList<PlayerTime> mPlayerTimeList;	// プレイヤータイムリスト

	/**
	 * コンストラクタ
	 *
	 * @param groupId グループID
	 * @param playerList プレイヤーリスト
	 */
	protected GroupTime(int groupId, ArrayList<PlayerInfo> playerList)  {

		mGroupId = groupId;
		long firstTimeUs = 0;
		long lastTimeUs = 0;
		boolean errFlag = false;

		// グループのトータルタイムを計算
		for(int i=0; i<playerList.size(); i++) {

			PlayerInfo playerInfo = playerList.get(i);
			long currentPtsUs = playerInfo.getCurrentPtsUs();
			long videoDurationUs = playerInfo.getVideoDurationUs();

			if (currentPtsUs < 0 || videoDurationUs < currentPtsUs) {
				// 現在時間が動画時間範囲外の場合
				errFlag = true;
				break;
			}else if(videoDurationUs < 0) {
				// 動画の総時間が負の値の場合
				errFlag = true;
				break;
			}

			if(firstTimeUs < currentPtsUs) {
				firstTimeUs = currentPtsUs;
			}
			if(lastTimeUs < videoDurationUs - currentPtsUs){
				lastTimeUs = videoDurationUs - currentPtsUs;
			}
		}

		mCurrentPtsNs = firstTimeUs * 1000;
		mPlayRate = 0;
		mTotalTimeNs = (firstTimeUs + lastTimeUs) * 1000;
		mPlayerTimeList = new ArrayList<PlayerTime>();

		if(errFlag)
			callConstructorError();

		for(int i=0; i<playerList.size(); i++){

			PlayerInfo playerInfo = playerList.get(i);

			long offsetNs = (firstTimeUs - playerInfo.getCurrentPtsUs()) * 1000;
			long totalTimeNs = playerInfo.getVideoDurationUs() * 1000;
			PlayerTime playerTime = new PlayerTime(playerInfo.getPlayerId(), offsetNs, totalTimeNs);
			mPlayerTimeList.add(playerTime);
		}
	}

	/**
	 * 引数エラー
	 * @throws IllegalArgumentException
	 */
	private void callConstructorError() throws IllegalArgumentException{
		IllegalArgumentException e = new IllegalArgumentException("");
		throw e;
	}

	/**
	 * 現在時刻更新
	 *
	 * @param absPassingTimeNs 経過時間[nsec]
	 */
	protected void updateCurrentPtsNs(long absPassingTimeNs) {
		mCurrentPtsNs += (absPassingTimeNs * mPlayRate);

		//Log.d(TAG, "absPassingTimeNs:"+Long.toString(absPassingTimeNs)+ "playRate:" + Double.toString(mPlayRate));

		if( mCurrentPtsNs < 0 ) {
			mCurrentPtsNs = 0;
		} else if (mCurrentPtsNs > mTotalTimeNs) {
			mCurrentPtsNs = mTotalTimeNs;
		}

	}

	/**
	 * 現在時刻設定
	 *
	 * @param playerId 経過時間[nsec]
	 * @param currentPtsNs 設定する時刻[nsec]
	 */
	protected void setCurrentPtsNs(int playerId, long currentPtsNs) {

		PlayerTime playerTime = getPlayerTime(playerId);

		if (playerTime != null) {
			mCurrentPtsNs = currentPtsNs + playerTime.getOffsetNs();
			if(mCurrentPtsNs < 0){
				mCurrentPtsNs = 0;
			} else if (mCurrentPtsNs > mTotalTimeNs) {
				mCurrentPtsNs = mTotalTimeNs;
			}
		}
	}

	/**
	 * 現在時刻設定
	 *
	 * @param currentPtsNs 設定する時刻[nsec]
	 */
	protected void setCurrentPtsNs(long currentPtsNs) {
		mCurrentPtsNs = currentPtsNs;
		if (mCurrentPtsNs < 0) {
			mCurrentPtsNs = 0;
		} else if (mCurrentPtsNs > mTotalTimeNs) {
			mCurrentPtsNs = mTotalTimeNs;
		}
	}

	/**
	 * 速度倍率設定
	 *
	 * @param playRate 速度倍率(実速度からの倍率)
	 */
	protected void setPlayRate(double playRate) {
		mPlayRate = playRate;
	}


	/**
	 * 速度倍率取得
	 *
	 * @return 速度倍率 速度倍率(実速度からの倍率)
	 */
	protected double getPlayRate() {
		return mPlayRate;
	}

	/**
	 * グループID取得
	 *
	 * @return グループID
	 */
	public int getGroupId() {
		return mGroupId;
	}


	/**
	 * 現在時刻取得
	 *
	 * @param playerId プレイヤーID
	 * @return 正:現在時刻[μsec]　負:-1:時間範囲外(最小値)、-2:時間範囲外(最大値)、-3:IDが存在しない
	 */
	protected long getCurrentPtsNs(int playerId) {

		PlayerTime playerTime = getPlayerTime(playerId);
		if (playerTime != null) {
			long currentPtsNs = mCurrentPtsNs - playerTime.getOffsetNs();
			return currentPtsNs;
		}
		return TimeManager.ERROR_NOTFOUND_PTS;
	}

	/**
	 * 現在時刻取得
	 *
	 * @return 正:現在時刻[μsec]　負:-1:時間範囲外(最小値)、-2:時間範囲外(最大値)、-3:IDが存在しない
	 */
	protected long getCurrentPtsNs() {
		return mCurrentPtsNs;
	}

	/**
	 * トータル時間取得
	 *
	 * @return 正:現在時刻[μsec]　負:-1:時間範囲外(最小値)、-2:時間範囲外(最大値)、-3:IDが存在しない
	 */
	public long getTotalTimeUs() {
		return mTotalTimeNs / 1000;
	}

	/**
	 * プレイヤータイムリスト取得
	 *
	 * @return プレイヤータイムリスト
	 */
	public ArrayList<PlayerTime> getPlayerTimeList() {

		return mPlayerTimeList;
	}


	/**
	 * 指定したIDのプレイヤーを保持しているかの確認関数
	 *
	 * @param playerId プレイヤーID
	 * @return 指定したIDを保持している場合：true 指定したIDを保持していない場合：false
	 */
	protected boolean hasPlayer(int playerId) {

		for(int i=0; i<mPlayerTimeList.size();i++){
			PlayerTime playerTime = mPlayerTimeList.get(i);

			if( playerTime.getPlayerId() == playerId ){
				return true;
			}
		}
		return false;
	}


	/**
	 * プレイヤー時間取得
	 *
	 * @param playerId プレイヤーID
	 * @return プレイヤー時間
	 */
	protected PlayerTime getPlayerTime(int playerId) {

		for(int i=0; i<mPlayerTimeList.size();i++){
			PlayerTime playerTime = mPlayerTimeList.get(i);

			if( playerTime.getPlayerId() == playerId ){
				return playerTime;
			}
		}
		return null;
	}

}
