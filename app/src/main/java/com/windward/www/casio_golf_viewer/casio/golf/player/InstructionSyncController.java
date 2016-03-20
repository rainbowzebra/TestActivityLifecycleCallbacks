package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * 操作同期コントローラ
 *
 * 同期グループごとの操作同期管理を行う
 * 拡大・縮小、回転、反転の同期は時刻の同期とは別で管理する
 */
public class InstructionSyncController {

	// 操作タイプ
	public final static int INSTRUCTION_TYPE_SEEKBAR = 0;	// シークバー
	public final static int INSTRUCTION_TYPE_SWIPE = 1;	// スワイプ
	public final static int INSTRUCTION_TYPE_PLAY = 2;	// 再生

	// BroadCastReceiver アクションラベル名
	public final static String ACTION_INSTRUCTION_TYPE = "action_instruction_type";	// 操作タイプ設定
	public final static String ACTION_ZOOM_LEVEL = "action_zoom_level";				// ズームレベル設定
	public final static String ACTION_ROTATION_ANGLE = "action_rotation_angle"; 		// 回転角度設定
	public final static String ACTION_REVERSAL_TYPE = "action_reversal_type";			// 反転タイプ設定
	public final static String ACTION_TRANSLATION = "action_translation";				// 移動設定

	// BroadCastReceiver パラメータキー
	public final static String BCEXTRA_PLAYER_LIST = "bcextra_player_list";			// プレイヤーリスト
	public final static String BCEXTRA_INSTRUCTION_TYPE = "bcextra_instruction_type";	// 操作タイプ
	public final static String BCEXTRA_DIFF_ZOOM_LEVEL = "bcextra_diff_zoom_level";	// ズームレベル
	public final static String BCEXTRA_CENTER_POINT_X = "bcextra_center_point_x";		// 中心位置X
	public final static String BCEXTRA_CENTER_POINT_Y = "bcextra_center_point_y";		// 中心位置Y
	public final static String BCEXTRA_ROTATION_ANGLE = "bcextra_rotation_angle";		// 回転角度
	public final static String BCEXTRA_REVERSAL_TYPE = "bcextra_reversal_type";		// 反転タイプ
	public final static String BCEXTRA_DIFF_MOVEMENT_X = "bcextra_diff_movement_x";	// 差分移動量X
	public final static String BCEXTRA_DIFF_MOVEMENT_Y = "bcextra_diff_movement_y";	// 差分移動量Y


	private boolean mZoomSyncFlag;				// ズーム同期フラグ
	private boolean mRotateSyncFlag;			// 回転同期フラグ
	private boolean mReversalSyncFlag;			// 反転同期フラグ
	private ArrayList<SyncGroup> mSyncGroupList;// 同期グループリスト

	private final Context mContext;

	/**
	 * 同期グループ作成
	 *
	 * @param context コンテキスト
	 */
	protected InstructionSyncController(Context context) {
		mContext = context;
		mZoomSyncFlag = true;
		mRotateSyncFlag = true;
		mReversalSyncFlag = true;
		mSyncGroupList = new ArrayList<SyncGroup>();
	}

	/**
	 * 同期グループ作成
	 *
	 * @param syncGroup 同期グループ配列
	 */
	protected void createSyncGroup(ArrayList<ArrayList<PlayerInfo>> syncGroup) {

		int groupId = 0;
		ArrayList<SyncGroup> newSyncGroupList = new ArrayList<SyncGroup>();
		for(int i=0; i<syncGroup.size();i++){
			newSyncGroupList.add(new SyncGroup(groupId,syncGroup.get(i)));
			groupId++;
		}
		mSyncGroupList  = newSyncGroupList;
	}

	/**
	 * ズーム同期設定
	 *
	 * @param syncFlag 同期フラグ
	 */
	protected void setZoomSyncConfig(boolean syncFlag) {
		mZoomSyncFlag = syncFlag;
	}

	/**
	 * 回転同期設定
	 *
	 * @param syncFlag 同期フラグ
	 */
	protected void setRotateSyncConfig(boolean syncFlag) {
		mRotateSyncFlag = syncFlag;
	}

	/**
	 * 反転同期設定
	 *
	 * @param syncFlag 同期フラグ
	 */
	protected void setReversalSyncConfig(boolean syncFlag) {
		mReversalSyncFlag = syncFlag;
	}

	/**
	 * 終了
	 */
	protected void finish() {
		mSyncGroupList.clear();
		mZoomSyncFlag = true;
		mRotateSyncFlag = true;
		mReversalSyncFlag = true;
	}

