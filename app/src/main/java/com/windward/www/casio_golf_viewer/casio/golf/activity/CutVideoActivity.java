package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class CutVideoActivity extends BaseActivity {
    private RelativeLayout  mBackRelativeLayout;
    private RelativeLayout mCutRelativeLayout;
    private  Dialog mTipsDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_video);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mCutRelativeLayout=(RelativeLayout)findViewById(R.id.cutRelativeLayout);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mCutRelativeLayout);
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
            case R.id.cutRelativeLayout:
                showTipsDialog();
                break;
            case R.id.okTextView:
                if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                    mTipsDialog.dismiss();
                }
                System.out.println("-----> 注意页面跳转");
                break;
            case R.id.cancelTextView:
                if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                    mTipsDialog.dismiss();
                }
                break;
        }
    }


    //提醒用户是否要剪切视频
    private void showTipsDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_cut_video_tips, null);
        ScreenUtil.initScale(dialogView);
        mTipsDialog= new Dialog(mContext,R.style.dialog);
        mTipsDialog.setContentView(dialogView);
        Window dialogWindow = mTipsDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        // 高度设置为屏幕的0.7
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.55);
        // 宽度设置为屏幕的0.8
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.8);
        dialogWindow.setAttributes(layoutParams);
        mTipsDialog.show();
        initTipsDialog(dialogView);
    }
    private void initTipsDialog(View dialogView){
        addListener(dialogView.findViewById(R.id.okTextView));
        addListener(dialogView.findViewById(R.id.cancelTextView));
    }
}
