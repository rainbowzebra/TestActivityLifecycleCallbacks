package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.ToastUtil;


public abstract class BaseActivity extends Activity implements View.OnClickListener{
    public Context mContext;
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mContext=this;
        ScreenUtil.initScale(findView(android.R.id.content));
        initView();
        initListener();
        initData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtil.setScale(this);
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public void showToast(String msg) {
        ToastUtil.showToast(this, msg);
    }

    public BaseActivity addListener(View v) {
        v.setOnClickListener(this);
        return this;
    }

    public BaseActivity addListeners(View... views) {
        for (View view : views) {
            addListener(view);
        }
        return this;
    }

    //对于View.OnClickListener的实现
    @Override
    public void onClick(View v) {

    }


    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();
}
