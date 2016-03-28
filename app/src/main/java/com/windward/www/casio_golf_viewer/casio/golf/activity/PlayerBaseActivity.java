package com.windward.www.casio_golf_viewer.casio.golf.activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.fragment.PlayerFirstFragment;
import com.windward.www.casio_golf_viewer.casio.golf.player.MultiPlayerController;
import com.windward.www.casio_golf_viewer.casio.golf.player.PlayerInfo;
import com.windward.www.casio_golf_viewer.casio.golf.player.SinglePlayerController;
import com.windward.www.casio_golf_viewer.casio.golf.player.VideoInfo;
import java.util.ArrayList;


/**
 * Created by kato-hy on 2015/12/07.
 */
public class PlayerBaseActivity extends FragmentActivity {
    String TAG = "PlayerBaseActivity";
    private ArrayList<PlayerFirstFragment> mPlayerFirstFragmentList;          // プレイヤーリスト
    private ArrayList<String> mVideoPathsArrayList;                            // 再生リスト
    private SinglePlayerController mSinglePlayerController = null;  // シングルプレイヤーコントローラのインスタンス
    private MultiPlayerController mMultiPlayerController = null;    // マルチプレイヤーコントローラのインスタンス
    private ToggleButton mSyncBtn;                                  // 同期ボタンのインスタンス
    private int mNextId;                                             // 次のPlayerID
    private Context mContext;                                       // このアクティビティのコンテキスト


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //最初の呼び出しのみ
        if(savedInstanceState == null) {

            mContext = this;
            mNextId = 0;

            //ファイル選択アクティビティから画面モードと再生リストを取得
            Bundle bundle = getIntent().getExtras();
            //保存了待播放视频的路径。一个或者两个
            mVideoPathsArrayList = bundle.getStringArrayList("key_playlist");

            //プレイヤーリスト作成
            //有几个待播放视频就生成几个PlayerFragment！！！且将这些PlayerFragment保存到mPlayerFragmentList中
            makePlayerFragmentList();

            //表示面の初期化
            //调用先前生成的PlayerFragment播放一个或者两个视频 注意使用的是PlayerFragment！！！！
            initView();

            //ファイルオープンとタイムマネージャの設定
            //利用PlayerFragment播放视频
            //每个PlayerFragment与一个路径关联起来
            initPlayerFragments();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();


        releaseTimeManager();
    }

    /**
     * PlayerFragmentのリストを作成
     * 　PlayList数分のPlayerを生成しリストに保持
     *
     * 有几个待播放视频就生成几个PlayerFragment！！！！！！！
     * 且将这些PlayerFragment保存到mPlayerFragmentList中
     */
    private void makePlayerFragmentList(){

        mPlayerFirstFragmentList = new ArrayList<PlayerFirstFragment>(); //プレイヤーリストを作成

        for (int i=0; i < mVideoPathsArrayList.size(); i++) {
            PlayerFirstFragment player = new PlayerFirstFragment();

            //PlayerIDを設定する
            Bundle bundle = new Bundle();
            bundle.putInt("key_PlayerId", generatePlayerId());
            player.setArguments(bundle);

            mPlayerFirstFragmentList.add(player);
        }
    }
    /**
     * PlayerIdの生成
     * @return PlayerID
     */
    private int generatePlayerId(){

        int playerId = mNextId;
        mNextId++;

        return playerId;
    }


    /**
     * Viewの初期化
     *
     * 界面初始化
     */
    private void initView() {

        //init();

        //播放两个视频
        if (mVideoPathsArrayList.size() == 2) {
            //2画面を起動
            initMultiPlayerView();
            return;
        }

        //1画面で起動
        //播放一个视频
        initSinglePlayerView();
    }



    /**
     * 1画面時の表示面の初期化
     *  播放一个视频
     *  注意使用的是PlayerFragment！！！！
     */
    private void initSinglePlayerView() {
        setContentView(R.layout.activity_single_player);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        String key = "player" + Integer.toString(0);

        //PlayerBaseActivityにPlayerFragmentを追加
        if (manager.findFragmentByTag(key) == null) {
            transaction.add(R.id.SinglePlayerContainer, mPlayerFirstFragmentList.get(0), key);
            transaction.commit();
        }
    }

