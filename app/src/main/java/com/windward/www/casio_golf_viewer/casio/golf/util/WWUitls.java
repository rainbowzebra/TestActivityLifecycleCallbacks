package com.windward.www.casio_golf_viewer.casio.golf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class WWUitls {



	// 注意:ListView的item应该是LinearLayout！！！！
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 检测是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connm == null) {
			return false;
		} else {
			NetworkInfo[] nis = connm.getAllNetworkInfo();
			if (nis != null) {
				for (int i = 0; i < nis.length; i++) {
					if (nis[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String bitmaptoString(Bitmap bitmap) {

		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	public static int string2Int(String strNum) {
		int num = 0;
		try {
			if (strNum != null && !strNum.equals(""))
				num = Integer.parseInt(strNum);
		} catch (NumberFormatException e) {
			System.out.println("[NumberFormatException: strNum = " + strNum
					+ "]");
			e.printStackTrace();
		}
		return num;
	}

	public static long string2Long(String strNum) {
		long num = 0;
		try {
			if (strNum != null && !strNum.equals(""))
				num = Long.parseLong(strNum);
		} catch (NumberFormatException e) {
			System.out.println("[NumberFormatException: strNum = " + strNum
					+ "]");
			e.printStackTrace();
		}
		return num;
	}

	public static Float string2Float(String strNum) {
		float num = 0f;
		try {
			if (strNum != null && !strNum.equals(""))
				num = Float.parseFloat(strNum);
		} catch (NumberFormatException e) {
			System.out.println("[NumberFormatException: strNum = " + strNum
					+ "]");
			e.printStackTrace();
		}
		return num;
	}

	public static Double string2Double(String strNum) {
		Double num = 0d;
		try {
			if (strNum != null && !strNum.equals(""))
				num = Double.parseDouble(strNum);
		} catch (NumberFormatException e) {
			System.out.println("[NumberFormatException: strNum = " + strNum
					+ "]");
			e.printStackTrace();
		}
		return num;
	}

	public static void showDialogWithMessage(Context context, String message) {
		showDialogWithMessage(context, message, null);
	}

	public static void showDialogWithMessage(Context context, String message,
			String title) {
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setMessage(message).setNegativeButton("确定", null).create();
		dialog.setCanceledOnTouchOutside(false);
		if (!TextUtils.isEmpty(title)) {
			dialog.setTitle(title);
		}
		dialog.show();
	}


	public static void checkEditLightIndex(EditText editText) {
		String str = editText.getText().toString();
		if (str != null && !str.equals("")) {
			int length = str.length();
			editText.setSelection(length);
		}
	}

	public static byte[] bmpToByteArray(Bitmap bmp, final boolean needRecycle) {
		bmp = comp(bmp);
		byte[] result = null;
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, output);
			if (needRecycle) {
				bmp.recycle();
			}
			result = output.toByteArray();
			Log.w("bti", "img size  " + (result.length / 1024));
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		if (bytes.length / 1024 < 30) {
			return BitmapFactory.decodeStream(isBm);
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inSampleSize = computeSampleSize(newOpts, -1, 99 * 99);
		newOpts.inJustDecodeBounds = false;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
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

	/**
	 * 
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	public static int getAppVersionInt(Context context) {
		int i = 1;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			i = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return i;
	}

	public static String getAppVersionStr(Context context) {
		String s = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			s = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static int getScreenHeight(Context context) {
		return ((Activity) context).getResources().getDisplayMetrics().heightPixels
				- getStatusHeight((Activity) context);
	}

	public static int getScreenWidth(Context context) {
		return ((Activity) context).getResources().getDisplayMetrics().widthPixels;
	}

	public static String getCurentTime() {
		return String.valueOf(System.currentTimeMillis() / 1000L);
	}

	public static boolean isEmail(String email) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// 验证手机号码
	public static boolean phone(String str) {
		Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");

		return pattern.matcher(str).matches();
	}

	// 至少一个字,第一个是字母,后面可以没有,也可以是字母或数字
	public static boolean password(String str) {
		Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 校验银行卡卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	// 验证身份证
	public static boolean id_number(String str) {
		Pattern pattern = Pattern.compile("[0-9]{17}([0-9]|X|x)");

		return pattern.matcher(str).matches();
	}

	public static String saveTempBitmap(Context mContext, Bitmap bitmap) {
		if (bitmap != null
				&& Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			File imgFile = null;
			File tempDir = Environment.getExternalStorageDirectory();
			String path = tempDir.getAbsolutePath() + File.separator
					+ "feno_cache" + File.separator;
			File dir = new File(path);
			doMkdir(dir);
			try {
				String fileName = String.valueOf(System.currentTimeMillis());
				imgFile = new File(dir, fileName + ".jpg");
				FileOutputStream fos = new FileOutputStream(imgFile);
				bitmap.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				return imgFile.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean doMkdir(File dirFile) {
		try {
			boolean bFile = dirFile.exists();
			if (bFile == true) {
				return true;
			} else {
				bFile = dirFile.mkdirs();
				if (bFile == true) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
	}

	public static void deleteTempFiles() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File tempDir = Environment.getExternalStorageDirectory();
			String path = tempDir.getAbsolutePath() + File.separator
					+ "feno_cache" + File.separator;
			File dir = new File(path);
			delete(dir);
		}
	}

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	public static String getTempFilePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(Environment.getExternalStorageDirectory());
		sb.append(File.separator);
		sb.append("feno_cache");
		sb.append(File.separator);
		return sb.toString();
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	// 半角字符转成全角字符
	public static String StringFromatTextView(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String getHowMany(String str) {
		String s = null;
		double d = Double.valueOf(str);
		if (d < 10000) {
			if (d == 0) {
				s = "0.00";
			} else {
				if (d < 1) {
					s = d + "";
				} else {
					DecimalFormat df = new DecimalFormat("###.00");
					s = df.format(d);
				}
			}
		} else if (1000000 > d && d >= 10000d) {
			double i = (d / 10000);
			DecimalFormat df = new DecimalFormat("###.00");
			s = df.format(i);
		} else if (d >= 1000000) {
			double i = (d / 1000000);
			DecimalFormat df = new DecimalFormat("###.00");
			s = df.format(i);
		}
		return s;
	}

	public static String getDanWei(String str) {
		String s = null;
		double d = Double.valueOf(str);
		if (d < 10000) {
			s = "元";
		} else if (1000000 > d && d >= 10000d) {
			s = "万元";
		} else if (d >= 1000000) {
			s = "百万元";
		}

		return s;
	}

	// public static String[] getFormatMoney(String str) {
	// String s[] = new String[2];
	// String s1 = "";
	// String s2 = "";
	// double d = string2Double(str);
	// if (d < 10000) {
	// s1 = str;
	// s2 = "元";
	// } else if (1000000 > d && d >= 10000d) {
	// d = (double) (d / 10000);
	// s1 = String.valueOf(d);
	// s2 = "万元";
	// } else if (d >= 1000000) {
	// d = (double) (d / 10000);
	// s1 = String.valueOf(d);
	// s2 = "百万元";
	// }
	// s[0] = s1;
	// s[1] = s2;
	// return s;
	// }

	// yyyy-MM-dd HH:mm:ss
	public static String getNowDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String date = sdf.format(new Date());
		return date;
	}

	public static String getDate(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String date = sdf.format(new Date(WWUitls.string2Long(time) * 1000L));
		return date;
	}

	public static String getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String date = sdf.format(new Date(WWUitls.string2Long(str) * 1000L));
		return date;
	}

	public static boolean timeIsToday(String t) {
		boolean isToday = false;
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String today = simpleDateFormat.format(date);
		String time = simpleDateFormat.format(new Date(WWUitls.string2Long(t) * 1000L));
		System.out.println("---> today="+today+",time="+time);
		if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(today)
				&& today.equals(time)) {
			isToday = true;
		} else {
			isToday = false;
		}

		return isToday;
	}


	public static String getDay(String time) {
		String day="";
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		day = simpleDateFormat.format(new Date(WWUitls.string2Long(time) * 1000L));
		return day;
	}

	//将时间yyyy/MM/dd转换成时间戳
	public static String date2TimeStamp(String date_str,String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return String.valueOf(sdf.parse(date_str).getTime()/1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	// MD5加密
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

}
