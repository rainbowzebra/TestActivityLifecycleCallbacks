package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.SharedPreferencesUtil;
public class SplashActivity extends BaseActivity {
    private Handler mHandler = new SplashHandler();
    private final int SPLASH_FINISH=9527;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mHandler.sendEmptyMessageDelayed(SPLASH_FINISH, 1000*3);
    }


    private final class SplashHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SPLASH_FINISH) {
                if(TextUtils.isEmpty(SharedPreferencesUtil.getAppFirstFlag(mContext))){
                    SharedPreferencesUtil.saveAppFirstFlag(SplashActivity.this,"first");
                    Intent intent = new Intent(mContext, GuideActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        }
    }


}
