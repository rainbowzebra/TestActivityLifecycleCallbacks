package com.windward.www.casio_golf_viewer.casio.golf.player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *タイムマネージャ
 *
 * 同期グループごとの再生時刻管理を行う
 * 同期していない状態では1ストリームで1同期グループとして扱う
 */
public class TimeManager {

	// エラーコード
	public static final long ERROR_NOTFOUND_PTS = Long.MAX_VALUE;		// エラー:IDが存在しない
	public static final double ERROR_NOTFOUND_RATE = Double.MAX_VALUE;	// エラー:IDが存在しない

	private final long UPDATE_TIMER_INTERVAL = 5;// 更新タイマー間隔 5(ms)

	private ArrayList<GroupTime> mGroupTimeList;// グループタイムリスト
	private Timer mUpdateTimer;					// 更新タイマー
	private long mPreAbsTime;					// 前回の絶対時間


	/**
	 * コンストラクタ
	 */
	public TimeManager() {
		mGroupTimeList = new ArrayList<GroupTime>();
	}

	/**
	 * グループタイム作成
	 *
	 * SinglePlayerControllerから呼ぶ場合は、GroupIdは0等に設定する
	 * @param syncGroup 同期グループ配列
	 * @return グループタイム配列
	 */
	protected ArrayList<GroupTime> createGroupTime(ArrayList<ArrayList<PlayerInfo>> syncGroup) {

		int groupId = 0;
		for(int i=0; i<syncGroup.size();i++){

			try {
				GroupTime gt = new GroupTime(groupId, syncGroup.get(i));
				mGroupTimeList.add(gt);
				groupId++;
			}catch (IllegalArgumentException e){
				mGroupTimeList.clear();
				break;
			}
		}

		if(mGroupTimeList.size() != 0){
			return mGroupTimeList;
		}else{
			return null;
		}
	}

	/**
	 * 終了
 	 */
	protected void finish() {
		stopUpdateTimer();
		mGroupTimeList.clear();
	}

	/**
	 * 時刻取得(プレイヤーID指定)
	 *
	 *  指定プレイヤーの保持するストリームの時間を返す、playerIdが存在しない場合はエラーコードを返す
	 * @param playerId プレイヤーID
	 * @return 時刻[μs]　Long.MAX_VALUE:IDが存在しない
	 */
	public long getPresentationTimeUsFromPlayer(int playerId) {
		
		GroupTime groupTime = getGroupTime(playerId);

		if (groupTime != null){
			long currentPtsUs = groupTime.getCurrentPtsNs(playerId)/1000;
			return currentPtsUs;
		}
		return TimeManager.ERROR_NOTFOUND_PTS;
	}

	/**
	 * 時刻取得(グループID指定)
	 *
	 * @param groupId グループID
	 * @return 時刻[μs]　Long.MAX_VALUE:IDが存在しない
	 */
	public long getPresentationTimeUsFromGroup(int groupId) {
		if (mGroupTimeList.size() - 1 >= groupId) {
			GroupTime groupTime = mGroupTimeList.get(groupId);
			long currentPtsUs = groupTime.getCurrentPtsNs()/1000;
			return currentPtsUs;
		}
		return TimeManager.ERROR_NOTFOUND_PTS;
	}

	/**
	 * 速度倍率設定(プレイヤーID指定)
	 *
	 * 速度倍率が変更になった場合に指定する
	 * @param playerId プレイヤーID
	 * @param playRate 速度倍率
	 */
	public void setPlayRateFromPlayer(int playerId, double playRate) {

		GroupTime groupTime = getGroupTime(playerId);

		if (groupTime != null){
			groupTime.setPlayRate(playRate);

			//停止状態の場合更新タイマーを止める
			if(playRate == 0){
				// 他のグループの速度倍率が全て0の場合、更新タイマーを停止する
				for(int i=0;i<mGroupTimeList.size();i++) {
					GroupTime otherGroupTime = mGroupTimeList.get(i);
					if (groupTime.getGroupId() != otherGroupTime.getGroupId()) {
						if (otherGroupTime.getPlayRate() != 0) {
							return;
						}
					}
				}
				stopUpdateTimer();
			}else{
				setUpdateTimer();
			}
		}
	}

