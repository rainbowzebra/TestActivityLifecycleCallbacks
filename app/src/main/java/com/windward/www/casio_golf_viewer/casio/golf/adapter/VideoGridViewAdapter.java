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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.WWUitls;

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
            holder.dateLinearLayout=(LinearLayout)convertView.findViewById(R.id.dateLinearLayout);
            holder.yearTextView=(TextView)convertView.findViewById(R.id.timeTextView1);
            holder.dayTextView=(TextView)convertView.findViewById(R.id.timeTextView2);
            holder.weekTextView=(TextView)convertView.findViewById(R.id.timeTextView3);
            holder.deleteRelativeLayout=(RelativeLayout)convertView.findViewById(R.id.deleteRelativeLayout);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        ListItemInfo item=list.get(position);
        if(item.isShowVideo()){
            holder.dateLinearLayout.setVisibility(View.INVISIBLE);
            holder.deleteRelativeLayout.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageBitmap(list.get(position).getThumbnail());
        }else {
            holder.dateLinearLayout.setVisibility(View.VISIBLE);
            holder.yearTextView.setText(WWUitls.getYearAndMonth(item.getmTime()));
            holder.dayTextView.setText(WWUitls.getDayTime(item.getmTime()));
            holder.weekTextView.setText(WWUitls.getWeek(item.getmTime()));
            holder.imageView.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    class HolderView {
        LinearLayout dateLinearLayout;
        RelativeLayout deleteRelativeLayout;
        ImageView imageView;
        TextView yearTextView;
        TextView dayTextView;
        TextView weekTextView;
    }
}

