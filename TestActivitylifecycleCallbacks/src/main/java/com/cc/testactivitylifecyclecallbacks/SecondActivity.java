package com.cc.testactivitylifecyclecallbacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 原创作者:
 * 谷哥的小弟 http://blog.csdn.net/lfdfhl
 */
public class SecondActivity extends Activity {
    private Context mContext;
    private Button mOpenNewActivityButton;
    private Button mFinishThisActivityButton;
    private ClickListenerImpl mClickListenerImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        init();
    }

    private void init(){
        mContext=this;
        mClickListenerImpl=new ClickListenerImpl();
        mOpenNewActivityButton=(Button) findViewById(R.id.openNewActivityButton);
        mOpenNewActivityButton.setOnClickListener(mClickListenerImpl);
        mFinishThisActivityButton=(Button) findViewById(R.id.finishThisActivityButton);
        mFinishThisActivityButton.setOnClickListener(mClickListenerImpl);
    }


    private class ClickListenerImpl implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.openNewActivityButton:
                    Intent intent=new Intent(mContext, ThirdActivity.class);
                    startActivity(intent);
                    break;

                case R.id.finishThisActivityButton:
                    finish();
                    break;

                default:
                    break;
            }

        }

    }


}
