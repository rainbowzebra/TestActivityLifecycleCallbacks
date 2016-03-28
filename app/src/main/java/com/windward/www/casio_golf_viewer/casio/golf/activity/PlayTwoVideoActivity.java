package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class PlayTwoVideoActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mCompareRelativeLayout;
    private RelativeLayout mEditRelativeLayout;
    private RelativeLayout mSelectRelativeLayout;
    private ImageView mCompareImageView;
    private boolean isImageViewClick=true;
    private Dialog mTipsDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_two_video);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout= (RelativeLayout) findViewById(R.id.backRelativeLayout);
        mCompareRelativeLayout= (RelativeLayout) findViewById(R.id.compareRelativeLayout);
        mEditRelativeLayout= (RelativeLayout) findViewById(R.id.editRelativeLayout);
        mSelectRelativeLayout= (RelativeLayout) findViewById(R.id.selectRelativeLayout);
        mCompareImageView=(ImageView)findViewById(R.id.compareImageView);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mCompareRelativeLayout);
        addListener(mEditRelativeLayout);
        addListener(mSelectRelativeLayout);
        addListener(mCompareImageView);
    }

    @Override
    protected void initData() {
        showAddDialog();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.backRelativeLayout:
                System.out.println("-----> back ");
                finish();
                break;
            case R.id.compareRelativeLayout:
                if (isImageViewClick){
                    mCompareImageView.setImageResource(R.drawable.ic_ichi_ble);
                    isImageViewClick=false;
                }else {
                    mCompareImageView.setImageResource(R.drawable.ic_ichi_gr);
                    isImageViewClick=true;
                }
                System.out.println("-----> compare ");
                break;
            case R.id.editRelativeLayout:
                System.out.println("-----> edit ");
                break;
            case R.id.selectRelativeLayout:
                System.out.println("-----> select ");
                break;
            case R.id.okTextView:
                mTipsDialog.dismiss();
                break;

        }
    }


    private void showAddDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_tips_add, null);
        ScreenUtil.initScale(dialogView);
        mTipsDialog = new Dialog(mContext,R.style.dialog);
        mTipsDialog.setContentView(dialogView);
        Window dialogWindow = mTipsDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.3);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.7);
        dialogWindow.setAttributes(layoutParams);
        mTipsDialog.show();
        addListener(dialogView.findViewById(R.id.okTextView));
    }


}