	/**
	 * 操作タイプ設定（プレイヤーID指定）
	 *
	 * @param playerId プレイヤーID
	 * @param instructionType 操作タイプ 再生：0 スワイプ（コマ送り）：1 シークバー：2
	 */
	public void setInstructionTypeFromPlayer(int playerId, int instructionType) {

		ArrayList<Integer> playerList = getPlayerListFromPlayer(playerId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_INSTRUCTION_TYPE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putInt(BCEXTRA_INSTRUCTION_TYPE, instructionType);
		intent.putExtras(bundle);
		//intent.putExtra(BCEXTRA_PLAYER_LIST, playerList);
		//intent.putExtra(BCEXTRA_INSTRUCTION_TYPE, instructionType);

		mContext.sendBroadcast(intent);
	}


	/**
	 * 操作タイプ設定（グループID指定）
	 *
	 * @param groupId グループID
	 * @param instructionType 操作タイプ
	 */
	public void setInstructionTypeFromGroup(int groupId, int instructionType) {
		ArrayList<Integer> playerList = getPlayerListFromGroup(groupId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_INSTRUCTION_TYPE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putInt(BCEXTRA_INSTRUCTION_TYPE, instructionType);
		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * ズームレベル設定（プレイヤーID指定）
	 *
	 * @param playerId プレイヤーID
	 * @param centerPoint ズームレベル
	 * @param diffZoomLevel 中心位置
	 */
	public void setZoomLevelFromPlayer(int playerId, PointF centerPoint, float diffZoomLevel) {

		if(mZoomSyncFlag == false || centerPoint == null){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromPlayer(playerId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_ZOOM_LEVEL);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_DIFF_ZOOM_LEVEL, diffZoomLevel);
		bundle.putFloat(BCEXTRA_CENTER_POINT_X, centerPoint.x);
		bundle.putFloat(BCEXTRA_CENTER_POINT_Y, centerPoint.y);
		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * ズームレベル設定（グループID指定）
	 *
	 * @param groupId グループID
	 * @param centerPoint ズームレベル
	 * @param diffZoomLevel 中心位
	 */
	public void setZoomLevelFromGroup(int groupId, PointF centerPoint, float diffZoomLevel) {

		if(mZoomSyncFlag == false || centerPoint == null){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromGroup(groupId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_ZOOM_LEVEL);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_DIFF_ZOOM_LEVEL, diffZoomLevel);
		bundle.putFloat(BCEXTRA_CENTER_POINT_X, centerPoint.x);
		bundle.putFloat(BCEXTRA_CENTER_POINT_Y, centerPoint.y);
		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * 回転角度設定設定（プレイヤーID指定）
	 *
	 * @param playerId プレイヤーID
	 * @param rotationAngle 回転角度
	 */
	public void setRotationAngleFromPlayer(int playerId, float rotationAngle) {

		if(mRotateSyncFlag == false){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromPlayer(playerId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_ROTATION_ANGLE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_ROTATION_ANGLE, rotationAngle);

		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}


	/**
	 * 回転角度設定設定（グループID指定）
	 *
	 * @param groupId グループID
	 * @param rotationAngle 回転角度
	 */
	public void setRotationAngleFromGroup(int groupId, float rotationAngle) {

		if(mRotateSyncFlag == false){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromGroup(groupId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_ROTATION_ANGLE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_ROTATION_ANGLE, rotationAngle);

		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}


	/**
	 * 反転タイプ設定（プレイヤーID指定）
	 *
	 * @param playerId プレイヤーID
	 * @param reversalType 反転タイプ 上下:0 左右:1 上下左右:2
	 */
	public void setReversalTypeFromPlayer(int playerId, int reversalType) {

		if(mReversalSyncFlag == false){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromPlayer(playerId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_REVERSAL_TYPE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putInt(BCEXTRA_REVERSAL_TYPE, reversalType);

		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * 反転タイプ設定（グループID指定）
	 *
	 * @param groupId グループID
	 * @param reversalType 反転タイプ
	 */
	public void setReversalTypeFromGroup(int groupId, int reversalType) {

		if(mReversalSyncFlag == false){
			return;
		}

		ArrayList<Integer> playerList = getPlayerListFromGroup(groupId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_REVERSAL_TYPE);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putInt(BCEXTRA_REVERSAL_TYPE, reversalType);

		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * 移動量設定（プレイヤーID指定）
	 *
	 * @param playerId プレイヤーID
	 * @param diffMovementX 差分移動量X
	 * @param diffMovementY 差分移動量Y
	 */
	public void setTranslationFromPlayer(int playerId, float diffMovementX, float diffMovementY) {

		ArrayList<Integer> playerList = getPlayerListFromPlayer(playerId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_TRANSLATION);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_DIFF_MOVEMENT_X, diffMovementX);
		bundle.putFloat(BCEXTRA_DIFF_MOVEMENT_Y, diffMovementY);
		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}

	/**
	 * 移動量設定（グループID指定）
	 *
	 * @param groupId グループID
	 * @param diffMovementX 差分移動量X
	 * @param diffMovementY 差分移動量Y
	 */
	public void setTranslationFromGroup(int groupId, float diffMovementX, float diffMovementY) {

		ArrayList<Integer> playerList = getPlayerListFromGroup(groupId);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(ACTION_TRANSLATION);
		bundle.putIntegerArrayList(BCEXTRA_PLAYER_LIST, playerList);
		bundle.putFloat(BCEXTRA_DIFF_MOVEMENT_X, diffMovementX);
		bundle.putFloat(BCEXTRA_DIFF_MOVEMENT_Y, diffMovementY);
		intent.putExtras(bundle);

		mContext.sendBroadcast(intent);
	}


	/**
	 * プレイヤーリスト取得(プレイヤーID指定)
	 *
	 * @param playerId プレイヤーID
	 * @return プレイヤーリスト
	 */
	private ArrayList<Integer> getPlayerListFromPlayer(int playerId)  {

		SyncGroup syncGroup;

		for(int i=0;i<mSyncGroupList.size();i++){
			syncGroup = mSyncGroupList.get(i);
			if (syncGroup.hasPlayer(playerId)) {
				ArrayList<Integer> playerIdList = new ArrayList<Integer>(syncGroup.getPlayerIdList());
				int index = playerIdList.indexOf(playerId);
				playerIdList.remove(index);

				return playerIdList;
			}
		}
		return null;
	}


	/**
	 * プレイヤーリスト取得(グループID指定)
	 *
	 * @param groupId グループID
	 * @return プレイヤーリスト
	 */
	private ArrayList<Integer> getPlayerListFromGroup(int groupId){

		SyncGroup syncGroup;

		for(int i=0;i<mSyncGroupList.size();i++){
			syncGroup = mSyncGroupList.get(i);
			if (syncGroup.getmGroupId() == groupId) {
				return syncGroup.getPlayerIdList();
			}
		}
		return null;
	}
}
