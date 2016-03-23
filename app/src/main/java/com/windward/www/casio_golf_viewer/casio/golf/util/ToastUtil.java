package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.windward.www.casio_golf_viewer.R;

public class ToastUtil {
	static Toast mToast;

	public static void showToast(Context context, String msg) {
		if (TextUtils.isEmpty(msg)) {
			return;
		}

		if (mToast != null)
			mToast.cancel();
		
		View view = View.inflate(context, R.layout.toast_layout, null);
		ScreenUtil.initScale(view);
		TextView textView = (TextView) view.findViewById(R.id.msg_text);
		textView.setText(msg);

		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		mToast = toast;
		mToast.show();
	}
}
