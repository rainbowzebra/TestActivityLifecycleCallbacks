package com.windward.www.casio_golf_viewer.casio.golf.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

@SuppressLint("NewApi")
public class CollapseLinearlayoutView extends LinearLayout {
    private TextView mNumberTextView;
    private TextView mTitleTextView;
    private RelativeLayout mContentRelativeLayout;
    //private TextView mContentTextView;
    private ImageView mArrowImageView;
    private long duration = 200;
    private Context mContext;

    public CollapseLinearlayoutView(Context context) {
        this(context, null);
    }

    public CollapseLinearlayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        LayoutInflater.from(mContext).inflate(R.layout.view_collapse_layout, this);
        initView();
    }



    private void initView() {
        mNumberTextView=(TextView)findViewById(R.id.numberTextView);
        mTitleTextView =(TextView)findViewById(R.id.titleTextView);
        mContentRelativeLayout=(RelativeLayout)findViewById(R.id.contentRelativeLayout);
        //mContentTextView =(TextView)findViewById(R.id.contentTextView);
        mArrowImageView =(ImageView)findViewById(R.id.arrowImageView);
        mArrowImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateArrow();
            }
        });

        ScreenUtil.initScale(this);

        //collapse(mContentTextView);
        collapse(mContentRelativeLayout);
    }

    public void setNumber(String number){
        if(!TextUtils.isEmpty(number)){
            mNumberTextView.setText(number);
        }
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTextView.setText(title);
        }
    }

//    public void setContent(String content){
//        if(!TextUtils.isEmpty(content)){
//            mContentTextView.setText(content);
//        }
//    }


    public void setContent(int resID){
        View view=LayoutInflater.from(mContext).inflate(resID,null);
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        mContentRelativeLayout.addView(view);
    }


    public void rotateArrow() {
        int degree = 0;
        // 反转箭头
        if (mArrowImageView.getTag() == null || mArrowImageView.getTag().equals(true)) {
            mArrowImageView.setTag(false);
            degree = -180;
            //expand(mContentTextView);
            expand(mContentRelativeLayout);
        } else {
            degree = 0;
            mArrowImageView.setTag(true);
            //collapse(mContentTextView);
            collapse(mContentRelativeLayout);
        }
        ViewPropertyAnimator.animate(mArrowImageView).setDuration(duration).rotation(degree);
    }

    // 扩展
    //http://blog.csdn.net/eclipsexys/article/details/40106953
    private void expand(final View view) {
        view.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int measuredHeight = view.getMeasuredHeight();
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = (interpolatedTime == 1) ? RelativeLayout.LayoutParams.WRAP_CONTENT : (int) (measuredHeight * interpolatedTime);
                view.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    // 折叠
    private void collapse(final View view) {
        final int measuredHeight = view.getMeasuredHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = measuredHeight - (int) (measuredHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        view.startAnimation(animation);
    }
}
