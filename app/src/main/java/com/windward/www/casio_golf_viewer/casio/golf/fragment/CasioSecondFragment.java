/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.windward.www.casio_golf_viewer.casio.golf.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.activity.ChooseTwoVideoActivity;
import com.windward.www.casio_golf_viewer.casio.golf.activity.PlayTwoVideoActivity;
import com.windward.www.casio_golf_viewer.casio.golf.activity.PlayerBaseActivity;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.TwoVideosAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.VideoUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CasioSecondFragment extends Fragment {
	private int position;
	private static Context mContext;
	private static final String POSITION = "position";
	private ImageView mAddImageView;
	private ClickListenerImpl mClickListenerImpl;
	private ListView mListView;
	private TwoVideosAdapter mAdapter;
	private ItemClickListenerImpl mItemClickListenerImpl;
	private List<ArrayList<ListItemInfo>> mList;
	private List<ArrayList<ListItemInfo>> mDeleteList;
	private final int REQUEST_CODE=9527;
	private final int RESULT_CODE=9528;

	public static CasioSecondFragment getFragment(int position, Context context) {
		mContext=context;
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION, position);
		CasioSecondFragment casioFirstFragment = new CasioSecondFragment();
		//setArguments()是个好东西,涉及到屏幕旋转
		casioFirstFragment.setArguments(bundle);
		return casioFirstFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		View contentView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_second,null);
		ScreenUtil.initScale(contentView);
		contentView.setLayoutParams(layoutParams);
		initSecondFragmentViews(contentView);
		return contentView;

	}

	private void initSecondFragmentViews(View view){
		if(null!=view){
			mClickListenerImpl=new ClickListenerImpl();
			mAddImageView=(ImageView)view.findViewById(R.id.addImageView);
			mAddImageView.setOnClickListener(mClickListenerImpl);

			mListView= (ListView) view.findViewById(R.id.listView);
			mAdapter=new TwoVideosAdapter(mContext);
			mItemClickListenerImpl=new ItemClickListenerImpl();
			mListView.setOnItemClickListener(mItemClickListenerImpl);
		}
	}


	private class ItemClickListenerImpl implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ArrayList<ListItemInfo> videosArrayList= mList.get(position);
			//跳转到Activity
			if (null != mList && videosArrayList.size() > 0) {
				Intent intent = new Intent(mContext, PlayTwoVideoActivity.class);
				Bundle bundle = new Bundle();
				Iterator<ListItemInfo> iterator=videosArrayList.iterator();
				ArrayList<String> playList = new ArrayList<String>();
				while (iterator.hasNext()){
					ListItemInfo itemInfo = iterator.next();
					if (itemInfo.isShowVideo()) {
						playList.add(itemInfo.getFilePath());
					}
				}
				bundle.putStringArrayList("key_playlist", playList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}



	private class ClickListenerImpl implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.addImageView:
					Intent intent=new Intent(mContext, ChooseTwoVideoActivity.class);
					startActivityForResult(intent,REQUEST_CODE);
					break;
			}
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode==REQUEST_CODE&&resultCode==RESULT_CODE&&null!=data){
			mList=new ArrayList<ArrayList<ListItemInfo>>();
			ArrayList<ListItemInfo> list=new ArrayList<ListItemInfo>();
			ListItemInfo dateItemInfo;
			ListItemInfo itemInfo;
			boolean isNeedShowDate=true;

			ArrayList<String> arrayList=(ArrayList<String> )data.getSerializableExtra("selected_videos");
			Iterator<String> iterator=arrayList.iterator();
			while (iterator.hasNext()){
				   if (isNeedShowDate){
					   //添加日期(今天的日期)
					   String path=iterator.next();
					   dateItemInfo=new ListItemInfo(mContext,path);
					   dateItemInfo.setIsShowVideo(false);
					   dateItemInfo.setmTime("" + (System.currentTimeMillis() / 1000));
					   list.add(dateItemInfo);

					   //添加第一个视频
					   itemInfo=new ListItemInfo(mContext,path);
					   itemInfo.setIsShowVideo(true);
					   list.add(itemInfo);

					   isNeedShowDate=false;

				   }else {
					   //添加第二个视频
					   itemInfo=new ListItemInfo(mContext,iterator.next());
					   itemInfo.setIsShowVideo(true);
					   list.add(itemInfo);
				   }
			}

			mList.add(list);

			//以下4句为测试代码
			mList.add(list);
			mList.add(list);
			mList.add(list);
			mList.add(list);
			//以上两4为测试代码

			mAdapter.setList(mList);
			mListView.setAdapter(mAdapter);
			//保存数据
			VideoUtils.saveComparedVideosList(mList);
		}

	}
}