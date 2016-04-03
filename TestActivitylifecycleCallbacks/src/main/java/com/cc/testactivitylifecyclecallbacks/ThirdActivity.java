package com.cc.testactivitylifecyclecallbacks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 原创作者:
 * 谷哥的小弟 http://blog.csdn.net/lfdfhl
 */
public class ThirdActivity extends Activity {
    private Context mContext;
    private Button mFinishAllActivityButton;
    private Button mFinishThisActivityButton;
    private ClickListenerImpl mClickListenerImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);
        init();
    }

    private void init(){
        mContext=this;
        mClickListenerImpl=new ClickListenerImpl();
        mFinishThisActivityButton=(Button) findViewById(R.id.finishThisActivityButton);
        mFinishThisActivityButton.setOnClickListener(mClickListenerImpl);
        mFinishAllActivityButton=(Button) findViewById(R.id.finishAllActivityButton);
        mFinishAllActivityButton.setOnClickListener(mClickListenerImpl);
    }


    private class ClickListenerImpl implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.finishThisActivityButton:
                    finish();
                    break;
                case R.id.finishAllActivityButton:
                    MyApplication.getInstance().finishAllActivity();
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //System.out.println(" third Activity onResume --> "+MyApplication.getInstance().isAppRunningBackground());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //System.out.println(" third Activity onStop --> "+MyApplication.getInstance().isAppRunningBackground());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //System.out.println(" third Activity onDestroy --> "+MyApplication.getInstance().isAppRunningBackground());
    }


}
