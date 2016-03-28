package com.windward.www.casio_golf_viewer.casio.golf.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.activity.CutVideoActivity;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.VideoViewPagerAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.player.EffectGlLayer;
import com.windward.www.casio_golf_viewer.casio.golf.player.GlLayer;
import com.windward.www.casio_golf_viewer.casio.golf.player.InstructionSyncController;
import com.windward.www.casio_golf_viewer.casio.golf.player.TimeManager;
import com.windward.www.casio_golf_viewer.casio.golf.player.TouchController;
import com.windward.www.casio_golf_viewer.casio.golf.player.VideoController;
import com.windward.www.casio_golf_viewer.casio.golf.player.VideoFrame;
import com.windward.www.casio_golf_viewer.casio.golf.player.VideoGlLayer;
import com.windward.www.casio_golf_viewer.casio.golf.player.VideoInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kato-hy on 2015/12/07.
 * 在GlLayer.java中修改背景色
 *  // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
 *  GLES20.glClearColor(231, 231, 239, 255);
 *  一共有两处需要修改
 */
public class PlayerFirstFragment extends Fragment {

    private double playerRate=1;
    public static final int STATUS_PAUSE = 0;                                       // 再生状態(一時停止)
    public static final int STATUS_PLAY = 1;                                        // 再生状態(再生)
    public static final int STATUS_REALTIME_PLAY = 2;                               // 再生状態(実速度再生)

    private int mPlayStatus = STATUS_PAUSE;                                         // 現在の再生状態
    private int mBeforeTouchPlayStatus = STATUS_PAUSE;                              // タッチ操作前の再生状態
    private int mBeforeSeekPlayStatus = STATUS_PAUSE;                               // シークバー操作前の再生状態

    private int mInstructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY; // 操作タイプ

    private int mPlayerId;                                                          // プレイヤーID

    private TimeManager mTimeManager;                                               // タイムマネージャクラスのインスタンス
    private InstructionSyncController mInstructionSyncController;                   // 操作同期マネージャクラスのインスタンス
    private VideoController mVideoController;                                       // デコードコントローラのインスタンス
    private VideoInfo mVideoInfo;                                                   // デコードする動画情報クラスのインスタンス

    //描画関連
    private VideoGlLayer mVideoGlLayer;                                             // 動画表示用のGlLayer クラスのインスタンス
    private EffectGlLayer mEffectGlLayer;                                           // エフェクト表示用のGlLayerクラスのインスタンス
    private boolean mEffectShow = true;                                             // エフェクト表示フラグ

    //タッチイベント関連
    private TouchController mTouchController;                                       //タッチコントローラクラスのインスタンス

    //タイマー
    private Timer mCheckTimer;                                                      // 定期的に表示内容の更新を行うタイマー
    private int CHECK_INTERVAL = 1000/60;                                           // チェックインターバル[ms]:60fps

    //動画パラメータ
    private double mLastPlayRate;                                                   // 前回の再生レイト(再生時に参照)
    private long mPresentationTimeUs = -1;                                          // 動画の再生時間[us]
    private long mLastPresentationTimeUs = -1;                                      // 前回の表示時間[us]
    private long mLastPresentationTimeUsFromTimeManager = -1;                       // 前回TimeManagerから取得した時間[us]

    //動画デコード結果
    private int[] mEffectFrameIdArray = {                                           // エフェクト画像の配列
            R.raw.frame00,R.raw.frame01,R.raw.frame02,R.raw.frame03,R.raw.frame04,R.raw.frame05,
            R.raw.frame06,R.raw.frame07,R.raw.frame08, R.raw.frame09,R.raw.frame10,
            R.raw.frame11,R.raw.frame12,R.raw.frame13,R.raw.frame14,R.raw.frame15,
            R.raw.frame16, R.raw.frame17,R.raw.frame18,R.raw.frame19,R.raw.frame20,
            R.raw.frame21,R.raw.frame22,R.raw.frame23,R.raw.frame24,R.raw.frame25,
            R.raw.frame26, R.raw.frame27,R.raw.frame28,R.raw.frame29,R.raw.frame30,
            R.raw.frame31,R.raw.frame32,R.raw.frame33,R.raw.frame34,R.raw.frame35,
            R.raw.frame36, R.raw.frame37,R.raw.frame38,R.raw.frame39,R.raw.frame40,
            R.raw.frame41, R.raw.frame42,
    };

    private boolean mOperationViewHidden = false;                                   // 操作UIViewの表示非表示フラグ
    private boolean mSeekBarUserControl = false;                                    // シークバー操作フラグ

    //UI
    private GLSurfaceView mPlayerView;                                              // 動画表示面のGLSurfaceView
    private GLSurfaceView mEffectView;                                              // エフェクト表示面のGLSurfaceView
    private RelativeLayout mMainTopOperationLayout = null;                          // 上部の操作用レイヤーのインスタンス
    private RelativeLayout mMainBottomOperationLayout = null;                       // 下部の操作用レイヤーのインスタンス
    private RelativeLayout mMainOperationControlButtonLayout = null;                // UI表示非表示レイヤーのインスタンス
    private ImageButton mPlayPauseBtn = null;                                       // 再生・一時停止ボタンのインスタンス
    private ImageButton mRealTimePlayBtn = null;                                    // 実速度再生ボタンのインスタンス
    private ImageButton mBackTopBtn = null;                                         // トップへ戻るボタンのインスタンス
    private SeekBar mVideoSeekBar = null;                                           // シークバーのインスタンス
    private TextView mTimeText = null;                                              // 再生時刻のラベルのインスタンス
    private ImageButton mShowHideBtn = null;                                        // 操作用レイヤーの表示非表示切り替えボタン
    private Button mRotationBtn = null;                                             // 回転切り替えボタン
    private Button mReversalLeftRightBtn = null;                                    // 左右反転切り替えボタン
    private Button mReversalUpDownBtn = null;                                 // 上下反転切り替えボタン
    private ToggleButton mTouchModeChangeBtn = null;                                      // タッチ操作縦横転切り替えボタン

    private Context mContext;                                                       // Fragmentを生成したViewのコンテキスト
    private Handler mUiThreadHandler;                                               // UIのスレッド

    private BroadcastReceiver mSetInstructionTypeReceiver = null;                   // 他プレイヤーからの操作に関するIntentを受信するBroadcastReceiver
    private BroadcastReceiver mSetReversalTypeReceiver = null;                      // 他プレイヤーからの反転に関するIntentを受信するBroadcastReceiver
    private BroadcastReceiver mSetRotationAngleReceiver = null;                     // 他プレイヤーからの回転に関するIntentを受信するBroadcastReceiver
    private BroadcastReceiver mSetTranslationReceiver = null;                       // 他プレイヤーからの画像移動に関するIntentを受信するBroadcastReceiver
    private BroadcastReceiver mSetZoomLevelReceiver = null;                         // 他プレイヤーからの拡大率に関するIntentを受信するBroadcastReceiver
    private final boolean IS_USE_SURFACE = true;                                    // true:decoderが画像を直接Surfaceに書き込む方式を使用　false:デコード画像をVideoFrameから取得描画ライブラリに渡す方式を使用
    private boolean mIsPreparedVideoGlLayer = false;                                // VideoGlLayerが準備完了済みかのフラグ
    private Intent mIntent;
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mOperateRelativeLayout;
    private ImageView mEditImageView;
    private AlertDialog mOperateDialog;
    private AlertDialog mRevolutionDialog;
    private Dialog mVideoInfoDialog;
    private Dialog mGuideDialog;
    private  Dialog mTipsDialog;
    private ViewPager mViewPager;
    private VideoViewPagerAdapter mViewPagerAdapter;
    private ImageView[] dotImageViews;
    private PageChangeListenerImpl mPageChangeListenerImpl;
    private LinearLayout mDotsLinearLayout;
    private ClickListenerImpl mClickListenerImpl;
    private ImageView mPlayImageView;
    private ImageView mStartAndPauseImageView;
    private ImageView mToStartImageView;
    private ImageView mRateImageView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        mPlayerView = (GLSurfaceView)view.findViewById(R.id.PlayerGLSurfaceView);

        //隐藏EffectGLSurfaceView即mEffectView
        mEffectView = (GLSurfaceView)view.findViewById(R.id.EffectGLSurfaceView);
        setEffectGlLayerHidden(true);