    /**
     * 2画面時の表示面の初期化
     */
    private void initMultiPlayerView()
    {
        setContentView(R.layout.activity_multi_player);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        String key0 = "player" + Integer.toString(0);
        String key1 = "player" + Integer.toString(1);

        //PlayerBaseActivityにPlayerFragmentを追加
        if (manager.findFragmentByTag(key0) == null) {
            transaction.add(R.id.MultiPlayerContainer1, mPlayerFirstFragmentList.get(0), key0);
        }
        if (manager.findFragmentByTag(key1) == null) {
            transaction.add(R.id.MultiPlayerContainer2, mPlayerFirstFragmentList.get(1), key1);
        }
        transaction.commit();

        //同期ボタンの生成とボタンの内容を記述
        mSyncBtn = (ToggleButton)findViewById((R.id.MultiPlayerSyncButton));
        mSyncBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //同期処理
                    mMultiPlayerController.createSyncGroup(getSyncPlayerInfoList());
                } else {
                    //同期解除処理
                    mMultiPlayerController.createSyncGroup(getNonSyncPlayerInfoList());
                }
            }
        });
    }

    /**
     * PlayerFragmentの初期化を行う
     * 　全ての動画のファイルオープンとTimeManagerの初期設定を行う
     *
     * 　PlayerFragmentのファイルオープンの完了はPlayerBaseActivityのonCreateとは非同期であるため
     * 　別スレッドにて行っている
     *
     *   利用PlayerFragment播放视频,每个PlayerFragment与一个路径关联起来
     */
    private void initPlayerFragments() {

        //別スレッド生成
        Thread openthread = new Thread(new Runnable() {

            @Override
            public void run() {

                /**
                 * ①各PlayerFragmentのファイルオープン
                 * ②タイムマネージャの設定
                 */

                //①各PlayerFragmentのファイルオープン
                for (int i = 0; i < mVideoPathsArrayList.size(); i++) {
                    PlayerFirstFragment player = mPlayerFirstFragmentList.get(i);
                    String videoPath = mVideoPathsArrayList.get(player.getPlayerId());


                    //ファイルが開けるまでループ
                    while (true) {
                        int status = player.openFile(videoPath);

                        if(status == 0) {
                            //Log.d(TAG,"ファイルオープン成功");
                            break;
                        }else if(status == -1) {
                            //Log.d(TAG,"描画ライブラリ準備完了待ち");
                            //何もしない
                        }else{
                            //Log.d(TAG,"ファイルオープン失敗");
                            return;
                        }

                        try {
                            Thread.sleep(5);//5ms待ち
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }

                //②タイムマネージャの設定
                initTimeManager();
            }
        });

        //スレッド開始
        openthread.start();
    }

    /**
     * タイムマネージャーの設定
     * 设置时间管理
     */
    private void initTimeManager() {

        //一画面の場合
        if (mPlayerFirstFragmentList.size() == 1) {

            PlayerFirstFragment player = mPlayerFirstFragmentList.get(0);

            //PlayerInfoの生成
            //videoInfoの取得はPlayerFragmentのファイルオープン後でないとできないことに注意
            VideoInfo videoInfo = player.getVideoInfo();
            PlayerInfo playerInfo = new PlayerInfo(player.getPlayerId(), player.getPresentationTimeUs(), videoInfo.getCaptureRate(), videoInfo.getDurationUs());

            if (mSinglePlayerController == null)
                mSinglePlayerController = new SinglePlayerController();

            mSinglePlayerController.createTimeManager(playerInfo);
            player.setTimeManager(mSinglePlayerController.getTimeManager());

        } else {//複数画面の場合

            if (mMultiPlayerController == null) {
                mMultiPlayerController = new MultiPlayerController(mContext);
            }

            //同期状態の設定
            if (mSyncBtn.isChecked()) {
                //非同期設定
                mMultiPlayerController.createSyncGroup(getSyncPlayerInfoList());
            } else {
                //同期解除設定
                mMultiPlayerController.createSyncGroup(getNonSyncPlayerInfoList());
            }

            for (int i = 0; i < mPlayerFirstFragmentList.size(); i++) {

                PlayerFirstFragment player = mPlayerFirstFragmentList.get(i);

                player.setTimeManager(mMultiPlayerController.getTimeManager());
                player.setInstructionSyncController(mMultiPlayerController.getInstructionSyncController());
            }
        }
    }

    /**
     * タイムマネージャの解放
     *  タイムマネージャを利用しない場合、必ず必要
     */
    private void releaseTimeManager(){

        if (mPlayerFirstFragmentList.size() == 1) {
            if(mSinglePlayerController != null)
                mSinglePlayerController.finish();
        } else {
            if(mMultiPlayerController != null)
                mMultiPlayerController.finish();
        }
    }

    /**
     * 非同期時のプレイヤー情報配列作成関数
     * @return 非同期時のプレイヤー情報配列
     */
    private ArrayList<ArrayList<PlayerInfo>> getNonSyncPlayerInfoList(){

        ArrayList<ArrayList<PlayerInfo>> syncGroupList = new ArrayList<ArrayList<PlayerInfo>>();

        for(int i=0; i< mPlayerFirstFragmentList.size();i++){
            ArrayList<PlayerInfo> syncGroup = new ArrayList<PlayerInfo>();
            PlayerFirstFragment player = mPlayerFirstFragmentList.get(i);
            VideoInfo videoInfo = player.getVideoInfo();
            if(videoInfo != null){
                PlayerInfo playerInfo = new PlayerInfo(player.getPlayerId(), player.getPresentationTimeUs(), videoInfo.getCaptureRate(),videoInfo.getDurationUs());
                syncGroup.add(playerInfo);
                syncGroupList.add(syncGroup);
            }
        }
        return syncGroupList;
    }

    /**
     * 同期時のプレイヤー情報配列作成関数
     * @return 同期時のプレイヤー情報配列
     */
    private ArrayList<ArrayList<PlayerInfo>> getSyncPlayerInfoList() {

        ArrayList<ArrayList<PlayerInfo>> syncGroupList = new ArrayList<ArrayList<PlayerInfo>>();
        ArrayList<PlayerInfo> syncGroup = new ArrayList<PlayerInfo>();

        for(int i=0; i< mPlayerFirstFragmentList.size();i++){
            PlayerFirstFragment player = mPlayerFirstFragmentList.get(i);
            VideoInfo videoInfo = player.getVideoInfo();
            if(videoInfo != null){
                PlayerInfo playerInfo = new PlayerInfo(player.getPlayerId(), player.getPresentationTimeUs(), videoInfo.getCaptureRate(),videoInfo.getDurationUs());
                syncGroup.add(playerInfo);
            }
        }
        syncGroupList.add(syncGroup);
        return syncGroupList;
    }

}