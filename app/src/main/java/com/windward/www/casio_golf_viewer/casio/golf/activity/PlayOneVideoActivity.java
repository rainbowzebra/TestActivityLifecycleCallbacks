package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.VideoViewPagerAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class PlayOneVideoActivity extends BaseActivity {
    private Intent mIntent;
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mOperateRelativeLayout;
    private ImageView mEditImageView;
    private AlertDialog mOperateDialog;
    private AlertDialog mRevolutionDialog;
    private Dialog mVideoInfoDialog;
    private Dialog mGuideDialog;
    private  Dialog mTipsDialog;
    private ViewPager mViewPager;
    private VideoViewPagerAdapter mViewPagerAdapter;
    private ImageView[] dotImageViews;
    private PageChangeListenerImpl mPageChangeListenerImpl;
    private LinearLayout mDotsLinearLayout;

    private ImageView mPlayImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mEditImageView=(ImageView)findViewById(R.id.editImageView);
        mOperateRelativeLayout =(RelativeLayout)findViewById(R.id.operateRelativeLayout);
        mPlayImageView=(ImageView)findViewById(R.id.playImageView);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mOperateRelativeLayout);
        addListener(mEditImageView);
        addListener(mPlayImageView);
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
                showTipsDialog();
                break;
            case R.id.operateRelativeLayout:
                showOperateDialog();
                break;
            case R.id.move_cut_LinearLayout:
                mIntent=new Intent(mContext,CutVideoActivity.class);
                startActivity(mIntent);
                break;
            case R.id.move_combine_LinearLayout:
                System.out.println("-----> PPT上未说明跳转");
                break;
            case R.id.move_reflect_LinearLayout:
                System.out.println("-----> 视频翻转,不用页面跳转.");
                if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                    mOperateDialog.dismiss();
                }
                System.out.println("-----> 在此测试教程.以后请注释下行代码");
                showGuideDialog();
                break;
            case R.id.move_revolution_LinearLayout:
                System.out.println("-----> 视频旋转,不用页面跳转.");
                if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                    mOperateDialog.dismiss();
                }
                showRevolutionDialog();
                break;
            case R.id.move_movtx_LinearLayout:
                System.out.println("-----> 显示视频信息,不用页面跳转.");
                if(null!=mOperateDialog&&mOperateDialog.isShowing()){
                    mOperateDialog.dismiss();
                }
                showVideoInfoDialog();
                break;
            case R.id.revolution_TextView_90:
                System.out.println("-----> 旋转90");
                if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                    mRevolutionDialog.dismiss();
                }
                break;
            case R.id.revolution_TextView_180:
                System.out.println("-----> 旋转180");
                if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                    mRevolutionDialog.dismiss();
                }
                break;
            case R.id.revolution_TextView_270:
                System.out.println("-----> 旋转270");
                if(null!=mRevolutionDialog&&mRevolutionDialog.isShowing()){
                    mRevolutionDialog.dismiss();
                }
                break;
            case R.id.closeVideoInfoRelativeLayout:
                System.out.println("-----> 关闭对话框");
                if(null!=mVideoInfoDialog&&mVideoInfoDialog.isShowing()){
                    mVideoInfoDialog.dismiss();
                }
                break;
            case R.id.okTextView:
                if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                    mTipsDialog.dismiss();
                }
                System.out.println("-----> 跳转到 ppt e14 最复杂的画面");
                break;
            case R.id.cancelTextView:
                if(null!=mTipsDialog&&mTipsDialog.isShowing()){
                    mTipsDialog.dismiss();
                }
                break;
            case R.id.playImageView:
                //播放视频
                break;
        }
    }



    //显示操作(剪切,翻转,旋转等)对话框
    private void showOperateDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_operate, null);
        ScreenUtil.initScale(dialogView);
        mOperateDialog= new AlertDialog.Builder(mContext,R.style.dialog).create();
        mOperateDialog.show();
        Window window = mOperateDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mOperateDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mOperateDialog.getWindow().setAttributes(layoutParams);
        mOperateDialog.setContentView(dialogView);
        initOperateDialogItems(dialogView);
    }

    private void initOperateDialogItems(View dialogView){
        addListener(dialogView.findViewById(R.id.move_revolution_LinearLayout));
        addListener(dialogView.findViewById(R.id.move_combine_LinearLayout));
        addListener(dialogView.findViewById(R.id.move_cut_LinearLayout));
        addListener(dialogView.findViewById(R.id.move_movtx_LinearLayout));
        addListener(dialogView.findViewById(R.id.move_reflect_LinearLayout));
    }

    //显示视频旋转(90度,180度,270度)对话框
    private void showRevolutionDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_revolution, null);
        ScreenUtil.initScale(dialogView);
        mRevolutionDialog= new AlertDialog.Builder(mContext,R.style.dialog).create();
        mRevolutionDialog.show();
        Window window = mRevolutionDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mRevolutionDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mRevolutionDialog.getWindow().setAttributes(layoutParams);
        mRevolutionDialog.setContentView(dialogView);
        initRevolutionDialogItems(dialogView);
    }

    private void initRevolutionDialogItems(View dialogView){
        addListener(dialogView.findViewById(R.id.revolution_TextView_90));
        addListener(dialogView.findViewById(R.id.revolution_TextView_180));
        addListener(dialogView.findViewById(R.id.revolution_TextView_270));
    }

    //显示Video信息
    private void showVideoInfoDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_video_info, null);
        ScreenUtil.initScale(dialogView);
        mVideoInfoDialog= new Dialog(mContext,R.style.dialog);
        mVideoInfoDialog.setContentView(dialogView);
        Window dialogWindow = mVideoInfoDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.47);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.7);
        dialogWindow.setAttributes(layoutParams);
        mVideoInfoDialog.show();
        initVideoInfoDialog(dialogView);
    }

    private void initVideoInfoDialog(View dialogView){
        addListener(dialogView.findViewById(R.id.closeVideoInfoRelativeLayout));
    }


    //提醒用户是否要编辑视频
    private void showTipsDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_video_tips, null);
        ScreenUtil.initScale(dialogView);
        mTipsDialog= new Dialog(mContext,R.style.dialog);
        mTipsDialog.setContentView(dialogView);
        Window dialogWindow = mTipsDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.52);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.9);
        dialogWindow.setAttributes(layoutParams);
        mTipsDialog.show();
        initTipsDialog(dialogView);
    }

    private void initTipsDialog(View dialogView){
        addListener(dialogView.findViewById(R.id.okTextView));
        addListener(dialogView.findViewById(R.id.cancelTextView));
    }



    //--------------> 以下代码与教程相关
    //显示ViewPager的Dialog

    //注意ViewPager第二页展示的图片是错误的,需要重新切图！！！！！
    //即view_video_guide_b中的img_g是不对的
    private void showGuideDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_video_guide, null);
        ScreenUtil.initScale(dialogView);
        mGuideDialog= new Dialog(mContext,R.style.dialog);
        mGuideDialog.setContentView(dialogView);
        Window dialogWindow = mGuideDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.47);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.8);
        dialogWindow.setAttributes(layoutParams);
        mGuideDialog.show();
        initViewPager(dialogView);
    }

    private void initViewPager(View dialogView){
        mViewPager = (ViewPager)dialogView.findViewById(R.id.guide_viewpager);
        mDotsLinearLayout = (LinearLayout)dialogView.findViewById(R.id.dotsLinearLayout);
        mViewPagerAdapter = new VideoViewPagerAdapter(mContext,mGuideDialog);
        mPageChangeListenerImpl = new PageChangeListenerImpl();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListenerImpl);
        initDots();
    }

    //初始化小圆点
    private void initDots() {
        dotImageViews = new ImageView[mViewPagerAdapter.getCount()];
        for (int i = 0; i < dotImageViews.length; i++) {
            LinearLayout layout = new LinearLayout(mContext);
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ScreenUtil.getScalePxValue(24), ScreenUtil.getScalePxValue(24)));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.guide_dot_white);
            } else {
                layout.setPadding(ScreenUtil.getScalePxValue(16), 0, 0, 0);
                imageView.setBackgroundResource(R.drawable.guide_dot_black);
            }
            dotImageViews[i] = imageView;
            layout.addView(imageView);
            mDotsLinearLayout.addView(layout);
        }
    }

    private class PageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < dotImageViews.length; i++) {
                dotImageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
                if (arg0 != i) {
                    dotImageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }
    //--------------> 以上代码与教程相关

}
