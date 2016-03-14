package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;

public class DrawLineActivity extends BaseActivity {
    //注意:该页面Title栏有一个素菜需要替换,还有垃圾箱旁边一个需要替换素材
    private RelativeLayout mBackRelativeLayout;
    private ImageView mEditImageView;
    private RelativeLayout mSaveRelativeLayout;
    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private LinearLayout mRightLinearLayout;
    private LinearLayout mLeftLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_line);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mEditImageView=(ImageView)findViewById(R.id.editImageView);
        mSaveRelativeLayout=(RelativeLayout)findViewById(R.id.saveRelativeLayout);
        mLeftImageView=(ImageView)findViewById(R.id.leftImageView);
        mRightImageView=(ImageView)findViewById(R.id.rightImageView);
        mLeftLinearLayout=(LinearLayout)findViewById(R.id.leftLinearLayout);
        mRightLinearLayout=(LinearLayout)findViewById(R.id.rightLinearLayout);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mEditImageView);
        addListener(mSaveRelativeLayout);
        addListener(mLeftImageView);
        addListener(mRightImageView);
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
            case R.id.editImageView:
                break;
            case R.id.saveRelativeLayout:
                break;
            case R.id.leftImageView:
                if(mLeftLinearLayout.getVisibility()==View.VISIBLE){
                    mLeftLinearLayout.setVisibility(View.INVISIBLE);
                }else {
                    mLeftLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rightImageView:
                if(mRightLinearLayout.getVisibility()==View.VISIBLE){
                    mRightLinearLayout.setVisibility(View.INVISIBLE);
                }else {
                    mRightLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
