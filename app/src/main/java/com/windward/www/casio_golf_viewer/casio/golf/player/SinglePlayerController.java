package com.windward.www.casio_golf_viewer.casio.golf.player;

import java.util.ArrayList;

/**
 * シングルプレイヤーコントローラ
 *
 * シングルストリームの場合に使用する
 * 同期時刻管理の設定を行う
 */
public class SinglePlayerController {

	private TimeManager mTimeManager;// タイムマネージャ

	/**
	 * コンストラクタ
	 */
	public SinglePlayerController() {
		mTimeManager = new TimeManager();
	}

	/**
	 * タイムマネージャ設定
	 *
	 * @param playerInfo プレイヤー情報
	 */
	public void createTimeManager(PlayerInfo playerInfo) {
		finish();

		if(playerInfo == null)
			return;

		ArrayList<PlayerInfo> singlePlayerList = new ArrayList<PlayerInfo>();
		singlePlayerList.add(playerInfo);
		ArrayList<ArrayList<PlayerInfo>> singleGroupPlayerList = new ArrayList<ArrayList<PlayerInfo>>();
		singleGroupPlayerList.add(singlePlayerList);

		mTimeManager.createGroupTime(singleGroupPlayerList);
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
	 * 終了
	 */
	public void finish() {
		mTimeManager.finish();
	}
}
