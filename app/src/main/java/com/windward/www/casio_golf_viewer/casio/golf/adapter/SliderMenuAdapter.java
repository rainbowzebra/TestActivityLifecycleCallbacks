package com.windward.www.casio_golf_viewer.casio.golf.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.entity.SliderMenuItem;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class SliderMenuAdapter extends BaseAdapter {
	private Context context;
	private List<SliderMenuItem> list;

	public SliderMenuAdapter(Context context, List<SliderMenuItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup parent) {
		ViewHolder holder;
		if (contentView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			contentView = inflater.inflate(R.layout.adapter_slidermenu,null);
			ScreenUtil.initScale(contentView);
			holder = new ViewHolder();
			holder.titleTextView = (TextView) contentView.findViewById(R.id.menu_item_title);
			holder.contentTextView = (TextView) contentView.findViewById(R.id.menu_item_content);
			contentView.setTag(holder);
		} else {
			holder = (ViewHolder) contentView.getTag();
		}
		if(!TextUtils.isEmpty(list.get(position).getTitle())){
			holder.titleTextView.setText(list.get(position).getTitle());
		}

		if(!TextUtils.isEmpty(list.get(position).getContent())){
			holder.contentTextView.setText(list.get(position).getContent());
		}

		return contentView;
	}

	class ViewHolder {
		private TextView titleTextView;
		private TextView contentTextView;
	}
}
