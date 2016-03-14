package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class ComposeUtils {


	public static final Bitmap fuseImgs(Bitmap bg, Bitmap model, float x,
			float y, float width, float heiht) {
		Bitmap bmRes = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(),
				Bitmap.Config.ARGB_8888);

		Rect bgRect = new Rect(0, 0, bg.getWidth(), bg.getHeight());
		RectF modelRect = new RectF(x, y, x + width, y + heiht);

		Paint paint = new Paint();
		Canvas canvas = new Canvas(bmRes);
//		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawARGB(255, 255, 255, 255);
		paint.setAntiAlias(true);

		canvas.drawBitmap(bg, bgRect, bgRect, paint);
		canvas.save();

		Matrix matrix = new Matrix();
//		 matrix.setRectToRect(new RectF(bgRect), modelRect,
//		 ScaleToFit.CENTER);
		matrix.setRectToRect(
				new RectF(0, 0, model.getWidth(), model.getHeight()),
				modelRect, ScaleToFit.CENTER);
		canvas.concat(matrix);

		canvas.restore();

		canvas.drawBitmap(model, matrix, paint);

		return bmRes;
	}

	public static final TextPaint createPaint(String content, float x, float y,
			int color, float size, int width, int height) {
		return new TextPaint(content, x, y, color, size, width, height);
	}


	public static final Bitmap setImgsText(Bitmap bg, TextPaint paint1,
			TextPaint paint2) {
		Bitmap bmRes = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(),
				Bitmap.Config.ARGB_8888);

		Rect bgRect = new Rect(0, 0, bg.getWidth(), bg.getHeight());

		Paint paint = new Paint();
		Canvas canvas = new Canvas(bmRes);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setAntiAlias(true);

		canvas.drawBitmap(bg, bgRect, bgRect, paint);

		if (paint1 != null) {
			paint1.draw(canvas);
		}

		if (paint2 != null) {
			paint2.draw(canvas);
		}

		return bmRes;
	}

	public static final Bitmap fuseImg(Bitmap bgBm, Bitmap model) {
		Bitmap bmCanvas = Bitmap.createBitmap(bgBm.getWidth(),
				bgBm.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmCanvas);

		Rect rectBg = new Rect(0, 0, bgBm.getWidth(), bgBm.getHeight());
		int offX = bgBm.getWidth() / 2 - 75;
		int offY = bgBm.getHeight() / 2 - 75;
		Rect rectModel = new Rect(offX, offY, offX + 150, offY + 150);

		Paint paint = new Paint();
		canvas.drawARGB(0, 0, 0, 0);
		paint.setAntiAlias(true);

		canvas.drawBitmap(bgBm, rectBg, rectBg, paint);

		// Paint paint2 = new Paint(paint);
		// paint2.setColor(Color.RED);
		// canvas.drawRect(rectModel, paint2);

		Matrix matrix = new Matrix();
		matrix.setRectToRect(
				new RectF(0, 0, model.getWidth(), model.getHeight()),
				new RectF(rectModel), ScaleToFit.FILL);

		canvas.drawBitmap(model, matrix, paint);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.parseColor("#3d3d3d"));
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(42);
		canvas.drawText("Color", 100f, 100f, textPaint);

		return bmCanvas;
	}

	public static class TextPaint extends Paint {
		private String content;
		private float x;
		private float y;
		private int color;

		private float size;
		private RectF rectF;
		private FontMetricsInt fontMetrics;

		private TextPaint(String content, float x, float y, int color,
				float size, int width, int height) {
			super();
			this.content = content;
			this.x = x;
			this.y = y;
			this.color = color;
			this.size = size;
			rectF = new RectF(x, y, x + width, y + height);
			fontMetrics = getFontMetricsInt();
			initPaint();
		}

		private void initPaint() {
			setAntiAlias(true);
			setTextSize(size);
			setColor(color);
		}

		public String getContent() {
			return content;
		}

		public RectF getRectF() {
			return rectF;
		}

		public void draw(Canvas canvas) {
			float baseline = rectF.top
					+ (rectF.bottom - rectF.top - fontMetrics.bottom + fontMetrics.top)
					/ 2 - fontMetrics.top;

			int width = (int) (measureText(content) + 0.5f);

			canvas.drawText(content, (int) ((canvas.getWidth() - width) / 2),
					baseline, this);
		}

	}
}
