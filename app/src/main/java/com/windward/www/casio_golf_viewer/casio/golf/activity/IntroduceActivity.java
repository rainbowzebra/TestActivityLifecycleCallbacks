package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.views.CollapseLinearlayoutView;

public class IntroduceActivity extends BaseActivity {
    private ImageView mBackImageView;
    private RelativeLayout mBackRelativeLayout;
    private CollapseLinearlayoutView mCollapseLinearlayoutView1;
    private CollapseLinearlayoutView mCollapseLinearlayoutView2;
    private CollapseLinearlayoutView mCollapseLinearlayoutView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
    }

    @Override
    protected void initView() {
        mBackImageView=(ImageView)findViewById(R.id.backImageView);
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mCollapseLinearlayoutView1=findView(R.id.collapseLinearlayoutView1);
        mCollapseLinearlayoutView1.setNumber("1");
        mCollapseLinearlayoutView1.setTitle("動画の再生");
        mCollapseLinearlayoutView1.setContent(R.layout.view_expand_1);

        mCollapseLinearlayoutView2=findView(R.id.collapseLinearlayoutView2);
        mCollapseLinearlayoutView2.setNumber("2");
        mCollapseLinearlayoutView2.setTitle("ラインを引く");
        mCollapseLinearlayoutView2.setContent(R.layout.view_expand_2);


        mCollapseLinearlayoutView3=findView(R.id.collapseLinearlayoutView3);
        mCollapseLinearlayoutView3.setNumber("3");
        mCollapseLinearlayoutView3.setTitle("2画面比较");
        mCollapseLinearlayoutView3.setContent(R.layout.view_expand_3);


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
