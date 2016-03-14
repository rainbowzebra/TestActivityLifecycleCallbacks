package com.windward.www.casio_golf_viewer.casio.golf.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.windward.www.casio_golf_viewer.R;

public class LauncherViewPagerAdapter extends PagerAdapter {

	private Context mContext;
	private int[] page = { R.drawable.a,
			R.drawable.b, R.drawable.c, R.drawable.d};

	public LauncherViewPagerAdapter(Context _context) {
		this.mContext = _context;
	}

	@Override
	public int getCount() {
		return page.length;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		View iv = LayoutInflater.from(mContext).inflate(
				R.layout.guide_pager_adapter, null);
		iv.setFocusable(true);
		ImageView layout = (ImageView) iv.findViewById(R.id.imageView);
		layout.setBackgroundResource(page[position]);

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
