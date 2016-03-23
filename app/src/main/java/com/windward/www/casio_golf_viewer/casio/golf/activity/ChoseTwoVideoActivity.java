package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.VideoGridViewAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ToastUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.VideoUtils;

import java.util.ArrayList;

public class ChoseTwoVideoActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mNextRelativeLayout;
    private GridView mGridView;
    private VideoGridViewAdapter mVideoGridViewAdapter;
    private ItemClickListenerImpl mItemClickListenerImpl;
    private ArrayList<ListItemInfo> mArrayList;
    private ArrayList<String> mSelectedArrayList;
    private final int RESULT_CODE=9528;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_video);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mNextRelativeLayout =(RelativeLayout)findViewById(R.id.nextRelativeLayout);
        mGridView= (GridView) findViewById(R.id.gridview);
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mNextRelativeLayout);
    }

    @Override
    protected void initData() {
        mSelectedArrayList=new ArrayList<String>();
        mItemClickListenerImpl=new ItemClickListenerImpl();
        mVideoGridViewAdapter=new VideoGridViewAdapter(mContext);
        mArrayList= VideoUtils.getFixedVideoArrayList();
        mVideoGridViewAdapter.setList(mArrayList);
        mGridView.setAdapter(mVideoGridViewAdapter);
        mGridView.setOnItemClickListener(mItemClickListenerImpl);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.backRelativeLayout:
                finish();
                break;
            case R.id.nextRelativeLayout:
                if (null != mSelectedArrayList && mSelectedArrayList.size() == 2) {
                    Intent intent = new Intent(mContext, CompareVideoActivity.class);
                    intent.putExtra("selected_videos", mSelectedArrayList);
                    setResult(RESULT_CODE, intent);
                    finish();
                } else {
                    ToastUtil.showToast(mContext, "please choose two videos");
                }
                break;
        }
    }

    private class ItemClickListenerImpl implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ListItemInfo item = mArrayList.get(position);
            if (item.isShowVideo()) {
                RelativeLayout deleteRelativeLayout = (RelativeLayout) view.findViewById(R.id.deleteRelativeLayout);
                if (deleteRelativeLayout.getVisibility() == View.VISIBLE) {
                    deleteRelativeLayout.setVisibility(View.INVISIBLE);
                    if (null != mSelectedArrayList && mSelectedArrayList.contains(item.getFilePath())) {
                        mSelectedArrayList.remove(item.getFilePath());
                    }
                } else {
                    if (null != mSelectedArrayList && mSelectedArrayList.size() < 2) {
                        deleteRelativeLayout.setVisibility(View.VISIBLE);
                        if (null != mSelectedArrayList && !mSelectedArrayList.contains(item.getFilePath())) {
                            mSelectedArrayList.add(item.getFilePath());
                        }
                    } else {
                        ToastUtil.showToast(mContext, "The selected videos max number is two");
                    }

                }

            }
        }
    }

}
