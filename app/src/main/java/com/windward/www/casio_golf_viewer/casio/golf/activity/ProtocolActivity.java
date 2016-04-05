package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.windward.www.casio_golf_viewer.R;

public class ProtocolActivity extends BaseActivity {
    private TextView mAgreeTextView;
    private TextView mUnAgreeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
    }

    @Override
    protected void initView() {
        mAgreeTextView= (TextView) findViewById(R.id.agreeTextView);
        mUnAgreeTextView= (TextView) findViewById(R.id.unAgreeTextView);
    }

    @Override
    protected void initListener() {
        addListener(mAgreeTextView);
        addListener(mUnAgreeTextView);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.agreeTextView:
                Intent intent=new Intent(mContext,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.unAgreeTextView:
                Intent i=new Intent(mContext,MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }
}
