package com.windward.www.casio_golf_viewer.casio.golf.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 
 * 功能：获取电话信息 <br />
 * 需要加入权限:<br/>
 * android.permission.READ_PHONE_STATE <br/>
 * 日期：2013-1-14<br />
 * 版本：ver 1.0<br />
 *
 * @author fighter
 * @since
 */
public final class PhoneUtils {

	/**
	 * 获取手机号码
	 *
	 * @param context
	 * @return "" sim卡中没有电话号码信息 作者:fighter <br />
	 *         创建时间:2013-1-14<br />
	 *         修改时间:<br />
	 */
	public static String getTelephoneNumber(Context context) {
		String number = getTelephonyManager(context).getLine1Number();
		return null == number ? "" : number;
	}

	public static String getTelephoneNumberFilter(Context context) {
		String number = getTelephoneNumber(context);
		if (!TextUtils.isEmpty(number)) {
			if (number.startsWith("+86"))
				;
			{
				number = number.replace("+86", "");
			}
		}
		return number;
	}

	/**
	 * 获取手机SIME码
	 *
	 * @param context
	 * @return 作者:fighter <br />
	 *         创建时间:2013-1-14<br />
	 *         修改时间:<br />
	 */
	public static String getSIME(Context context) {
		return getTelephonyManager(context).getSimSerialNumber();
	}

	public static String getIMEI(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				"DEVICE_CONFIG", Context.MODE_PRIVATE);
		String deviceId = preferences.getString("device_id", "");
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = getDeviceId(context);
			Editor editor = preferences.edit();
			editor.putString("device_id", deviceId);
			editor.commit();
		}

		return deviceId;

		// String deviceId = getTelephonyManager(context).getDeviceId();
		// if (TextUtils.isEmpty(deviceId)) {
		// deviceId = getAndroidId(context);
		// }
		//
		// if (TextUtils.isEmpty(deviceId)) {
		// deviceId = getPhoneModel();
		// }
		//
		// if (TextUtils.isEmpty(deviceId)) {
		// deviceId = "android_no_DeviceId";
		// }
		//
		// return MD5Utils.encodeByMD5(deviceId);
	}

	public static String getDeviceId(Context context) {

		String deviceId = new String();
		// imei
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();

		// Pseudo-Unique ID
		String m_szDevIDShort = "35" +

		Build.BOARD.length() % 10 + Build.BRAND.length() % 10
				+ Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
				+ Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
				+ Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
				+ Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
				+ Build.TAGS.length() % 10 + Build.TYPE.length() % 10
				+ Build.USER.length() % 10; // 13 digits

		// The Android ID
		String m_szAndroidID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);

		// The WLAN MAC Address string
		String m_szWLANMAC = "";
		try {
			WifiManager wm = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		} catch (Exception e) {

		}

		// The BT MAC Address string
		String m_szBTMAC = "";
		try {
			BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth
														// adapter
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			m_szBTMAC = m_BluetoothAdapter.getAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String m_szLongID = szImei + m_szDevIDShort + m_szAndroidID
				+ m_szWLANMAC + m_szBTMAC;
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
		// get md5 bytes
		byte p_md5Data[] = m.digest();
		// create a hex string
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			// if it is a single digit, make sure it have 0 in front (proper
			// padding)
			if (b <= 0xF)
				deviceId += "0";
			// add number to string
			deviceId += Integer.toHexString(b);
		} // hex string to uppercase
		deviceId = deviceId.toUpperCase();

		return deviceId;
	}

	public static String getAndroidId(Context context) {
		String android_id = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return android_id;
	}

	/**
	 * 获取手机型号
	 *
	 * @return 作者:fighter <br />
	 *         创建时间:2013-1-14<br />
	 *         修改时间:<br />
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取系统的版本号(如：4.0.5)
	 *
	 * @return
	 */
	public static String getAppOs() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取app 的版本
	 * 
	 * @return
	 */
	public static String getAppVer(Context context) {
		String ver = "1.0";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			ver = info.versionName;
		} catch (Exception e) {
		}
		return ver;
	}

	public static TelephonyManager getTelephonyManager(Context context) {
		return (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}
}
