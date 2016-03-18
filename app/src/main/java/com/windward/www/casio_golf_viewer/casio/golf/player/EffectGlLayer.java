package com.windward.www.casio_golf_viewer.casio.golf.player;


import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

public class EffectGlLayer {

	private GlLayer mGlLayer;

	public EffectGlLayer(GLSurfaceView glSurfaceView) {
		mGlLayer = new GlLayer(glSurfaceView);
	}

	/**
	 * 描画画像のセット関数
	 * @param bitmap 画像
	 */
	public void setUIImage(Bitmap bitmap) {
		mGlLayer.setDrawImage(bitmap);
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
