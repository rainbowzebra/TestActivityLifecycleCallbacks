package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Dialog;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;


public class LoadingActivity extends BaseActivity {
    private Dialog mDialog;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    private TextView mPercentTextView;
    public final int LOADING=9527;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
    }

    @Override
    protected void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPercentTextView=(TextView)findViewById(R.id.percentTextView);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==LOADING){
                    mPercentTextView.setText(mProgressBar.getProgress() + "%");
                }

            }
        };
        //showLoadingDialog();
        loadindData();
    }

    private void loadindData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int progressBarMax = mProgressBar.getMax();
                try {
                    while (progressBarMax != mProgressBar.getProgress()) {
                        int everyProgress = progressBarMax / 10;
                        int currentprogress = mProgressBar.getProgress();
                        mProgressBar.setProgress(currentprogress + everyProgress);
                       mHandler.sendEmptyMessage(LOADING);
                        Thread.sleep(1000);//线程睡眠一秒
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start(); //开启线程.关键结束
    }

    private void showLoadingDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading, null);
        ScreenUtil.initScale(dialogView);
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        mDialog=builder.create();
        mDialog.show();
    }
}
