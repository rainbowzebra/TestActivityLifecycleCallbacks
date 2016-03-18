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
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.activity.PlayVideoActivity;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.VideoGridViewAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.VideoViewPagerAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.VideoUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class CasioFirstFragment extends Fragment {
	private int position;
	private Context mContext;
	private static final String POSITION = "position";
	private GridView mGridView;
	private VideoGridViewAdapter mVideoGridViewAdapter;
	private ArrayList<String> mPlayList;
	private ItemClickListenerImpl mItemClickListenerImpl;
	private ArrayList<ListItemInfo> mVideosArrayList;

	public static CasioFirstFragment getFragment(int position) {
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION, position);
		CasioFirstFragment casioFirstFragment = new CasioFirstFragment();
		//setArguments()是个好东西,涉及到屏幕旋转
		casioFirstFragment.setArguments(bundle);
		return casioFirstFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=getContext();
		position = getArguments().getInt(POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		View contentView=LayoutInflater.from(getContext()).inflate(R.layout.view_no_image_1,null);
		ScreenUtil.initScale(contentView);
		contentView.setLayoutParams(layoutParams);
		initFirstFragmentViews(contentView);
		return contentView;

	}

	private void initFirstFragmentViews(View view){
		if(null!=view){
			mGridView= (GridView) view.findViewById(R.id.gridView);
			VideoUtils videoUtils=new VideoUtils();
			mVideosArrayList=videoUtils.getVideoList(getContext());

			if(null!=mVideosArrayList&&mVideosArrayList.size()>0){
				mPlayList = new ArrayList<String>();
				mVideosArrayList=videoUtils.fixVideoArrayList(getContext(),mVideosArrayList);
				mVideoGridViewAdapter=new VideoGridViewAdapter(getContext());
				mVideoGridViewAdapter.setList(mVideosArrayList);
				mGridView.setAdapter(mVideoGridViewAdapter);
				mItemClickListenerImpl=new ItemClickListenerImpl();
				mGridView.setOnItemClickListener(mItemClickListenerImpl);
			}else {
				mGridView.setVisibility(View.INVISIBLE);
				view.findViewById(R.id.noVideoLinearLayout).setVisibility(View.VISIBLE);
			}

		}
	}


	private class ItemClickListenerImpl implements AdapterView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent=new Intent(mContext, PlayVideoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("path", mVideosArrayList.get(position).getFilePath());
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}


}