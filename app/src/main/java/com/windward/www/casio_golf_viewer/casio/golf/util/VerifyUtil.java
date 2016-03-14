package com.windward.www.casio_golf_viewer.casio.golf.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

public class VerifyUtil {
	public static boolean check(TextView[][] views) {
		boolean re = true;
		try {
			for (TextView[] view : views) {
				if (TextUtils.isEmpty(view[0].getText())) {
					view[1].setTextColor(Color.RED);
					re = false;
				} else {
					view[1].setTextColor(Color.BLACK);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static boolean checkMinLen(TextView titleView, TextView valueView,
			int minLen) {
		boolean re = false;
		try {
			if (valueView.getText().length() < minLen) {
				if (titleView != null)
					titleView.setTextColor(Color.RED);
				re = false;
			} else {
				if (titleView != null)
					titleView.setTextColor(Color.BLACK);
				re = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static boolean checkMatchLen(String str, int len) {
		boolean re = false;
		try {
			if (str.length() != len) {
				re = false;
			} else {
				re = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static boolean checkMatchLen(TextView titleView, TextView valueView,
			int minLen) {
		boolean re = false;
		try {
			if (valueView.getText().length() != minLen) {
				titleView.setTextColor(Color.RED);
				re = false;
			} else {
				titleView.setTextColor(Color.BLACK);
				re = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}



	public static boolean isMail(String str) {

		boolean re = false;
		try {
			Pattern pattern = Pattern
					.compile("^([a-zA-Z0-9_+\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
			Matcher matcher = pattern.matcher(str);
			re = matcher.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return re;
	}

//	public static boolean isHullWidth(TextView titleView, TextView valueView) {
//		String str = valueView.getText().toString();
//		boolean b = false;
//		if (!Util.isEmpty(str)) {
//			Pattern pattern = Pattern.compile("^[ァ-タダ-ヶー　\\s]*$");
//			Matcher matcher = pattern.matcher(str);
//			b = matcher.matches();
//			// if (str != null) {
//			// for (int i = 0; i < str.length(); i++) {
//			// char c = str.charAt(i);
//			// if ((c <= '\u007e') || // 英数字
//			// (c == '\u00a5') || // \記号
//			// (c == '\u203e') || // ~記号
//			// (c >= '\uff61' && c <= '\uff9f') // 半角カナ
//			// ) {
//			// b = false;// 半角
//			// break;
//			// } else {
//			// b = true;
//			//
//			// }
//			// }
//			// }
//			if (b) {
//				titleView.setTextColor(Color.BLACK);
//			} else {
//				titleView.setTextColor(Color.RED);
//			}
//		}
//		return b;
//	}

	/*
	 * public static boolean isHullWidth(TextView titleView, TextView valueView)
	 * { String str = valueView.getText().toString(); boolean b = false; if (str
	 * != null) { for (int i = 0; i < str.length(); i++) { char c =
	 * str.charAt(i); if ((c <= '\u007e') || // 英数字 (c == '\u00a5') || // \記号 (c
	 * == '\u203e') || // ~記号 (c >= '\uff61' && c <= '\uff9f') // 半角カナ ) { b =
	 * false;// 半角 break; } else { b = true; } } } return b; }
	 */

	public static boolean isHalfWidth(String str) {
		boolean b = false;
		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if ((c <= '\u007e') || // 英数字
						(c == '\u00a5') || // \記号
						(c == '\u203e') || // ~記号
						(c >= '\uff61' && c <= '\uff9f') // 半角カナ
				) {
					b = true;// 半角
				} else {
					b = false;
					break;
				}
			}
		}
		return b;
	}

//	public static InputFilter getBqFilter() {
//		InputFilter emojiFilter = new InputFilter() {
//
//			Pattern emoji = Pattern
//					.compile(
//
//							"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
//
//							Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
//
//			@Override
//			public CharSequence filter(CharSequence source, int start, int end,
//					Spanned dest, int dstart,
//
//					int dend) {
//
//				Matcher emojiMatcher = emoji.matcher(source);
//
//				if (emojiMatcher.find()) {
//
//					return "";
//
//				}
//				return null;
//
//			}
//		};
//		return emojiFilter;
//	}
}
