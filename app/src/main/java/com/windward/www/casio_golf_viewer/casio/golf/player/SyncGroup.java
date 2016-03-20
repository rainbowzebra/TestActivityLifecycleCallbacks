package com.windward.www.casio_golf_viewer.casio.golf.player;

import java.util.ArrayList;

class SyncGroup {

	private int mGroupId;// グループID
	private ArrayList<Integer> mPlayerIdList;// プレイヤーIDリスト

	/**
	 * コンストラクタ
	 * @param groupId グループID
	 * @param playerInfoList プレイヤー情報リスト
	 */
	protected SyncGroup(int groupId, ArrayList<PlayerInfo> playerInfoList) {

		mGroupId = groupId;
		mPlayerIdList = new ArrayList<Integer>();

		for(int i=0;i<playerInfoList.size();i++){
			mPlayerIdList.add(playerInfoList.get(i).getPlayerId());
		}

	}

	/**
	 プレイヤー所持チェック
	 - Parameter playerId: プレイヤーID
	 - Returns: true:プレイヤー所持 false:プレイヤー非所持
	 */
	/**
	 * プレイヤー所持チェック
	 * @param playerId プレイヤーID
	 * @return  true:プレイヤー所持 false:プレイヤー非所持
	 */
	protected boolean hasPlayer(int playerId) {
		return mPlayerIdList.contains(playerId);
	}

	/**
	 * プレイヤー情報リスト取得
	 * @return プレイヤー情報リスト
	 */
	public ArrayList<Integer> getPlayerIdList() {
		return mPlayerIdList;
	}

	/**
	 * グループID取得
	 * @return グループID
	 */
	public int getmGroupId() {
		return mGroupId;
	}
}
