package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;

public class ChoseTwoVideoActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mNextRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_video);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mNextRelativeLayout =(RelativeLayout)findViewById(R.id.nextRelativeLayout);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mNextRelativeLayout);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.backRelativeLayout:
                finish();
                break;
            case R.id.nextRelativeLayout:
                Intent intent=new Intent(mContext,CompareVideoActivity.class);
                startActivity(intent);
                break;
        }
    }

}
