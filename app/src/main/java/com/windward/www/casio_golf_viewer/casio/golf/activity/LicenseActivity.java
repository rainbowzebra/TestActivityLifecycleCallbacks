package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;

public class LicenseActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
    }

    @Override
    protected void initListener() {
        mBackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }
}
