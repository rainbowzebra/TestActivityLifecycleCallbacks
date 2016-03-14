package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.LauncherViewPagerAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class GuideActivity extends BaseActivity {
    private ViewPager mViewPager;
    private LauncherViewPagerAdapter mViewPagerAdapter;
    private ImageView[] dotImageViews;
    private PageChangeListenerImpl mPageChangeListenerImpl;
    private LinearLayout mDotsLinearLayout;
    private TextView mStartTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    @Override
    protected void initView() {
        mViewPager = findView(R.id.guide_viewpager);
        mDotsLinearLayout = findView(R.id.dotsLinearLayout);
        mViewPagerAdapter = new LauncherViewPagerAdapter(mContext);
        mPageChangeListenerImpl = new PageChangeListenerImpl();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListenerImpl);
        mStartTextView=findView(R.id.startTextView);
        initDots();
    }

    @Override
    protected void initListener() {
        addListener(mStartTextView);
    }

    @Override
    protected void initData() {

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class PageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < dotImageViews.length; i++) {
                //当前选中
                dotImageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
                if (arg0 != i) {
                    //当前未选中
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTextView:
                Intent intent=new Intent(mContext,ProtocolActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeAllViews();
        mViewPager = null;
    }


}
