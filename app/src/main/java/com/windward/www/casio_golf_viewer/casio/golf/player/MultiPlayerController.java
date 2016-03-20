package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.content.Context;

import java.util.ArrayList;

/**
 *マルチプレイヤーコントローラ
 *
 * マルチストリームの場合に使用する
 * 同期時刻管理、同期操作管理の設定を行う
 */
public class MultiPlayerController {

	private TimeManager mTimeManager;								// タイムマネージャ
	private InstructionSyncController mInstructionSyncController;	// 操作同期コントローラ

	/**
	 * コンストラクタ
	 *
	 * @param context コンテキスト
	 */
	public MultiPlayerController(Context context) {
		mTimeManager = new TimeManager();
		mInstructionSyncController = new InstructionSyncController(context);
	}

	/**
	 *同期グループ作成
	 *
	 * TimeManagerとInstructionSyncControllerの設定を行う
	 * 同期グループが変更される度に呼ぶ、その際に引数で渡すPlayerInfoも作成する
	 * @param syncGroupList 同期グループ配列 [[playerInfo1, playerInfo2],[playerInfo3],...]
	 * @return グループタイム配列(引数で指定されたグループの順番)、グループを作成できなかった場合はnullを返す
	 */
	public ArrayList<GroupTime> createSyncGroup(ArrayList<ArrayList<PlayerInfo>> syncGroupList) {

		finish();

		if(syncGroupList == null)
			return null;

		int playerInfoCount = 0;
		for (int i=0;i<syncGroupList.size();i++){
			playerInfoCount += syncGroupList.get(i).size();
		}

		if(playerInfoCount < 2) {
			// PlayerInfoの数が2より小さい場合
			return null;
		}

		ArrayList<GroupTime> groupTimeList = mTimeManager.createGroupTime(syncGroupList);
		if(groupTimeList != null){
			mInstructionSyncController.createSyncGroup(syncGroupList);
		}

		return groupTimeList;
	}

	/**
	 * タイムマネージャ取得
	 *
	 * @return タイムマネージャ
	 */
	public TimeManager getTimeManager() {
		return mTimeManager;
	}

	/**
	 *操作同期コントローラ取得
	 *
	 * @return 操作同期コントローラ
	 */
	public InstructionSyncController getInstructionSyncController() {
		return mInstructionSyncController;
	}

	/**
	 * ズーム同期設定
	 *
	 *  時刻同期とは別に設定する
	 * @param syncFlag 同期フラグ
	 */
	public void setZoomSyncConfig(boolean syncFlag) {
		mInstructionSyncController.setZoomSyncConfig(syncFlag);
	}

	/**
	 * 回転同期設定
	 *
	 *  時刻同期とは別に設定する
	 * @param syncFlag 同期フラグ
	 */
	public void setRotationSyncConfig(boolean syncFlag) {
		mInstructionSyncController.setRotateSyncConfig(syncFlag);
	}

	/**
	 * 反転同期設定
	 *
	 * 　時刻同期とは別に設定する
	 * @param syncFlag 同期フラグ
	 */
	public void setReversalSyncConfig(boolean syncFlag) {
		mInstructionSyncController.setReversalSyncConfig(syncFlag);
	}

	/**
	 * 終了
	 */
	public void finish() {
		mTimeManager.finish();
		mInstructionSyncController.finish();
	}
}