        mContext =view.getContext();

        //プレイヤーIDの取得
        if (getArguments() != null && getArguments().containsKey("key_PlayerId")) {
            mPlayerId = getArguments().getInt("key_PlayerId");
        }

        mUiThreadHandler = new Handler();

        initValue();
        initGlLayer();
        initView(view);
        initTouch();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //ファイル終了
        closeFile();

        //操作同期受信解除
        releaseInstructionSyncController();
    }

    @Override
    public void onResume() {
        super.onResume();

        //描画更新タイマー開始
        startCheckTimer();
    }


    @Override
    public void onPause() {
        super.onPause();

        //Homeボタンを押された際には停止
        playPauseMethod(false);

        //描画更新タイマー停止
        stopCheckTimer();
    }


    /**
     * 変数の初期化関数
     */
    private void initValue(){
        mPlayStatus = STATUS_PAUSE;
        setUiEnable(true);                                     // UI操作可能
    }

    /**
     * GLLayerに関する初期化関数
     */
    private void initGlLayer()
    {

        //エフェクトレイヤーの生成
        mEffectGlLayer = new EffectGlLayer(mEffectView);

        /**
         * 描画レイヤーの生成
         * 　デコーダでデコーダでSurfaceを使用するかしないかで分岐を使用するかしないかで分岐
         * 　(I)Surfaceを使用する場合
         * 　　　 GLSurfaceViewを設定し、onSurfaceCreatedで生成されたSurfaceをVideoControllerに設定する
         * 　(II)Surfaceを使用しない場合
         * 　　　 VideoControllerにSurfaceを設定する必要なし
         */
        mIsPreparedVideoGlLayer = false;
        if(IS_USE_SURFACE) {
            mVideoGlLayer = new VideoGlLayer(mPlayerView, IS_USE_SURFACE, new GlLayer.GlLayerCallback() {

                @Override
                public void onSurfaceCreated(Surface surface) {
                    mVideoController = new VideoController(surface);   // デコードコントローラの初期化
                    synchronized (this) {
                        mIsPreparedVideoGlLayer = true;
                    }
                }
            });
        }else{
            mVideoGlLayer = new VideoGlLayer(mPlayerView, IS_USE_SURFACE, null);
            mVideoController = new VideoController(null);                   // デコードコントローラの初期化
            mIsPreparedVideoGlLayer = true;
        }

        //エフェクトレイヤーにエフェクトを設定
        Bitmap frameImg =  checkFrameImage(0);
        if (frameImg != null) {
            mEffectGlLayer.setUIImage(frameImg);
        }
    }








    /**
     * 表示関係のUIに関しての初期化関数
     * @param view
     */
    private void initView(View view){
        mClickListenerImpl=new ClickListenerImpl();
        mBackRelativeLayout=(RelativeLayout)getActivity().findViewById(R.id.backRelativeLayout);
        mBackRelativeLayout.setOnClickListener(mClickListenerImpl);
        mEditImageView=(ImageView)getActivity().findViewById(R.id.editImageView);
        mEditImageView.setOnClickListener(mClickListenerImpl);
        mOperateRelativeLayout =(RelativeLayout)getActivity().findViewById(R.id.operateRelativeLayout);
        mOperateRelativeLayout.setOnClickListener(mClickListenerImpl);
        mPlayImageView=(ImageView)getActivity().findViewById(R.id.playImageView);
        mPlayImageView.setOnClickListener(mClickListenerImpl);

        mStartAndPauseImageView=(ImageView)getActivity().findViewById(R.id.controller_startAndPause_ImageView);
        mStartAndPauseImageView.setOnClickListener(mClickListenerImpl);

        mToStartImageView=(ImageView)getActivity().findViewById(R.id.controller_to_start_ImageView);
        mToStartImageView.setOnClickListener(mClickListenerImpl);

        mRateImageView=(ImageView)getActivity().findViewById(R.id.controller_rate_ImageView);
        mRateImageView.setOnClickListener(mClickListenerImpl);



        //操作バー全体
        mMainBottomOperationLayout = (RelativeLayout)view.findViewById(R.id.MainBottomOperationLayout);
        mMainTopOperationLayout = (RelativeLayout)view.findViewById(R.id.MainTopOperationLayout);
        mMainOperationControlButtonLayout = (RelativeLayout)view.findViewById(R.id.MainOperationControlButtonLayout);

//        /**
//         * OperationLayoutの表示非表示ボタンに対するActionイベント
//         */
//        mOperationViewHidden = false;
//        mShowHideBtn = (ImageButton)view.findViewById(R.id.ShowHideButton);
//        mShowHideBtn.setImageResource(R.drawable.hidden_btn);
//        mShowHideBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//
//                        if(mOperationViewHidden){
//                            mShowHideBtn.setImageResource(R.drawable.hidden_btn);
//                            mMainBottomOperationLayout.setVisibility(View.VISIBLE);
//                        }else{
//                            mShowHideBtn.setImageResource(R.drawable.show_btn);
//                            mMainBottomOperationLayout.setVisibility(View.INVISIBLE);
//                        }
//                        mOperationViewHidden = !mOperationViewHidden;
//                    }
//                });
//
//
//        /**
//         * 回転切り替えボタンに対するActionイベント
//         * ボタンを押すごとに0->90->180->270->0->...のトグル動作
//         */
//        mRotationBtn = (Button)view.findViewById(R.id.RotationButton);
//        mRotationBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//
//                        //動画像の現在の回転量の取得
//                        float nowRotation = mVideoGlLayer.getRotateAngle();
//
//                        if (nowRotation == GlLayer.ROTATE_ANGLE_0) {
//                            setRotateAngle(GlLayer.ROTATE_ANGLE_90);
//                        } else if (nowRotation == GlLayer.ROTATE_ANGLE_90) {
//                            setRotateAngle(GlLayer.ROTATE_ANGLE_180);
//                        } else if (nowRotation == GlLayer.ROTATE_ANGLE_180) {
//                            setRotateAngle(GlLayer.ROTATE_ANGLE_270);
//                        } else if (nowRotation == GlLayer.ROTATE_ANGLE_270) {
//                            setRotateAngle(GlLayer.ROTATE_ANGLE_0);
//                        } else {
//                            setRotateAngle(GlLayer.ROTATE_ANGLE_0);
//                        }
//                    }
//                });
//
//        /**
//         * 左右反転タイプ切り替えボタンに対するActionイベント
//         */
//        mReversalLeftRightBtn = (Button)view.findViewById(R.id.ReversalLeftRightButton);
//        mReversalLeftRightBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//
//                        //動画像の現在の反転状態取得
//                        float nowReversalType = mVideoGlLayer.getReversalType();
//
//                        if (nowReversalType == GlLayer.REVERSAL_DEFAULT) {
//                            setReversalType(GlLayer.REVERSAL_LEFTRIGHT);
//                        } else if (nowReversalType == GlLayer.REVERSAL_LEFTRIGHT) {
//                            setReversalType(GlLayer.REVERSAL_DEFAULT);
//                        } else {
//                            setReversalType(GlLayer.REVERSAL_LEFTRIGHT);
//                        }
//                        checkReversalBtn();
//                    }
//                });
//

//        /**
//         * 上下反転タイプ切り替えボタンに対するActionイベント
//         */
//        mReversalUpDownBtn = (Button)view.findViewById(R.id.ReversalUpDownButton);
//        mReversalUpDownBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//
//                        float nowReversalType = mVideoGlLayer.getReversalType();
//
//                        if (nowReversalType == GlLayer.REVERSAL_DEFAULT) {
//                            setReversalType(GlLayer.REVERSAL_UPDOWN);
//                        } else if (nowReversalType == GlLayer.REVERSAL_UPDOWN) {
//                            setReversalType(GlLayer.REVERSAL_DEFAULT);
//                        } else {
//                            setReversalType(GlLayer.REVERSAL_UPDOWN);
//                        }
//                        checkReversalBtn();
//                    }
//                });
//
//        /**
//         * タッチ操作の縦横切り替えボタンに対するActionイベント
//         */
//        mTouchModeChangeBtn = (ToggleButton)view.findViewById(R.id.TouchModeChangeButton);
//        mTouchModeChangeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    if (mTouchController != null) {
//                        mTouchController.setVertical(isChecked);
//                    }
//                } else {
//                    if (mTouchController != null) {
//                        mTouchController.setVertical(isChecked);
//                    }
//                }
//
//                //エフェクトレイヤーの描画
//                if (mEffectGlLayer != null) {
//                    if (mEffectShow) {
//                        Bitmap frameImg = checkFrameImage(mPresentationTimeUs);
//                        if (frameImg != null) {
//                            //エフェクトレイヤーに画像を設定
//                            mEffectGlLayer.setUIImage(frameImg);
//                        }
//                    } else {
//                        //エフェクトレイヤーに何も表示させない
//                        mEffectGlLayer.setUIImage(null);
//                    }
//                }
//            }
//        });


//        /**
//         * 再生/一時停止ボタンに対するActionイベント
//         */
//        mPlayPauseBtn = (ImageButton)view.findViewById(R.id.MainOperationPlayPauseButton);
//        mPlayPauseBtn.setImageResource(R.drawable.play_button);
//        mPlayPauseBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//                        switch(mPlayStatus){
//                            case STATUS_PAUSE:
//                                playPauseMethod(true);
//                                break;
//                            case STATUS_PLAY:
//                                playPauseMethod(false);
//                                break;
//                            case STATUS_REALTIME_PLAY:
//                                playPauseMethod(false);
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//
//        /**
//         * 実速再生ボタンに対するActionイベント
//         */
//        mRealTimePlayBtn = (ImageButton)view.findViewById(R.id.MainOperationRealTimePlayButton);
//        mRealTimePlayBtn.setImageResource(R.drawable.realtime_button);
//        mRealTimePlayBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//
//                        mPlayStatus = STATUS_PLAY;
//                        if (mTimeManager != null) {
//
//                            //実速度＝1倍速を設定
//                            double realTimeMoviePlayRate = 1;
//                            mTimeManager.setPlayRateFromPlayer(mPlayerId, realTimeMoviePlayRate);
//
//                            //他プレイヤーに再生状態を通知し状態の同期を図る
//                            if (mInstructionSyncController != null) {
//                                int instructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY;
//                                mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
//                                mInstructionType = instructionType;
//                            }
//                        }
//                    }
//                });
//
//
//        /**
//         * 先頭時刻へ戻るボタンに対するActionイベント
//         */
//        mBackTopBtn = (ImageButton)view.findViewById(R.id.MainOperationBackTopButton);
//        mBackTopBtn.setImageResource(R.drawable.back_top_button);
//        mBackTopBtn.setEnabled(true);
//        mBackTopBtn.setOnClickListener(
//                new Button.OnClickListener() {
//                    public void onClick(View w) {
//                        if (mTimeManager != null) {
//                            // 再生を停止、再生時刻に0にシーク
//                            mTimeManager.setPlayRateFromPlayer(mPlayerId, 0);
//                            mTimeManager.setCurrentPtsUsFromPlayer(mPlayerId, 0);
//                        }
//                    }
//                });


        /**
         * シークバーに対するActionイベント
         */
        //mVideoSeekBar=(SeekBar)view.findViewById(R.id.seekBar);//MainOperationSeekBar
        mVideoSeekBar=(SeekBar)getActivity().findViewById(R.id.seekBar);//MainOperationSeekBar
        mVideoSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener(){

                    // ツマミに触れたときに呼ばれる
                    public void onStartTrackingTouch(SeekBar aSeekBar) {
                        mBeforeSeekPlayStatus = mPlayStatus;
                        playPauseMethod(false);
                        mSeekBarUserControl = true;
                    }

                    // ツマミをドラッグしたときに呼ばれる
                    public void onProgressChanged(SeekBar aSeekBar, int aProgress,
                                                  boolean aFromUser) {
                        long presentationTimeUs = (long) ((double) mVideoInfo.getDurationUs() * ((double) aProgress / (double) aSeekBar.getMax()));
                        if (aFromUser) {

                            if(mSeekBarUserControl){
                                if (mTimeManager != null) {
                                    mTimeManager.setCurrentPtsUsFromPlayer(mPlayerId, presentationTimeUs);

                                    //他プレイヤーにシークバー操作状態を通知し状態の同期を図る
                                    if (mInstructionSyncController != null) {
                                        int instructionType = InstructionSyncController.INSTRUCTION_TYPE_SEEKBAR;
                                        mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                                        mInstructionType = instructionType;
                                    }
                                }
                            }
                        }
                        //updateTimeUi(presentationTimeUs);
                    }

                    // ツマミを離したときに呼ばれる
                    public void onStopTrackingTouch(SeekBar aSeekBar) {
                        mSeekBarUserControl = false;
                        if (mTimeManager != null) {
                            mTimeManager.setCurrentPtsUsFromPlayer(mPlayerId, mPresentationTimeUs);
                            //他プレイヤーにシークバー操作状態を通知し状態の同期を図る
                            if (mInstructionSyncController != null) {
                                int instructionType = InstructionSyncController.INSTRUCTION_TYPE_SEEKBAR;
                                mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                                mInstructionType = instructionType;
                            }
                        }

                        //シークバー操作前の再生状態へ戻す処理
                        if (mBeforeSeekPlayStatus == STATUS_PLAY) {
                            playPauseMethod(true);
                        } else {
                            playPauseMethod(false);
                        }
                    }
                });


        //mTimeText = (TextView)view.findViewById(R.id.timeTextView);//MainOperationTimeText
        mTimeText = (TextView)getActivity().findViewById(R.id.timeTextView);//MainOperationTimeText
    }


    /**
     * タッチ関係の初期化関数
     */
    private void initTouch(){

        /**
         * TouchControllerライブラリの生成
         * 生成時にTouchController.OnTouchControllerListenerを実装したものを引数にセット
         */
        mTouchController = new TouchController(mContext,mPlayerView, new TouchController.OnTouchControllerListener() {

            /**
             * タッチ開始時に呼ばれる
             * 　ベースアプリでは停止操作
             */
            @Override
            public void onTouchBegan() {
                mBeforeTouchPlayStatus = mPlayStatus;
                playPauseMethod(false);
            }

            /**
             * シングルタップ時に呼ばれる
             * 　ベースアプリでは再生、停止のトグル操作
             */
            @Override
            public void onSingleTap() {
                switch(mBeforeTouchPlayStatus){
                    case STATUS_PAUSE:
                        playPauseMethod(true);
                        break;
                    case STATUS_PLAY:
                        playPauseMethod(false);
                        break;
                }
            }

            /**
             * ダブルタップ時に呼ばれる
             * 　ベースアプリでは実速度再生
             */
            @Override
            public void onDoubleTap() {
                mPlayStatus = STATUS_PLAY;
                if (mTimeManager != null) {

                    //実速度＝1倍速を設定
                    double realTimeMoviePlayRate = 1;
                    mTimeManager.setPlayRateFromPlayer(mPlayerId, realTimeMoviePlayRate);
                    mPlayStatus = STATUS_REALTIME_PLAY;

                    //他プレイヤーに再生状態を通知し状態の同期を図る
                    if (mInstructionSyncController != null) {
                        int instructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY;
                        mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                        mInstructionType = instructionType;
                    }
                }
            }

            /**
             *　1本指スワイプ時に呼ばれる
             * 　ベースアプリではコマ送り操作
             * @param status　指の状態
             * @param diffTimeUs　進める再生時間(差分)
             */
            @Override
            public void onOneFingerSwipe(int status, long diffTimeUs) {
                // タッチ状態の判断(記述のみ未使用)
                switch (status) {
                    case TouchController.STATUS_DOWN:
                        break;
                    case TouchController.STATUS_MOVE:
                        break;
                    case TouchController.STATUS_UP:
                        break;
                    case TouchController.STATUS_CANCEL:
                        break;
                    default :
                        break;
                }

                /**
                 * TimeManagerによる現在の再生時刻の取得
                 * 　(TimeManagerが動画の再生時刻に関して管理しているので
                 * 　　再生時刻の更新や取得は必ずTimeManagerで行う)
                 */
                if (mTimeManager != null) {
                    long presentationTimeUs = mTimeManager.getPresentationTimeUsFromPlayer(mPlayerId);
                    if(presentationTimeUs != TimeManager.ERROR_NOTFOUND_PTS){
                        if(mVideoInfo != null) {
                            if (presentationTimeUs < 0) {

                                //取得した再生時刻が動画の0より前の場合
                                presentationTimeUs = 0;
                            }else if(presentationTimeUs > mVideoInfo.getDurationUs()){

                                //取得した再生時刻が動画の最大再生時刻よりも後ろの場合
                                presentationTimeUs = mVideoInfo.getDurationUs();
                            }else{

                                //エラーなく正常に取得できた場合そのまま利用
                            }
                        }
                    }else{
                        //PlayerIdが見つからなかった場合
                        presentationTimeUs = mLastPresentationTimeUs;
                    }

                    //更新時に指定する再生時刻の計算 　(※更新する再生時刻=取得した現在再生時刻+差分時間)
                    long newPresentationTimeUs = presentationTimeUs + diffTimeUs;

                    //TimeManagerに再生時刻の更新
                    mTimeManager.setCurrentPtsUsFromPlayer(mPlayerId, newPresentationTimeUs);

                    //他プレイヤーにスワイプ操作状態を通知し状態の同期を図る
                    if (mInstructionSyncController != null) {
                        mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, InstructionSyncController.INSTRUCTION_TYPE_SWIPE);
                    }
                }
            }

            /**
             * フリック時に呼ばれる
             * 　ベースアプリでは任意速度再生
             * @param playRate　指の速度
             */
            @Override
            public void onFlick(double playRate) {

                //再生速度が変更したことをTimeManagerにセット
                if (mTimeManager != null) {
                    mTimeManager.setPlayRateFromPlayer(mPlayerId, playRate);
                }

                //他プレイヤーに再生状態を通知し状態同期を図る
                if (playRate != 0) {
                    if (mInstructionSyncController != null) {
                        int instructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY;
                        mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                        mInstructionType = instructionType;
                    }
                }
            }

            /**
             * ピンチ操作時に呼ばれる
             * 　ベースアプリでは拡大操作
             * @param center　中心位置
             * @param diffZoomLevel　ズームレベル(差分)
             */
            @Override
            public void onZoom(PointF center, float diffZoomLevel) {
                setZoomLevel(center, diffZoomLevel);
            }

            /**
             * 2本指スワイプ時に呼ばれる
             * 　ベースアプリでは拡大画像の移動操作
             * @param status　指の状態
             * @param diffMovementX　X方向の移動距離(差分)
             * @param diffMovementY　Y方向の移動距離(差分)
             */
            @Override
            public void onTwoFingersSwipe(int status, float diffMovementX, float diffMovementY) {
                // タッチ状態の判断(記述のみ未使用)
                switch (status) {
                    case TouchController.STATUS_DOWN:
                        break;
                    case TouchController.STATUS_MOVE:
                        break;
                    case TouchController.STATUS_UP:
                        break;
                    case TouchController.STATUS_CANCEL:
                        break;
                    default :
                        break;
                }

                setTranslate(diffMovementX, diffMovementY);
            }
        });

        //TouchControllerの設定
        mTouchController.changeTimeScaleUs(8 * 1000);
        mTouchController.setVertical(true);

    }

    /**
     反転TYPEの設定関数
     * 描画レイヤーへの反転TYPEの設定と操作同期コントローラへの通知処理を行う
     * 自身のプレイヤーの反転TYPEの変更時に呼び出される関数
     * 操作同期コントローラからの通知では呼び出さないように注意
     - Parameter reversalType 設定する反転TYPE
     */
    private void setReversalType(int reversalType){
        if (mVideoGlLayer != null) {
            mVideoGlLayer.setReversalType(reversalType);
        }
        if (mInstructionSyncController != null) {
            mInstructionSyncController.setReversalTypeFromPlayer(mPlayerId, reversalType);
        }
    }

    /**
     * ズーム倍率加算関数
     * 描画レイヤーへのズーム倍率の差分値の設定と操作同期コントローラへの通知処理を行う
     * 自身のプレイヤーのズーム倍率の変更時に呼び出される関数
     * 操作同期コントローラからの通知では呼び出さないように注意
     * @param centerPoint 拡大する中心位置
     * @param diffZoomLevel 拡大率
     */
    private void setZoomLevel(PointF centerPoint ,float diffZoomLevel){
        if (mVideoGlLayer != null) {
            mVideoGlLayer.setZoomLevel(diffZoomLevel);
        }
        if (mInstructionSyncController != null) {
            mInstructionSyncController.setZoomLevelFromPlayer(mPlayerId, centerPoint, diffZoomLevel);
        }
    }

    /**
     * 表示位置移動設定関数
     * 描画レイヤーへの表示位置移動設量の差分値の設定と操作同期コントローラへの通知処理を行う
     * 自身のプレイヤーの表示位置移動設量の変更時に呼び出される関数
     * 操作同期コントローラからの通知では呼び出さないように注意
     * @param diffMovementX X座標の移動量
     * @param diffMovementY Y座標の移動量
     */
    private void setTranslate(float diffMovementX, float diffMovementY) {

        if (mVideoGlLayer != null) {
            mVideoGlLayer.setTranslate(diffMovementX, diffMovementY);
        }
        if (mInstructionSyncController != null){
            mInstructionSyncController.setTranslationFromPlayer(mPlayerId, diffMovementX, diffMovementY);
        }
    }

    /**
     画像回転量の設定関数
     * 描画レイヤーへの画像回転量の設定と操作同期コントローラへの通知処理を行う
     * 自身のプレイヤーの画像回転量の変更時に呼び出される関数
     * 操作同期コントローラからの通知では呼び出さないように注意
     - Parameter rotateAngle 設定する回転量(0度、90度、180度、270度の4種類)[rad]
     */
    private void setRotateAngle(float rotateAngle){
        if (mVideoGlLayer != null) {
            mVideoGlLayer.setRotateAngle(rotateAngle);
        }
        if (mInstructionSyncController != null){
            mInstructionSyncController.setRotationAngleFromPlayer(mPlayerId, rotateAngle);
        }
    }

    /**
     * このプレイヤーIDの取得
     * @return プレイヤーID
     */
    public int getPlayerId(){
        return mPlayerId;
    }


    /**
     * メインビューから呼ばれるファイルオープン関数
     * @param filePath ファイルパス
     * @return 0 成功、-1 描画ライブラリ未準備エラー、-2 ファイルオープンエラー,-3 その他
     */
    public int openFile(String filePath) {

        if(filePath != null) {

            if (!mIsPreparedVideoGlLayer) {                                                 //描画レイヤーの初期化の完了を確認
                return -1;
            }

            VideoInfo videoInfo = new VideoInfo();                                          //ファイルオープン関数呼び出し
            if (mVideoController.openFile(filePath, videoInfo) != 0){
                return -2;                                                                  //ファイルが開けない場合エラー
            }

            mVideoInfo = videoInfo;                                                         //VideoInfo取得
            if(mVideoInfo != null) {

                mVideoGlLayer.changeImageSize(videoInfo.getWidth(), videoInfo.getHeight());     //描画レイヤーに画像のサイズを設定
                //mEffectGlLayer.changeImageSize(videoInfo.getWidth(), videoInfo.getHeight());    //エフェクトレイヤーも描画レイヤーと同じ表示領域にしたい場合設定
            }

            mPresentationTimeUs = 0;                                                       //再生時刻を"0"で初期化
            mVideoSeekBar.setMax((int) videoInfo.getDurationUs());                          //シークバー初期化

            return 0;
        }
        return -3;
    }

    /**
     * ファイルクローズ関数
     * @return 0 成功, 0以外 失敗
     */
    public int closeFile() {

        int status = 0;

        if (mVideoController != null) {
            status = mVideoController.closeFile();
            mVideoController = null;
        }

        return status;
    }

    /**
     * メインビューから呼ばれるタイムマネージャクラスのインスタンス受け渡し関数
     * @param timeManager タイムマネージャクラス
     */
    public void setTimeManager(TimeManager timeManager) {
        mTimeManager = timeManager;
    }


    /**
     * VideoInfo取得関数
     * 　※動画をオープンした後に取得しなければ未設定のまま
     * @return VideoInfoクラス
     */
    public VideoInfo getVideoInfo(){
        return mVideoInfo;
    }


    /**
     * 現在の再生時間取得関数
     * @return 現在の再生時間
     */
    public long getPresentationTimeUs(){
        return mPresentationTimeUs;
    }

    /**
     * プレイヤーのボタン等のUIのEnable設定関数
     * @param uiEnable true イベント有効 false イベント無効
     */
    public void setUiEnable(boolean uiEnable) {

        //タッチイベントの解除
        if (mPlayerView != null) {
            mPlayerView.setEnabled(uiEnable);
        }

        if (mPlayPauseBtn != null) {
            mPlayPauseBtn.setEnabled(uiEnable);
        }
        if (mRealTimePlayBtn != null) {
            mRealTimePlayBtn.setEnabled(uiEnable);
        }
        if (mBackTopBtn != null) {
            mBackTopBtn.setEnabled(uiEnable);
        }
        if (mVideoSeekBar != null) {
            mVideoSeekBar.setEnabled(uiEnable);
        }
    }


    /**
     * InstructionSyncControllerの受信設定をする関数
     * 　BroadcastReceiverをセットする
     * @param instructionSyncController InstructionSyncControllerのインスタンス
     */
    public void setInstructionSyncController(InstructionSyncController instructionSyncController) {

        if (instructionSyncController != null) {
            mInstructionSyncController = instructionSyncController;

            IntentFilter intentFilter;

            intentFilter = new IntentFilter();
            intentFilter.addAction(InstructionSyncController.ACTION_INSTRUCTION_TYPE);
            mSetInstructionTypeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveSetInstructionType(intent);
                }
            };
            mContext.registerReceiver(mSetInstructionTypeReceiver, intentFilter);

            intentFilter = new IntentFilter();
            intentFilter.addAction(InstructionSyncController.ACTION_REVERSAL_TYPE);
            mSetReversalTypeReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveSetReversalType(intent);
                }
            };
            mContext.registerReceiver(mSetReversalTypeReceiver ,intentFilter);

            intentFilter = new IntentFilter();
            intentFilter.addAction(InstructionSyncController.ACTION_ROTATION_ANGLE);
            mSetRotationAngleReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveSetRotationAngle(intent);
                }
            };
            mContext.registerReceiver(mSetRotationAngleReceiver,intentFilter);

            intentFilter = new IntentFilter();
            intentFilter.addAction(InstructionSyncController.ACTION_TRANSLATION);
            mSetTranslationReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveSetTranslation(intent);
                }
            };
            mContext.registerReceiver(mSetTranslationReceiver,intentFilter);

            intentFilter = new IntentFilter();
            intentFilter.addAction(InstructionSyncController.ACTION_ZOOM_LEVEL);
            mSetZoomLevelReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveSetZoomLevel(intent);
                }
            };
            mContext.registerReceiver(mSetZoomLevelReceiver,intentFilter);
        }
    }


    /**
     * InstructionSyncControllerの受信設定を解除する関数
     * 　設定したBroadcastReceiverの解除を行う
     */
    private void releaseInstructionSyncController(){

        if(mSetInstructionTypeReceiver != null ) {
            mContext.unregisterReceiver(mSetInstructionTypeReceiver);
            mSetInstructionTypeReceiver = null;
        }
        if(mSetReversalTypeReceiver != null ){
            mContext.unregisterReceiver(mSetReversalTypeReceiver);
            mSetReversalTypeReceiver = null;
        }
        if(mSetRotationAngleReceiver != null ){
            mContext.unregisterReceiver(mSetRotationAngleReceiver);
            mSetRotationAngleReceiver = null;
        }
        if(mSetTranslationReceiver != null ){
            mContext.unregisterReceiver(mSetTranslationReceiver);
            mSetTranslationReceiver = null;
        }
        if(mSetZoomLevelReceiver != null ){
            mContext.unregisterReceiver(mSetZoomLevelReceiver);
            mSetZoomLevelReceiver = null;
        }

        mInstructionSyncController = null;
    }



    /**
     * 操作同期コントローラからのInstructionType変更通知
     * @param intent 通知内容Intent
     */
    private void onReceiveSetInstructionType( Intent intent ){
        Bundle bundle = intent.getExtras();

        ArrayList<Integer> playerInfo = bundle.getIntegerArrayList(InstructionSyncController.BCEXTRA_PLAYER_LIST);

        if(playerInfo != null){
            if(playerInfo.contains(mPlayerId)){
                int instructionType = bundle.getInt(InstructionSyncController.BCEXTRA_INSTRUCTION_TYPE);
                mInstructionType = instructionType;
            }
        }
    }

    /**
     * 操作同期コントローラからのReversalType変更通知
     * @param intent 通知内容Intent
     */
    private void onReceiveSetReversalType(Intent intent){
        Bundle bundle = intent.getExtras();

        ArrayList<Integer> playerInfo = bundle.getIntegerArrayList(InstructionSyncController.BCEXTRA_PLAYER_LIST);

        if(playerInfo != null){
            if(playerInfo.contains(mPlayerId)){
                int reversalType = bundle.getInt(InstructionSyncController.BCEXTRA_REVERSAL_TYPE);

                if(mVideoGlLayer != null){
                    mVideoGlLayer.setReversalType(reversalType);
                }
                checkReversalBtn();
            }
        }
    }

    /**
     * 操作同期コントローラからのRotationAngle変更通知
     * @param intent 通知内容Intent
     */
    private void onReceiveSetRotationAngle(Intent intent){
        Bundle bundle = intent.getExtras();

        ArrayList<Integer> playerInfo = bundle.getIntegerArrayList(InstructionSyncController.BCEXTRA_PLAYER_LIST);

        if(playerInfo != null){
            if(playerInfo.contains(mPlayerId)){
                float rotationAngle = bundle.getFloat(InstructionSyncController.BCEXTRA_ROTATION_ANGLE);
                if(mVideoGlLayer != null){
                    mVideoGlLayer.setRotateAngle(rotationAngle);
                }
            }
        }
    }

    /**
     * 操作同期コントローラからのTranslation変更通知
     * @param intent 通知内容Intent
     */
    private void onReceiveSetTranslation(Intent intent){
        Bundle bundle = intent.getExtras();

        ArrayList<Integer> playerInfo = bundle.getIntegerArrayList(InstructionSyncController.BCEXTRA_PLAYER_LIST);

        if(playerInfo != null){
            if(playerInfo.contains(mPlayerId)){
                float diffMovementX = bundle.getFloat(InstructionSyncController.BCEXTRA_DIFF_MOVEMENT_X);
                float diffMovementY = bundle.getFloat(InstructionSyncController.BCEXTRA_DIFF_MOVEMENT_Y);

                if(mVideoGlLayer != null){
                    mVideoGlLayer.setTranslate(diffMovementX, diffMovementY);
                }
            }
        }
    }

    /**
     * 操作同期コントローラからのZoomLevel変更通知
     * @param intent 通知内容Intent
     */
    private void onReceiveSetZoomLevel(Intent intent){
        Bundle bundle = intent.getExtras();

        ArrayList<Integer> playerInfo = bundle.getIntegerArrayList(InstructionSyncController.BCEXTRA_PLAYER_LIST);

        if(playerInfo != null){
            if(playerInfo.contains(mPlayerId)){

                float diffZoomLevel = bundle.getFloat(InstructionSyncController.BCEXTRA_DIFF_ZOOM_LEVEL);
                float centerPointX = bundle.getFloat(InstructionSyncController.BCEXTRA_CENTER_POINT_X);
                float centerPointY = bundle.getFloat(InstructionSyncController.BCEXTRA_CENTER_POINT_Y);

                if(mVideoGlLayer != null){
                    mVideoGlLayer.setZoomLevel(diffZoomLevel);
                }
            }
        }
   }

    /**
     * 描画更新タイマーの開始
     */
    private void startCheckTimer() {
        stopCheckTimer();

        if (mCheckTimer == null) {
            mCheckTimer = new Timer();
            mCheckTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onCheckMethod();
                }
            }, 0, CHECK_INTERVAL);

        }
    }

    /**
     * 描画更新タイマー停止関数
     */
    private void stopCheckTimer()
    {
        if(mCheckTimer != null){
            mCheckTimer.cancel();
            mCheckTimer = null;
        }
    }

    /**
     * 描画更新タイマー呼び出し関数
     * Android版のベースアプリではUIの更新と動画のデコード処理共にタイマーの呼び出しにて行っている
     * ※再生時刻の管理はTimeManagerで行っているため、必ずTimeManagerから取得した再生時刻をデコーダにセットすることに注意する
     */
    private void onCheckMethod(){
        //TimeManagerの確認処理
        if (mTimeManager != null) {
            long presentationTimeUs = mTimeManager.getPresentationTimeUsFromPlayer(mPlayerId);
            if(presentationTimeUs != TimeManager.ERROR_NOTFOUND_PTS){
                if(mVideoInfo != null) {
                    if (presentationTimeUs < 0) {

                        //取得した再生時刻が動画の0より前の場合
                        presentationTimeUs = 0;
                    }else if(presentationTimeUs > mVideoInfo.getDurationUs()){

                        //取得した再生時刻が動画の最大再生時刻よりも後ろの場合
                        presentationTimeUs = mVideoInfo.getDurationUs();
                    }else{

                        //エラーなく正常に取得できた場合そのまま利用
                    }
                }
            }else{
                //PlayerIdが見つからなかった場合
                presentationTimeUs = mLastPresentationTimeUs;
            }
            double moviePlayRate = mTimeManager.getPlayRateFromPlayer(mPlayerId);

            //デコード依頼
            if (mLastPresentationTimeUsFromTimeManager != presentationTimeUs) {
                decodeRequest(presentationTimeUs, moviePlayRate);
                mLastPresentationTimeUsFromTimeManager = presentationTimeUs;
            }

            //再生速度の確認
            checkMoviePlayRate(moviePlayRate);

            //UIの更新
            updateAllUi();
        }
    }


    /**
     * デコードコントローラへ現在時間を渡す関数
     *
     * 　ベースアプリでは操作状態に応じてデコードの仕方を変えている
     * 　シークバー操作時　　　　　　：キーフレームをデコード
     * 　再生状態時、1本指スワイプ時：通常デコード
     *
     * @param presentationTimeUs 描画する動画の再生時刻[us]
     * @param moviePlayRate 動画の再生速度倍率
     */
    private void decodeRequest(long presentationTimeUs, double moviePlayRate) {

        if (mVideoInfo != null) {
            if (presentationTimeUs >= 0 && presentationTimeUs <= mVideoInfo.getDurationUs()) {
                if(mVideoController != null) {

                    VideoFrame videoFrame;

                    /**
                     * ユーザーの操作によってデコード画像呼び出し方法を変更
                     *
                     *  ① シークバー操作時　など大まかにシークしたい場合　
                     *  　　　　　　　　　　-> mVideoController.getVideoKeyFrame();//指定時間に近いキーフレームを取得
                     *  ② 1本指スワイプ操作時、プレイヤーボタン操作時、デフォルト時　細かくシークしたい場合
                     *                    -> mVideoController.getVideoFrame();//指定時間のフレームを取得
                     *  ※ただし、現状getVideoKeyFrameは未実装なのでどちらを呼んでも同じ
                     */

                    switch (mInstructionType) {
                        case InstructionSyncController.INSTRUCTION_TYPE_SEEKBAR:

                            videoFrame = mVideoController.getVideoKeyFrame(presentationTimeUs, moviePlayRate);
                            if(videoFrame != null) {
                                drawUpdate(videoFrame.getImage(), videoFrame.getPresentationTimeUs());
                                mPresentationTimeUs = videoFrame.getPresentationTimeUs();
                            }
                            break;
                        case InstructionSyncController.INSTRUCTION_TYPE_SWIPE:

                            videoFrame = mVideoController.getVideoFrame(presentationTimeUs, moviePlayRate);
                            if(videoFrame != null) {
                                drawUpdate(videoFrame.getImage(), videoFrame.getPresentationTimeUs());
                                mPresentationTimeUs = videoFrame.getPresentationTimeUs();
                            }
                            break;
                        case InstructionSyncController.INSTRUCTION_TYPE_PLAY:

                            videoFrame = mVideoController.getVideoFrame(presentationTimeUs, moviePlayRate);
                            if(videoFrame != null) {
                                drawUpdate(videoFrame.getImage(), videoFrame.getPresentationTimeUs());
                                mPresentationTimeUs = videoFrame.getPresentationTimeUs();
                            }
                            break;
                        default :
                            videoFrame = mVideoController.getVideoFrame(presentationTimeUs, moviePlayRate);
                            if(videoFrame != null) {
                                drawUpdate(videoFrame.getImage(), videoFrame.getPresentationTimeUs());
                                mPresentationTimeUs = videoFrame.getPresentationTimeUs();
                            }
                            break;
                    }
                }
            } else if (presentationTimeUs < 0) {
                //表示できません
            } else if (presentationTimeUs > mVideoInfo.getDurationUs()){
                //表示できません
            }
        }
    }

    /**
     * 動画画像とエフェクト画像の描画を更新
     * @param buffer
     * @param presentationTimeUs
     */
    private void drawUpdate(ByteBuffer buffer, long presentationTimeUs) {

        /**
         * 描画レイヤーへの動画像の描画
         * 　デコーダでデコーダでSurfaceを使用するかしないかで分岐を使用するかしないかで分岐
         * 　(I)Surfaceを使用する場合
         * 　　　 VideoControllerのgetVideoFrameやgetVideoKeyFrameでSurfaceに直接描画しているため何もしない
         * 　(II)Surfaceを使用しない場合
         * 　　　 getVideoFrameやgetVideoKeyFrameで取得した画像データを描画レイヤーに設定する
         */
        if(IS_USE_SURFACE){
                //(I)デコーダが直接Surfaceに書く場合は何もしなくてよい。
        }else{
            if (mVideoGlLayer != null) {
                //(II)描画レイヤーに描画依頼

                //mVideoGlLayer.setDrawImage(convertByteBufferToBitmap(buffer));
            }
        }

        //エフェクトレイヤーの描画
        if (mEffectGlLayer != null) {
            if (mEffectShow) {
                Bitmap frameImg =  checkFrameImage(presentationTimeUs);
                if (frameImg != null) {
                    //エフェクトレイヤーに画像を設定
                    mEffectGlLayer.setUIImage(frameImg);
                }
            } else {
                //エフェクトレイヤーに何も表示させない
                mEffectGlLayer.setUIImage(null);
            }
        }
    }

    /**
     * エフェクト画像配列から該当のエフェクト画像を取得する関数
     * @param presentationTimeUs 現在時刻[us]
     * @return 該当のエフェクト画像
     */
    private Bitmap checkFrameImage(long presentationTimeUs) {
        int number;
        Bitmap frameImg = null;

        if(mTouchController != null){
            if(mTouchController.isVertical()){
                number = (int)(presentationTimeUs/1000/20)%18;     //縦モード:0-17 20[ms]を1Frameとする
            }else{
                number = (int)(presentationTimeUs/1000/20)%25 + 18;//横モード:18-42 20[ms]を1Frameとする
            }
            InputStream inputStream = mContext.getResources().openRawResource(mEffectFrameIdArray[number]);
            frameImg = BitmapFactory.decodeStream(inputStream);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return frameImg;
    }


    /**
     * 再生速度の確認関数
     * 　※ベースアプリでは実速度時はエフェクトを表示しないようにしている
     * @param nowMoviePlayRate 再生速度
     */
    private void checkMoviePlayRate(double nowMoviePlayRate) {

        if(nowMoviePlayRate==0){
            //再生速度保存値を更新しない
            mPlayStatus = STATUS_PAUSE;
        } else {
            mLastPlayRate = nowMoviePlayRate;
            if (mVideoInfo != null) {
                if (nowMoviePlayRate == 1) {
                    mEffectShow = false;
                    mPlayStatus = STATUS_REALTIME_PLAY;
                } else {
                    mEffectShow = true;
                    mPlayStatus = STATUS_PLAY;
                }
            } else {
                mPlayStatus = STATUS_PLAY;
            }
        }
    }

    /**
     * 再生•一時停止関数
     * @param play 再生状態(true:再生、false:一時停止)
     */
    private void playPauseMethod(boolean play) {
        if(play){
            //再生
            if (mTimeManager != null) {
                //再生速度の設定
                mTimeManager.setPlayRateFromPlayer(mPlayerId, mLastPlayRate);
                mPlayStatus = STATUS_PLAY;
                if (mLastPlayRate != 0) {

                    //他プレイヤーに再生状態を通知し状態の同期を図る
                    if (mInstructionSyncController != null) {
                        int instructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY;
                        mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                        mInstructionType = instructionType;
                    }
                }
            }
        } else {
            //一時停止
            if (mTimeManager != null) {
                //再生速度の設定
                mTimeManager.setPlayRateFromPlayer(mPlayerId, 0);
                mPlayStatus = STATUS_PAUSE;
            }
        }
    }


    /**
     * 操作系UIの表示チェック関数
     * 　Androidの仕様上タイマースレッドからUIを操作するため、UIのスレッドを呼び出して実行
     */
    private void updateAllUi(){
        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                checkPlayBtnMode(); //再生ボタン確認
                checkRealtimeRate();//実速度ボタン確認
                updateTimeUi();     //時刻、シークバー確認
            }
        });
    }

    /**
     * 再生•一時停止ボタンの表示確認関数
     */
    private void checkPlayBtnMode() {
        switch (mPlayStatus) {
            case STATUS_PLAY:
                //mPlayPauseBtn.setImageResource(R.drawable.pause_button);
                mStartAndPauseImageView.setImageResource(R.drawable.ic_play_gr);

                break;
            case STATUS_PAUSE:
                //mPlayPauseBtn.setImageResource(R.drawable.play_button);
                mStartAndPauseImageView.setImageResource(R.drawable.ic_play_gr);

                break;
            case STATUS_REALTIME_PLAY:
                //mPlayPauseBtn.setImageResource(R.drawable.pause_button);
                mStartAndPauseImageView.setImageResource(R.drawable.ic_play_gr);
                break;
            default :
                break;
        }
    }

    /**
     * 実速度再生ボタンと戻るボタンの表示確認関数
     */
    private void checkRealtimeRate() {

        if (mTimeManager != null) {
            double nowPlayRate = mTimeManager.getPlayRateFromPlayer(mPlayerId);
            if (nowPlayRate == 0) {
                //表示を変化させない
            } else if(nowPlayRate == 1) {
                //mRealTimePlayBtn.setEnabled(false);
                mPlayImageView.setEnabled(false);

            }else {
                //mRealTimePlayBtn.setEnabled(true);
                mPlayImageView.setEnabled(true);
            }
        }
    }

    /**
     * 動画の時間に関するUIを更新する関数(シークバー、時間表示)
     */
    private void updateTimeUi(){
        labelUpdate();
        if (!mSeekBarUserControl) {
            if (mVideoSeekBar != null) {
                mVideoSeekBar.setProgress((int)mPresentationTimeUs);
            }
        }
    }

    /**
     * 動画の時間表示を更新する関数
     */
    private void labelUpdate() {
        int time = (int)mPresentationTimeUs/1000;//[ms]
        int msec = time%1000;
        int sec = (time/1000)%60;
        int min = ((time/1000)-sec)%(60*60)/60;
        int hour = (time/1000)/(60*60);

        //String timeStr = String.format("%d:%02d:%02d:%03d",hour,min,sec,msec);
        String timeStr = String.format("%d:%02d:%02d",min,sec,msec);
        if(mTimeText != null)
            mTimeText.setText(timeStr);

        mLastPresentationTimeUs = mPresentationTimeUs;
   }


    /**
     *現在の反転状態からボタンの表示を設定する関数
     */
    private void checkReversalBtn() {

        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVideoGlLayer != null) {
                    float nowReversalType = mVideoGlLayer.getReversalType();

                    if (nowReversalType == GlLayer.REVERSAL_LEFTRIGHT) {
                        if (mReversalLeftRightBtn != null) {
                            mReversalLeftRightBtn.setTextColor(Color.RED);
                        }
                        if (mReversalUpDownBtn != null) {
                            mReversalUpDownBtn.setTextColor(Color.WHITE);
                        }
                    } else if (nowReversalType == GlLayer.REVERSAL_UPDOWN) {
                        if (mReversalLeftRightBtn != null) {
                            mReversalLeftRightBtn.setTextColor(Color.WHITE);
                        }
                        if (mReversalUpDownBtn != null) {
                            mReversalUpDownBtn.setTextColor(Color.RED);
                        }
                    } else {
                        if (mReversalLeftRightBtn != null) {
                            mReversalLeftRightBtn.setTextColor(Color.WHITE);
                        }
                        if (mReversalUpDownBtn != null) {
                            mReversalUpDownBtn.setTextColor(Color.WHITE);
                        }
                    }
                }
            }
        });
    }

    /**
     * テクスチャ透過度設定関数
     * @param textureAlpha 設定するテクスチャ透過度
     * @return 0:設定成功　-1:設定失敗
     */
    public int setTextureAlpha(float textureAlpha){
        if(mIsPreparedVideoGlLayer) {
            if (mVideoGlLayer != null) {
                mVideoGlLayer.setTextureAlpha(textureAlpha);
                return 0;
            }
        }
        return -1;
    }

    /**
     * エフェクトレイヤーの非表示設定関数
     * @param isHidden true:表示 false:非表示
     */
    public void setEffectGlLayerHidden(boolean isHidden){

        int visibility;
        if(isHidden){
            visibility = View.GONE;
        }else{
            visibility = View.VISIBLE;
        }
        mEffectView.setVisibility(visibility);
    }

    /**
     * デバッグレイヤー(回転、上下反転、左右反転、縦横表示設定レイヤー)の非表示設定関数
     * @param isHidden true:表示 false:非表示
     */
    public void setDebugBtnHidden(boolean isHidden){

        int visibility;
        if(isHidden){
            visibility = View.GONE;
        }else{
            visibility = View.VISIBLE;
        }
        if(mMainTopOperationLayout != null)
            mMainTopOperationLayout.setVisibility(visibility);
    }

    /**
     * コントロールレイヤー(再生ボタン、シークバー)の非表示設定関数
     * @param isHidden true:表示 false:非表示
     */
    public void setOperationLayerHidden(boolean isHidden){

        int visibility;
        if(isHidden){
            visibility = View.GONE;
        }else{
            visibility = View.VISIBLE;
        }

        if (mMainBottomOperationLayout != null){
            if(!mOperationViewHidden && !isHidden) {
                mMainBottomOperationLayout.setVisibility(View.VISIBLE);
            }else {
                mMainBottomOperationLayout.setVisibility(View.INVISIBLE);
            }
        }
        if (mMainOperationControlButtonLayout != null){
            mMainOperationControlButtonLayout.setVisibility(visibility);
        }
    }

    /**
     * プレイヤーの現在の回転状態の取得
     * @return GlLayerクラスのAngleタイプ
     */
    public float getRotateAngle(){
        if (mVideoGlLayer != null) {

            return mVideoGlLayer.getRotateAngle();
        }
        return -1;
    }

    /**
     * プレイヤーの現在の反転状態の取得
     * @return GlLayerクラス　REVERSALタイプ
     */
    public float getReversalType(){
        if (mVideoGlLayer != null) {

            return mVideoGlLayer.getReversalType();
        }
        return -1;
    }


    private class ClickListenerImpl implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backRelativeLayout:
                    getActivity().finish();
                    break;
                case R.id.editImageView:
                    showTipsDialog();
                    break;
                case R.id.operateRelativeLayout:
                    showOperateDialog();
                    break;
                case R.id.move_cut_LinearLayout:
                    mIntent=new Intent(mContext,CutVideoActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.move_combine_LinearLayout:
                    System.out.println("-----> PPT上未说明跳转");
                    break;
                case R.id.move_reflect_LinearLayout:
                    System.out.println("-----> 视频翻转,不用页面跳转.");
                    if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                        mOperateDialog.dismiss();

                        //動画像の現在の反転状態取得
                        float nowReversalType = mVideoGlLayer.getReversalType();
                        if (nowReversalType == GlLayer.REVERSAL_DEFAULT) {
                            setReversalType(GlLayer.REVERSAL_LEFTRIGHT);
                        } else if (nowReversalType == GlLayer.REVERSAL_LEFTRIGHT) {
                            setReversalType(GlLayer.REVERSAL_DEFAULT);
                        } else {
                            setReversalType(GlLayer.REVERSAL_LEFTRIGHT);
                        }
                        checkReversalBtn();
                    }
                    System.out.println("-----> 在此测试教程.以后请注释下行代码");
                    //showGuideDialog();
                    break;
                case R.id.move_revolution_LinearLayout:
                    System.out.println("-----> 视频旋转,不用页面跳转.");
                    if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                        mOperateDialog.dismiss();
                    }
                    showRevolutionDialog();
                    break;
                case R.id.move_movtx_LinearLayout:
                    System.out.println("-----> 显示视频信息,不用页面跳转.");
                    if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                        mOperateDialog.dismiss();
                    }
                    showVideoInfoDialog();
                    break;
                case R.id.revolution_TextView_90:
                    System.out.println("-----> 旋转90");
                    if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                        mRevolutionDialog.dismiss();
                        setRotateAngle(GlLayer.ROTATE_ANGLE_90);
                    }
                    break;
                case R.id.revolution_TextView_180:
                    System.out.println("-----> 旋转180");
                    if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                        mRevolutionDialog.dismiss();
                        setRotateAngle(GlLayer.ROTATE_ANGLE_180);
                    }
                    break;
                case R.id.revolution_TextView_270:
                    System.out.println("-----> 旋转270");
                    if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                        mRevolutionDialog.dismiss();
                        setRotateAngle(GlLayer.ROTATE_ANGLE_270);
                    }
                    break;
                case R.id.closeVideoInfoRelativeLayout:
                    System.out.println("-----> 关闭对话框");
                    if(null!=mVideoInfoDialog&&mVideoInfoDialog.isShowing()){
                        mVideoInfoDialog.dismiss();
                    }
                    break;
                case R.id.okTextView:
                    if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                        mTipsDialog.dismiss();
                    }
                    System.out.println("-----> 跳转到 ppt e14 最复杂的画面");
                    break;
                case R.id.cancelTextView:
                    if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                        mTipsDialog.dismiss();
                    }
                    break;
                case R.id.playImageView:
                    //开始播放视频
                   startPlayVideo();
                    break;
                case R.id.controller_startAndPause_ImageView:
                    startAndPauseVideo();
                    break;
                case R.id.controller_to_start_ImageView:
                    toVideoStart();
                    break;
                case R.id.controller_rate_ImageView:
                    setPlayerRate();
                    break;
            }
        }
    }

    private void startPlayVideo(){
        mPlayImageView.setVisibility(View.GONE);
        mPlayStatus = STATUS_PLAY;
        if (mTimeManager != null) {
            //実速度＝1倍速を設定
            double realTimeMoviePlayRate = 1;
            mTimeManager.setPlayRateFromPlayer(mPlayerId, realTimeMoviePlayRate);

            //他プレイヤーに再生状態を通知し状態の同期を図る
            if (mInstructionSyncController != null) {
                int instructionType = InstructionSyncController.INSTRUCTION_TYPE_PLAY;
                mInstructionSyncController.setInstructionTypeFromPlayer(mPlayerId, instructionType);
                mInstructionType = instructionType;
            }
        }
    }


    /**
     * 注意在checkPlayBtnMode()方法中替换图片!!!!!!!
     */
    private void startAndPauseVideo(){
        switch(mPlayStatus){
            case STATUS_PAUSE:
                playPauseMethod(true);
                break;
            case STATUS_PLAY:
                playPauseMethod(false);
                break;
            case STATUS_REALTIME_PLAY:
                playPauseMethod(false);
                break;
            default:
                break;
        }
    }

    private void toVideoStart(){
        if (mTimeManager != null) {
            // 再生を停止、再生時刻に0にシーク
            mTimeManager.setPlayRateFromPlayer(mPlayerId, 0);
            mTimeManager.setCurrentPtsUsFromPlayer(mPlayerId, 0);
        }
    }

    //设置播放速率
    private void setPlayerRate(){
        if(playerRate>=4){
            playerRate=1;
        }else {
            playerRate++;
        }
        mTimeManager.setPlayRateFromPlayer(mPlayerId,playerRate);
    }



    //显示操作(剪切,翻转,旋转等)对话框
    private void showOperateDialog(){
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_operate, null);
        ScreenUtil.initScale(dialogView);
        mOperateDialog= new AlertDialog.Builder(mContext,R.style.dialog).create();
        mOperateDialog.show();
        Window window = mOperateDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mOperateDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mOperateDialog.getWindow().setAttributes(layoutParams);
        mOperateDialog.setContentView(dialogView);
        initOperateDialogItems(dialogView);
    }

    private void initOperateDialogItems(View dialogView) {
        dialogView.findViewById(R.id.move_revolution_LinearLayout).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.move_combine_LinearLayout).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.move_cut_LinearLayout).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.move_movtx_LinearLayout).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.move_reflect_LinearLayout).setOnClickListener(mClickListenerImpl);
    }

    //显示视频旋转(90度,180度,270度)对话框
    private void showRevolutionDialog(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_revolution, null);
        ScreenUtil.initScale(dialogView);
        mRevolutionDialog= new AlertDialog.Builder(mContext,R.style.dialog).create();
        mRevolutionDialog.show();
        Window window = mRevolutionDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mRevolutionDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mRevolutionDialog.getWindow().setAttributes(layoutParams);
        mRevolutionDialog.setContentView(dialogView);
        initRevolutionDialogItems(dialogView);
    }

    private void initRevolutionDialogItems(View dialogView){
        dialogView.findViewById(R.id.revolution_TextView_90).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.revolution_TextView_180).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.revolution_TextView_270).setOnClickListener(mClickListenerImpl);
    }

    //显示Video信息
    private void showVideoInfoDialog(){
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_video_info, null);
        ScreenUtil.initScale(dialogView);
        mVideoInfoDialog= new Dialog(mContext,R.style.dialog);
        mVideoInfoDialog.setContentView(dialogView);
        Window dialogWindow = mVideoInfoDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.47);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.7);
        dialogWindow.setAttributes(layoutParams);
        mVideoInfoDialog.show();
        initVideoInfoDialog(dialogView);
    }

    private void initVideoInfoDialog(View dialogView){
        dialogView.findViewById(R.id.closeVideoInfoRelativeLayout).setOnClickListener(mClickListenerImpl);
    }


    //提醒用户是否要编辑视频
    private void showTipsDialog(){
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_video_tips, null);
        ScreenUtil.initScale(dialogView);
        mTipsDialog= new Dialog(mContext,R.style.dialog);
        mTipsDialog.setContentView(dialogView);
        Window dialogWindow = mTipsDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.52);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.9);
        dialogWindow.setAttributes(layoutParams);
        mTipsDialog.show();
        initTipsDialog(dialogView);
    }

    private void initTipsDialog(View dialogView){
        dialogView.findViewById(R.id.okTextView).setOnClickListener(mClickListenerImpl);
        dialogView.findViewById(R.id.cancelTextView).setOnClickListener(mClickListenerImpl);
    }



    //--------------> 以下代码与教程相关
    //显示ViewPager的Dialog

    //注意ViewPager第二页展示的图片是错误的,需要重新切图！！！！！
    //即view_video_guide_b中的img_g是不对的
    private void showGuideDialog(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_video_guide, null);
        ScreenUtil.initScale(dialogView);
        mGuideDialog= new Dialog(mContext,R.style.dialog);
        mGuideDialog.setContentView(dialogView);
        Window dialogWindow = mGuideDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.47);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.8);
        dialogWindow.setAttributes(layoutParams);
        mGuideDialog.show();
        initViewPager(dialogView);
    }

    private void initViewPager(View dialogView){
        mViewPager = (ViewPager)dialogView.findViewById(R.id.guide_viewpager);
        mDotsLinearLayout = (LinearLayout)dialogView.findViewById(R.id.dotsLinearLayout);
        mViewPagerAdapter = new VideoViewPagerAdapter(mContext,mGuideDialog);
        mPageChangeListenerImpl = new PageChangeListenerImpl();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListenerImpl);
        initDots();
    }

    //初始化小圆点
    private void initDots() {
        dotImageViews = new ImageView[mViewPagerAdapter.getCount()];
        for (int i = 0; i < dotImageViews.length; i++) {
            LinearLayout layout = new LinearLayout(mContext);
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ScreenUtil.getScalePxValue(24), ScreenUtil.getScalePxValue(24)));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.guide_dot_white);
            } else {
                layout.setPadding(ScreenUtil.getScalePxValue(16), 0, 0, 0);
                imageView.setBackgroundResource(R.drawable.guide_dot_black);
            }
            dotImageViews[i] = imageView;
            layout.addView(imageView);
            mDotsLinearLayout.addView(layout);
        }
    }

    private class PageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < dotImageViews.length; i++) {
                dotImageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
                if (arg0 != i) {
                    dotImageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }
    //--------------> 以上代码与教程相关







}
