package com.windward.www.casio_golf_viewer.casio.golf.adapter;

/**
 * Created by yy on 2016/3/3.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import java.util.ArrayList;
import java.util.List;


public class VideoGridViewAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ListItemInfo> list;

    public List<ListItemInfo> getList() {
        return list;
    }

    public void setList(ArrayList<ListItemInfo> list) {
        this.list = list;
    }

    public VideoGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }

    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holder;
        if (convertView == null) {
            holder = new HolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.video_gridview_item, null);
            ScreenUtil.initScale(convertView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        holder.imageView.setImageBitmap(list.get(position).getThumbnail());
        return convertView;
    }

    class HolderView {
        ImageView imageView;
    }
}

