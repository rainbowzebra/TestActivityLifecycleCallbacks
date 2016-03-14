package com.windward.www.casio_golf_viewer.casio.golf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class PhotoUtils {

    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    /**
     * 从文件中获取图片
     *
     * @param path 图片的路径
     * @return
     */
    public static Bitmap getBitmapFromFile(String path) {
        try {
            return BitmapFactory.decodeFile(path);
        } catch (OutOfMemoryError e) {
            return decodeSampledBitmapFromFile(path, 480, 480);
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFile(filename, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * 从Uri中获取图片
     *
     * @param cr  ContentResolver对象
     * @param uri 图片的Uri
     * @return
     */
    public static Bitmap getBitmapFromUri(ContentResolver cr, Uri uri) {
        try {
            return BitmapFactory.decodeStream(cr.openInputStream(uri));
        } catch (FileNotFoundException e) {

        }
        return null;
    }

    /**
     * 根据宽度和长度进行缩放图片
     *
     * @param path 图片的路径
     * @param w    宽度
     * @param h    长度
     * @return
     */
    public static Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图片
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取图片的长度和宽度
     *
     * @param bitmap 图片bitmap对象
     * @return
     */
    public static Bundle getBitmapWidthAndHeight(Bitmap bitmap) {
        Bundle bundle = null;
        if (bitmap != null) {
            bundle = new Bundle();
            bundle.putInt("width", bitmap.getWidth());
            bundle.putInt("height", bitmap.getHeight());
            return bundle;
        }
        return null;
    }

    /**
     * 判断图片高度和宽度是否过大
     *
     * @param bitmap 图片bitmap对象
     * @return
     */
    public static boolean bitmapIsLarge(Bitmap bitmap, int w, int h) {
        final int MAX_WIDTH = w;
        final int MAX_HEIGHT = h;
        Bundle bundle = getBitmapWidthAndHeight(bitmap);
        if (bundle != null) {
            int width = bundle.getInt("width");
            int height = bundle.getInt("height");
            if (width > MAX_WIDTH && height > MAX_HEIGHT) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据比例缩放图片
     *
     * @param screenWidth 手机屏幕的宽度
     * @param filePath    图片的路径
     * @param ratio       缩放比例
     * @return
     */
    public static Bitmap compressionPhoto(float screenWidth, String filePath,
                                          int ratio) {
        Bitmap bitmap = PhotoUtils.getBitmapFromFile(filePath);
        Bitmap compressionBitmap = null;
        float scaleWidth = screenWidth / (bitmap.getWidth() * ratio);
        float scaleHeight = screenWidth / (bitmap.getHeight() * ratio);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            compressionBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            return bitmap;
        }
        return compressionBitmap;
    }

    /**
     * 将图片保存到ImageLoader 缓存中, 减少网络开销.
     *
     * @param context
     * @param bmPath  本地图片
     * @param url     对应的网络地址
     * @return
     */
//    public static String saveToImgCache(Context context, String bmPath, String url) {
//        Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
//        File f1 = new File(bmPath);
//        File file2 = new File(StorageUtils.getIndividualCacheDirectory(context),
//                md5FileNameGenerator.generate(url)
//        );
//        try {
//            FileUtils.forJava(f1, file2);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        if (file2.exists())
//            return file2.getAbsolutePath();
//        else {
//            return null;
//        }
//    }

    /**
     * 保存图片到SD卡(并压缩)
     *
     * @param bitmap
     * @param fileName 文件存放的地址
     * @return
     */
//    public static String saveBmpToSdCard(Bitmap bitmap, File fileName) {
//        if (!FileUtils.isSdcardExist()) {
//            return null;
//        }
//
//        if (fileName == null) {
//            return null;
//        }
//        if (!fileName.getParentFile().exists()) {
//            fileName.getParentFile().mkdirs();
//        }
//
//        if (!fileName.exists()) {
//            try {
//                fileName.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        try {
//            if (bitmap != null){
//                Bitmap bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//                Canvas canvas = new Canvas(bm);
//                canvas.drawARGB(255, 255, 255, 255);
//                canvas.drawBitmap(bitmap, 0, 0, null);
//                bitmap = bm;
//            }
//            NativeUtil.compressBitmap(bitmap, fileName.getAbsolutePath(), true);
//        } catch (Throwable e) {
//            FileOutputStream os = null;
//            try {
//                os = new FileOutputStream(fileName);
//                bitmap.compress(CompressFormat.JPEG, 80, os);
//            } catch (Exception e2) {
//            } finally {
//                if (os != null) {
//                    try {
//                        os.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//
//        }
//
//        return fileName.getAbsolutePath();
//    }


    /**
     * 获取圆角图片
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获取颜色的圆角bitmap
     *
     * @param context
     * @param color
     * @return
     */
    public static Bitmap getRoundBitmap(Context context, int color) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12.0f, metrics));
        int height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4.0f, metrics));
        int round = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2.0f, metrics));
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(new RectF(0.0F, 0.0F, width, height), round,
                round, paint);
        return bitmap;
    }

    /**
     * 获取bitmap的字节大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static String getPictureName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmssSSS");
        String path = dateFormat.format(date) + ".jpg";
        return path;
    }


    @Deprecated
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 150) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
