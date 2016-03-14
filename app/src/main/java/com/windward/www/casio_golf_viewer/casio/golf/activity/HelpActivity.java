package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;

public class HelpActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
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
