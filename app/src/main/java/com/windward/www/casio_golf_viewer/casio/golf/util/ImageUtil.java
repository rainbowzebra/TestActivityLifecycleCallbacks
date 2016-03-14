package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageUtil {

	public static int imageWidth = 0;
	public static int imageHeight = 0;

	public static Bitmap getBitmapFromUri(Context context, Uri uri, int width,
			int height) {
		if (uri != null) {
			try {
				BitmapFactory.Options opts = null;
				if (width > 0 && height > 0) {
					opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					ContentResolver cr = context.getContentResolver();
					BitmapFactory.decodeStream(cr.openInputStream(uri), null,
							opts);
					// 计算图片缩放比例
					final int minSideLength = Math.min(width, height);
					opts.inSampleSize = computeSampleSize(opts, minSideLength,
							width * height);
					opts.inJustDecodeBounds = false;
					opts.inInputShareable = true;
					opts.inPurgeable = true;
					Bitmap bitmap = BitmapFactory.decodeStream(
							cr.openInputStream(uri), null, opts);
					if (bitmap != null) {

						int degress = 0;
						try {
							String[] querys = { MediaStore.Images.Media.ORIENTATION };
							Cursor cur = ((Activity) context).managedQuery(uri,
									querys, null, null, null);
							if (cur != null && cur.getCount() > 0) {
								cur.moveToFirst();
								degress = cur
										.getInt(cur
												.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						Matrix matrix = new Matrix();
						matrix.setRotate(degress);
						Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), matrix,
								true);
						imageWidth = bmp.getWidth();
						imageHeight = bmp.getHeight();
						if (imageWidth < 480) {
							float sclae = (float) 480 / imageWidth;
							matrix = new Matrix();
							matrix.setScale(sclae, sclae);
							bmp = Bitmap.createBitmap(bmp, 0, 0,
									bmp.getWidth(), bmp.getHeight(), matrix,
									true);
							imageWidth = bmp.getWidth();
							imageHeight = bmp.getHeight();
						}
						return bmp;
					}
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getBitmapFromFile(String path, int width, int height) {
		if (null != path && new File(path).exists()) {
			try {
				BitmapFactory.Options opts = null;
				if (width > 0 && height > 0) {
					opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(path, opts);
					// 计算图片缩放比例
					final int minSideLength = Math.min(width, height);
					opts.inSampleSize = computeSampleSize(opts, minSideLength,
							width * height);
					opts.inJustDecodeBounds = false;
					opts.inInputShareable = true;
					opts.inPurgeable = true;
					Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
					if (bitmap != null) {
						Matrix matrix = new Matrix();
						int orientation = 1;
						try {
							ExifInterface exif = new ExifInterface(path);
							orientation = exif.getAttributeInt(
									ExifInterface.TAG_ORIENTATION, 1);
						} catch (IOException e) {
							e.printStackTrace();
						}
						switch (orientation) {
						case 1:
							break;
						case 2:
							matrix.invert(matrix);
							break;
						case 3:
							matrix.setRotate(180);
							break;
						case 4:
							matrix.invert(matrix);
							matrix.setRotate(180);
							break;
						case 5:
							matrix.setRotate(90);
							matrix.invert(matrix);
							break;
						case 6:
							matrix.setRotate(90);
							break;
						case 7:
							matrix.invert(matrix);
							matrix.setRotate(90);
							break;
						case 8:
							matrix.setRotate(270);
							break;
						default:
							break;
						}

						Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), matrix,
								true);
						imageWidth = bmp.getWidth();
						imageHeight = bmp.getHeight();
						if (imageWidth < 480) {
							float sclae = (float) 480 / imageWidth;
							matrix = new Matrix();
							matrix.setScale(sclae, sclae);
							bmp = Bitmap.createBitmap(bmp, 0, 0,
									bmp.getWidth(), bmp.getHeight(), matrix,
									true);
							imageWidth = bmp.getWidth();
							imageHeight = bmp.getHeight();
						}
						return bmp;
					}
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}
