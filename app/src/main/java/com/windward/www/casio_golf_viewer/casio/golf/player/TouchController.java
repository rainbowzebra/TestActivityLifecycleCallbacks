package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class TouchController {

	/**
	 * タッチコントローラリスナー
	 */
	public interface OnTouchControllerListener {

		/**
		 * タッチ開始
		 */
		public void onTouchBegan();

		/**
		 * シングルタップ
		 */
		public void onSingleTap();

		/**
		 * ダブルタップ
		 */
		public void onDoubleTap();

		/**
		 * １本指スワイプ
		 * @param status　指の状態
		 * @param diffTimeUs　進める再生時間(差分)
		 */
		public void onOneFingerSwipe(int status, long diffTimeUs);

		/**
		 * フリック
		 * @param playRate　指の速度
		 */
		public void onFlick(double playRate);

		/**
		 * ズーム
		 * @param center　中心位置
		 * @param diffZoomLevel　ズームレベル(差分)
		 */
		public void onZoom(PointF center, float diffZoomLevel);

		/**
		 * ２本指スワイプ
		 * @param status　指の状態
		 * @param diffMovementX　X方向の移動距離(差分)
		 * @param diffMovementY　Y方向の移動距離(差分)
		 */
		public void onTwoFingersSwipe(int status, float diffMovementX, float diffMovementY);

	}

	//指の状態
	public final static int STATUS_DOWN = 0;	//ダウン
	public final static int STATUS_MOVE = 1;	//ムーブ
	public final static int STATUS_UP = 2;		//アップ
	public final static int STATUS_CANCEL = 3;	//キャンセル
	//public final static int STATUS_OUTSIDE = 4;

	private static double DISTANCE_THRESH_ONE_FINGER = 20;	//１本指スワイプモードの移動距離のスレッシュ
	private static double DISTANCE_THRESH_TWO_FINGER = 50;	//２本指スワイプモードの移動距離のスレッシュ

	private final static long MAX_TIME_SCALE_US = 1 * 60 * 60 * 1000 * 1000L;  // TimeScaleUsの許容最大値(1hour/dp)
	private final static long MIN_TIME_SCALE_US = -1 * 60 * 60 * 1000 * 1000L; // TimeScaleUsの許容最小値(-1hour/dp)

	private long mTimeScaleUs;		//移動距離に対して進める再生時間の比率（μs/pixel）
	private boolean mIsVertical;	//縦モード

	private long MAX_TOUCH_DISTANCE_DIFF;//タッチ幅の最大値
	private double MAX_TOUCH_VELOCITY; //タッチ速度の最大値

	//内部のタッチモード
	private static int MODE_FREE = 0;			//離されている状態
	private static int MODE_ONE_FINGER = 1;		//１本指タッチ状態
	private static int MODE_TWO_FINGER = 2;		//２本指タッチ状態
	private static int MODE_SINGLE_TAP = 3;		//シングルタップ状態
	private static int MODE_DOUBLE_TAP = 4;		//ダブルタップ状態
	private static int MODE_ZOOM = 5;			//ピンチズーム状態

	private int mTouchMode = 0;					//タッチモード保持変数
	private boolean mIsTwoTouch = false;		//２本指でタッチしているか保持変数

	private PointF mStartPositionAtOneFinger;		//最初に１本指でタッチした位置
	private PointF mStartFocusPositionAtTwoFinger;	//最初に２本指でタッチしたときの中心位置
	private PointF mLastPositionAtOneFinger;		//直近の１本指でタッチした位置
	private PointF mLastFocusPositionAtTwoFinger;	//直近の２本指でタッチした中心位置

	private OnTouchControllerListener mOnTouchControllerListener;
	private GestureDetector mGestureDetector;						
	private ScaleGestureDetector mScaleGestureDetector;

	/**
	 * コンストラクタ
	 * @param context　コンテキスト
	 * @param view　タッチ判定させるビュー
	 * @param onTouchControllerListener　タッチコントロールリスナー
	 */
	public TouchController(Context context, View view, OnTouchControllerListener onTouchControllerListener) {
		view.setOnTouchListener(this.mOnTouchListener);

		//初期化
		changeTimeScaleUs(8000);
		mIsVertical = true;

		mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
		mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleGestureListener);

		mOnTouchControllerListener = onTouchControllerListener;
	}

	/**
	 * 1本指スワイプ時の方向モードの設定
	 * @param isVertical　方向モード(縦方向true 横方向false)
	 */
	public void setVertical(boolean isVertical) {
		mIsVertical = isVertical;
	}

	/**
	 * 方向モードの取得
	 * @return 方向モード(縦方向true 横方向false)
	 */
	public boolean isVertical() {
		return mIsVertical;
	}

	/**
	 * タッチ移動量に対して進める再生時間の設定
	 * @param timeScaleUs タッチ移動距離に対して進める再生時間の比率（μs/pixel）
	 */
	public void changeTimeScaleUs(long timeScaleUs) {
		if( timeScaleUs <= MAX_TIME_SCALE_US && timeScaleUs >= MIN_TIME_SCALE_US) {
			MAX_TOUCH_DISTANCE_DIFF = Long.MAX_VALUE / timeScaleUs;

			if(timeScaleUs>1000000){
				MAX_TOUCH_VELOCITY = Double.MAX_VALUE/(timeScaleUs / 1000000);
			}else{
				MAX_TOUCH_VELOCITY = Double.MAX_VALUE;
			}

			mTimeScaleUs = timeScaleUs;
		}
	}

	/**
	 * タッチ移動量に対して進める再生時間の取得
	 * @return タッチ移動距離に対して進める再生時間の比率（μs/pixel）
	 */
	public long getTimeScaleUs() {
		return mTimeScaleUs;
	}

	/**
	 * OnTouchListener
	 */
	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			mScaleGestureDetector.onTouchEvent(event);
			mGestureDetector.onTouchEvent(event);

			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:

					if(mTouchMode == MODE_FREE) {
						mStartPositionAtOneFinger = new PointF(event.getX(), event.getY());
						mOnTouchControllerListener.onTouchBegan();
					}
					break;

				case MotionEvent.ACTION_POINTER_DOWN:

					if(mIsTwoTouch == false){
						mIsTwoTouch = true;
						mStartFocusPositionAtTwoFinger = new PointF(mScaleGestureDetector.getFocusX(), mScaleGestureDetector.getFocusY());
					}

					break;

				case MotionEvent.ACTION_MOVE:

					if(mTouchMode == MODE_ONE_FINGER) {

						callOneFinger(event,STATUS_MOVE);

					}else if(mTouchMode == MODE_TWO_FINGER) {

						callTwoFinger(STATUS_MOVE);

					}else if(mTouchMode == MODE_FREE){

						if(mIsTwoTouch) {

							if(checkTwoFingerMoving() == true){
								mTouchMode = MODE_TWO_FINGER;
								callTwoFinger(STATUS_DOWN);
							}
						}else{

							if(checkOneFingerMoving(event) == true) {
								mTouchMode = MODE_ONE_FINGER;
								callOneFinger(event, STATUS_DOWN);
							}
						}
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:

					if(mTouchMode == MODE_TWO_FINGER)
						callTwoFinger(STATUS_UP);

					mIsTwoTouch = false;

					break;

				case MotionEvent.ACTION_UP:

					if(mTouchMode == MODE_ONE_FINGER)
						callOneFinger(event, STATUS_UP);


					mTouchMode = MODE_FREE;

					break;
				case MotionEvent.ACTION_CANCEL:
					if(mTouchMode == MODE_ONE_FINGER)
						callOneFinger(event, STATUS_CANCEL);
					else if(mTouchMode == MODE_TWO_FINGER)
						callTwoFinger(STATUS_CANCEL);

					break;

/*			case MotionEvent.ACTION_OUTSIDE:
				if(mTouchMode == MODE_ONE_FINGER)
					callOneFinger(event, STATUS_OUTSIDE);
				else if(mTouchMode == MODE_TWO_FINGER)
					callTwoFinger(STATUS_OUTSIDE);
				break;
*/		}
			return true;
		}
	};


	/**
	 * mSimpleOnGestureListener
	 */
	private final GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDoubleTap(MotionEvent event) {

			if(mTouchMode == MODE_FREE) {
				mTouchMode = MODE_DOUBLE_TAP;
				mOnTouchControllerListener.onDoubleTap();
			}
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent event) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent event) {

			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

			if(mTouchMode == MODE_ONE_FINGER) {
				callFlick(velocityX, velocityY);
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent event) {
		}

		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
			return super.onScroll(event1, event2, distanceX, distanceY);
		}

		@Override
		public void onShowPress(MotionEvent event) {

			//mOnTouchControllerListener.onSingeTouch();
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {

			if(mTouchMode == MODE_FREE) {
				mTouchMode = MODE_SINGLE_TAP;
				mOnTouchControllerListener.onSingleTap();
				mTouchMode = MODE_FREE;
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			return false;
		}

	};


	/**
	 * mOnScaleGestureListener
	 */
	private final ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener (){

		private PointF mLastFocus;
		private float mScale;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			PointF currentFocus = new PointF(detector.getFocusX(),detector.getFocusY());

			if(mTouchMode == MODE_ZOOM) {

				float mNextScale = detector.getScaleFactor();
				mOnTouchControllerListener.onZoom(currentFocus, mNextScale / mScale);
				mScale = mNextScale;
			}
			return false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {

			if(mTouchMode == MODE_FREE)
			{
				mScale = 1;
				mTouchMode = MODE_ZOOM;
				mLastFocus = new PointF(detector.getFocusX(), detector.getFocusY());
			}

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			if(mTouchMode == MODE_ZOOM)
			{
				//mTouchMode = MODE_FREE;
			}
		}
	};


	/**
	 * １本指操作
	 * @param event　MotinEventのイベント
	 * @param motion_status　タッチ状態
	 */
	private void callOneFinger(MotionEvent event,int motion_status)
	{
		PointF currentPositionAtOneFinger = new PointF(event.getX(), event.getY());

		if(motion_status != STATUS_DOWN) {
			if (mIsVertical == true) {
				long diffTimeY = calcDiffTime(currentPositionAtOneFinger.y, mLastPositionAtOneFinger.y);
				mOnTouchControllerListener.onOneFingerSwipe(motion_status, diffTimeY);
			}else {
				long diffTimeX = calcDiffTime(currentPositionAtOneFinger.x, mLastPositionAtOneFinger.x) * -1;
				mOnTouchControllerListener.onOneFingerSwipe(motion_status, diffTimeX);
			}
		}else{
			mOnTouchControllerListener.onOneFingerSwipe(STATUS_DOWN, 0);
		}
		mLastPositionAtOneFinger = currentPositionAtOneFinger;
	}


	/**
	 * 差分時間計算関数
	 * @param currPoint 最新のタッチ位置
	 * @param prePoint　前回のタッチ位置
	 * @return 差分時間
	 */
	private long calcDiffTime(float currPoint, float prePoint){

		long diffTime;
		float diff = (currPoint - prePoint);

		if(diff < MAX_TOUCH_DISTANCE_DIFF){//上限を超えるか？
			diffTime = (long)diff * mTimeScaleUs;
		}else{
			diffTime = (long)(diff/Math.abs(diff)) * Long.MAX_VALUE;
		}
		return diffTime;
	}


	/**
	 * 1本指の移動スレッシュを超えたか否かの判定
	 * @param event MotionEventのイベント
	 * @return true：超えた false：超えない
	 */
	private boolean checkOneFingerMoving(MotionEvent event) {

		boolean ret = false;

		if(mIsVertical == true)
		{
			if(Math.abs(mStartPositionAtOneFinger.y - event.getY()) > DISTANCE_THRESH_ONE_FINGER)
				ret = true;
		}else{
			if(Math.abs(mStartPositionAtOneFinger.x - event.getX()) > DISTANCE_THRESH_ONE_FINGER)
				ret = true;
		}

		return ret;
	}

	/**
	 * ２本指操作
	 * @param motion_status　タッチ状態
	 */
	private void callTwoFinger(int motion_status)
	{
		PointF currentFocus = new PointF(mScaleGestureDetector.getFocusX(),mScaleGestureDetector.getFocusY());

		if(motion_status != STATUS_DOWN && motion_status != STATUS_UP) {
			mOnTouchControllerListener.onTwoFingersSwipe(motion_status, currentFocus.x - mLastFocusPositionAtTwoFinger.x,currentFocus.y - mLastFocusPositionAtTwoFinger.y);
		}else{
			mOnTouchControllerListener.onTwoFingersSwipe(STATUS_DOWN, 0, 0);
		}

		mLastFocusPositionAtTwoFinger = currentFocus;
	}

	/**
	 * ２本指の移動スレッシュを超えたか否かの判定
	 * @return true：超えた false：超えない
	 */
	private boolean checkTwoFingerMoving()
	{
		boolean ret = false;

		float x = mStartFocusPositionAtTwoFinger.x - mScaleGestureDetector.getFocusX();
		float y = mStartFocusPositionAtTwoFinger.y - mScaleGestureDetector.getFocusY();

		if(Math.sqrt(x*x+y*y) > DISTANCE_THRESH_TWO_FINGER)
			ret = true;

		return ret;
	}

	/**
	 * フリック操作
	 * @param velocityX　X方向のフリック速度(pixel/s)
	 * @param velocityY　Y方向のフリック速度(pixel/s)
	 */
	private void callFlick(float velocityX, float velocityY)
	{
		if(mIsVertical == true)
			mOnTouchControllerListener.onFlick(calcPlayRate(velocityY));
		else
			mOnTouchControllerListener.onFlick(calcPlayRate(velocityX) * -1);
	}

	/**
	 * 差分時間計算関数
	 * @param velocity タッチ速度
	 * @return 速度倍率
	 */
	private double calcPlayRate(float velocity){

		double playRate;
		if(velocity < MAX_TOUCH_VELOCITY){//再生速度の最大値を超えるか？
			playRate = velocity * mTimeScaleUs / 1000000;
		}else{
			playRate = (velocity/Math.abs(velocity)) * Double.MAX_VALUE;
		}
		return playRate;
	}
}
