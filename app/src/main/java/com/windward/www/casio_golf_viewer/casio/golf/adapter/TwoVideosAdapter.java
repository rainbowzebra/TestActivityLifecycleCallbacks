package com.windward.www.casio_golf_viewer.casio.golf.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.WWUitls;
import java.util.ArrayList;
import java.util.List;

public class TwoVideosAdapter extends BaseAdapter {
	Context context;
	private List<ArrayList<ListItemInfo>> list;

	public TwoVideosAdapter(Context context) {
		this.context = context;
	}

	public List<ArrayList<ListItemInfo>> getList() {
		return list;
	}

	public void setList(List<ArrayList<ListItemInfo>> list) {
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
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.video_two_item, null);
			ScreenUtil.initScale(convertView);

			holder = new viewHolder();

			holder.bgRelativeLayout= (RelativeLayout) convertView.findViewById(R.id.bgRelativeLayout);
			holder.timeTextView1= (TextView) convertView.findViewById(R.id.timeTextView1);
			holder.timeTextView2= (TextView) convertView.findViewById(R.id.timeTextView2);
			holder.timeTextView3= (TextView) convertView.findViewById(R.id.timeTextView3);

			holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.editImageView1 = (ImageView) convertView.findViewById(R.id.editImageView1);
			holder.starImageView1 = (ImageView) convertView.findViewById(R.id.starImageView1);

			holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
			holder.editImageView2 = (ImageView) convertView.findViewById(R.id.editImageView2);
			holder.starImageView2 = (ImageView) convertView.findViewById(R.id.starImageView2);

			convertView.setTag(holder);

		} else {
			holder = (viewHolder) convertView.getTag();
		}


		ArrayList<ListItemInfo> itemArrayList=list.get(position);

		holder.timeTextView1.setText(WWUitls.getYearAndMonth(itemArrayList.get(0).getmTime()));
		holder.timeTextView2.setText(WWUitls.getDayTime(itemArrayList.get(0).getmTime()));
		holder.timeTextView3.setText(WWUitls.getWeek(itemArrayList.get(0).getmTime()));

		holder.imageView1.setImageBitmap(itemArrayList.get(1).getThumbnail());
		holder.imageView2.setImageBitmap(itemArrayList.get(2).getThumbnail());

		return convertView;
	}



	class viewHolder {
		RelativeLayout bgRelativeLayout;
		TextView timeTextView1;
		TextView timeTextView2;
		TextView timeTextView3;

		ImageView imageView1;
		ImageView editImageView1;
		ImageView starImageView1;

		ImageView imageView2;
		ImageView editImageView2;
		ImageView starImageView2;

	}


}
