package com.windward.www.casio_golf_viewer.casio.golf.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class VideoViewPagerAdapter extends PagerAdapter {
    private View guide_a;
	private View guide_b;
	private View guide_c;
	private Context mContext;
	private View[]views =new View[3];
	private Dialog mDialog;

	public VideoViewPagerAdapter(Context _context,Dialog dialog) {
		this.mContext = _context;
		this.mDialog=dialog;
		initViews();
	}

	private void initViews(){
		guide_a=LayoutInflater.from(mContext).inflate(R.layout.view_video_guide_a, null);
		guide_b=LayoutInflater.from(mContext).inflate(R.layout.view_video_guide_b, null);
		guide_c=LayoutInflater.from(mContext).inflate(R.layout.view_video_guide_c, null);
		ScreenUtil.initScale(guide_a);
		ScreenUtil.initScale(guide_b);
		ScreenUtil.initScale(guide_c);
		views[0]=guide_a;
		views[1]=guide_b;
		views[2]=guide_c;
		guide_a.findViewById(R.id.closeRelativeLayout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});
		guide_b.findViewById(R.id.closeImageView).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});
		guide_c.findViewById(R.id.closeImageView).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
			}
		});

	}

	private void closeDialog(){
		if (null!=mDialog&&mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		View iv = null;
		if (position==0){
			iv=views[0];
		}
		if (position==1){
			iv=views[1];
		}
		if (position==2){
			iv=views[2];
		}

		((ViewPager) container).addView(iv);
		return iv;
	}


	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
}
