package com.windward.www.casio_golf_viewer.casio.golf.player;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.view.Surface;

public class VideoGlLayer {

	private GlLayer mGlLayer;

	public VideoGlLayer(GLSurfaceView glSurfaceView) {
		mGlLayer = new GlLayer(glSurfaceView);
	}

	public VideoGlLayer(GLSurfaceView glSurfaceView, boolean isUseSurface, GlLayer.GlLayerCallback glLayerCallback) {
		mGlLayer = new GlLayer(glSurfaceView,isUseSurface,glLayerCallback);
	}

	/**
	 * 描画画像のセット関数（Surfaceを利用しないときに使用）
	 * @param bitmap 画像
	 */
	public void setDrawImage(Bitmap bitmap) {
		mGlLayer.setDrawImage(bitmap);
	}

	public Surface getSurface() {
		return mGlLayer.getSurface();
	}

	/**
	 *  反転タイプ設定関数 
	 *  @param reversalType 設定する反転タイプ(デフォルト、左右反転、上下反転) 
	 * 
	 */
	public void setReversalType(int reversalType) {
		mGlLayer.setReversalType(reversalType);
	}

	/**
	 *  現在の反転タイプ取得関数 
	 *  @return 現在の反転タイプ(デフォルト、左右反転、上下反転) 
	 */
	public float getReversalType() {
		return mGlLayer.getReversalType();
	}

	/**
	 *  拡大倍率差分加算関数 
	 *  @param diffZoomLevel 加算する拡大倍率 
	 */
	public void setZoomLevel(float diffZoomLevel) {
		mGlLayer.setZoomLevel(diffZoomLevel);
	}

	/**
	 *  現在の拡大倍率取得関数 
	 *  @return zoomLevel 現在の拡大倍率 
	 */
	public float getZoomLevel() {
		return mGlLayer.getZoomLevel();
	}

	/**
	 *  表示位置移動設定関数 
	 *  @param diffMovementX 加算するx軸の移動距離 
	 *  @param diffMovementY 加算するy軸の移動距離 
	 */
	public void setTranslate(float diffMovementX, float diffMovementY) {
		mGlLayer.setTranslate(diffMovementX,diffMovementY);
	}

	/**
	 *  移動距離の取得関数 
	 *  @return 現在の移動距離 
	 */
	public PointF getTranslate() {
		return mGlLayer.getTranslate();
	}

	/**
	 *  回転量の設定関数 
	 *  @param rotateType 回転タイプ(0度、90度、180度、270度の4種類) 
	 */
	public void setRotateAngle(float rotateType) {
		mGlLayer.setRotateAngle(rotateType);
	}

	/**
	 *  回転量の取得関数 
	 *  @return 現在の回転量 
	 */
	public float getRotateAngle() {
		return mGlLayer.getRotateAngle();
	}

	/**
	 *  透過率設定関数
	 *  @param alpha 透過率
	 */
	public void setTextureAlpha(float alpha) {
		mGlLayer.setTextureAlpha(alpha);
	}

	/**
	 *  現在の透過率取得関数
	 *  @return 現在の透過率
	 */
	public float getTextureAlpha() {
		return mGlLayer.getTextureAlpha();
	}

	/**
	 *  画面サイズ変更関数
	 *   引数に0を設定した場合や何も設定しない場合、画面サイズ一杯に画像を表示
	 *  @param width 表示画像の横サイズ 
	 *  @param height 表示画像の縦サイズ
	 */
	public void changeImageSize(int width, int height) {
		mGlLayer.changeImageSize(width, height);
	}

}