	/**
	 * 速度倍率設定(グループID指定)
	 *
	 * 速度倍率が変更になった場合に指定する
	 * @param groupId グループID
	 * @param playRate 速度倍率
	 */
	public void setPlayRateFromGroup(int groupId, double playRate) {

		if(mGroupTimeList.size() - 1 >= groupId) {
			GroupTime groupTime = mGroupTimeList.get(groupId);
			groupTime.setPlayRate(playRate);

			//停止状態の場合更新タイマーを止める
			if(playRate == 0){
				for(int i=0;i<mGroupTimeList.size();i++) {
					GroupTime otherGroupTime = mGroupTimeList.get(i);
					if (groupTime.getGroupId() != otherGroupTime.getGroupId()) {
						if (otherGroupTime.getPlayRate() != 0) {
							return;
						}
					}
				}
				stopUpdateTimer();
			}else{
				setUpdateTimer();
			}
		}
	}

	/**
	 * 速度倍率取得(プレイヤーID指定)
	 *
	 * @param playerId プレイヤーID
	 * @return 速度倍率
	 */
	public double getPlayRateFromPlayer(int playerId) {

		GroupTime groupTime = getGroupTime(playerId);

		if(groupTime != null){
			return groupTime.getPlayRate();
		}
//		return 0;
		return ERROR_NOTFOUND_RATE;
	}

	/**
	 * 速度倍率取得(グループID指定)
	 *
	 * @param groupId グループID
	 * @return 速度倍率
	 */
	public double getPlayRateFromGroup(int groupId) {

		if (mGroupTimeList.size() - 1 >= groupId) {
			GroupTime groupTime = mGroupTimeList.get(groupId);
			return groupTime.getPlayRate();
		}
//		return 0;
		return ERROR_NOTFOUND_RATE;
	}


	/**
	 * 現在時刻取得(グループID指定)
	 *
	 * @param playerId グループID
	 * @param presentationTimeUs 時刻[μs]
	 */
	public void setCurrentPtsUsFromPlayer(int playerId, long presentationTimeUs) {

		GroupTime groupTime = getGroupTime(playerId);
		long ptsUs = presentationTimeUs;

		if(ptsUs > Long.MAX_VALUE/1000){
			ptsUs = Long.MAX_VALUE/1000;
		}

		if (groupTime != null) {
			groupTime.setCurrentPtsNs(playerId, ptsUs * 1000);
		}
	}

	/**
	 * 現在時刻設定(グループID指定)
	 *
	 * 現在の時刻をUIから変更された場合に設定する
	 * @param groupId グループID
	 * @param presentationTimeUs 時刻[μs]
	 */
	public void setCurrentPtsUsFromGroup(int groupId, long presentationTimeUs) {

		long ptsUs = presentationTimeUs;

		if(ptsUs > Long.MAX_VALUE/1000){
			ptsUs = Long.MAX_VALUE/1000;
		}

		if (mGroupTimeList.size() - 1 >= groupId) {
			GroupTime groupTime = mGroupTimeList.get(groupId);
			groupTime.setCurrentPtsNs(ptsUs * 1000);
		}
	}

	/**
	 * グループタイム取得(プレイヤーID指定)
	 *
	 * @param playerId プレイヤーID
	 * @return グループタイム
	 */
	private GroupTime getGroupTime(int playerId) {

		for(int i=0 ;i<mGroupTimeList.size(); i++) {

			GroupTime groupTime = mGroupTimeList.get(i);

			if(groupTime.hasPlayer(playerId)) {
				// 指定のプレイヤーが存在する場合
				return groupTime;
			}
		}
		return null;
	}

	/**
	 * 更新タイマー設定
	 */
	private void setUpdateTimer() {

		if(mUpdateTimer == null) {
			mPreAbsTime = System.nanoTime();
			mUpdateTimer = new Timer();
			mUpdateTimer.schedule(new TimerTask(){
				@Override
				public void run() {
					onUpdate();
				}
			},0, UPDATE_TIMER_INTERVAL);
		}
	}

	/**
	 * 更新タイマー停止
	 */
	private void stopUpdateTimer() {
		if(mUpdateTimer != null) {
			mUpdateTimer.cancel();
			mUpdateTimer = null;
		}
	}

	/**
	 *  更新タイマーデリゲート
	 */
	private void onUpdate() {
		long nowAbsTime = System.nanoTime();
		long passingTime = nowAbsTime - mPreAbsTime;

		for(int i=0;i<mGroupTimeList.size();i++){
			GroupTime groupTime = mGroupTimeList.get(i);
			groupTime.updateCurrentPtsNs(passingTime);
		}
		mPreAbsTime = nowAbsTime;
	}
}
