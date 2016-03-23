package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.DeleteTwoVideosAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.ToastUtil;
import com.windward.www.casio_golf_viewer.casio.golf.util.VideoUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteTwoVideoActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mDeleteVideoRelativeLayout;
    private Dialog mDeleteDialog;
    private ListView mListView;
    private DeleteTwoVideosAdapter mAdapter;
    private ItemClickListenerImpl mItemClickListenerImpl;
    private List<ArrayList<ListItemInfo>> mList;
    private List<ArrayList<ListItemInfo>> mDeleteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_two_video);

    }


    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mDeleteVideoRelativeLayout=(RelativeLayout)findViewById(R.id.deleteRelativeLayout);
        mListView= (ListView) findViewById(R.id.listView);
        mAdapter=new DeleteTwoVideosAdapter(mContext);
        mList= VideoUtils.getComparedVideosList();
        mAdapter.setList(mList);
        mListView.setAdapter(mAdapter);
        mItemClickListenerImpl=new ItemClickListenerImpl();
        mListView.setOnItemClickListener(mItemClickListenerImpl);

        mDeleteList=new ArrayList<ArrayList<ListItemInfo>>();
    }

    @Override
    protected void initListener() {
        addListener(mBackRelativeLayout);
        addListener(mDeleteVideoRelativeLayout);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.backRelativeLayout:
                finish();
                break;
            case R.id.deleteRelativeLayout:
                if(null!=mDeleteList&&mDeleteList.size()>0){
                    System.out.println("--------> mDeleteList.size()="+mDeleteList.size());
                    showDeleteDialog();
                }else{
                    ToastUtil.showToast(mContext,"please choose videos");
                }

                break;
            case R.id.okTextView:
                if (null!=mDeleteDialog&&mDeleteDialog.isShowing()){
                    mDeleteDialog.dismiss();
                    Iterator<ArrayList<ListItemInfo>> iterator=mDeleteList.iterator();
                    while (iterator.hasNext()){
                        ArrayList<ListItemInfo> item=iterator.next();
                        if (mList.contains(item)){
                            mList.remove(item);
                        }
                    }

                    VideoUtils.saveComparedVideosList(mList);
                    mList= VideoUtils.getComparedVideosList();
                    mAdapter.setList(mList);
                    mListView.setAdapter(mAdapter);
                    //mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.cancelTextView:
                if (null!=mDeleteDialog&&mDeleteDialog.isShowing()){
                    mDeleteDialog.dismiss();
                }
                break;
        }
    }


    private class ItemClickListenerImpl implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<ListItemInfo> item= mList.get(position);
            if (view.findViewById(R.id.choseImage).getVisibility()==View.VISIBLE){
                if (!mDeleteList.contains(item)){
                    mDeleteList.add(item);
                    System.out.println("--------> add mDeleteList.size()=" + mDeleteList.size());
                }
                view.findViewById(R.id.choseImage).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.deleteRelativeLayout).setVisibility(View.VISIBLE);
            }else {
                if (mDeleteList.contains(item)){
                    mDeleteList.remove(item);
                    System.out.println("--------> remove mDeleteList.size()=" + mDeleteList.size());
                }
                view.findViewById(R.id.choseImage).setVisibility(View.VISIBLE);
                view.findViewById(R.id.deleteRelativeLayout).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showDeleteDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_delete_video, null);
        ScreenUtil.initScale(dialogView);
        mDeleteDialog= new Dialog(mContext,R.style.dialog);
        mDeleteDialog.setContentView(dialogView);
        Window dialogWindow = mDeleteDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.6);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.9);
        dialogWindow.setAttributes(layoutParams);
        mDeleteDialog.show();
        initDeleteDialog(dialogView);
    }

    private void initDeleteDialog(View dialogView){
        addListener(dialogView.findViewById(R.id.okTextView));
        addListener(dialogView.findViewById(R.id.cancelTextView));
    }
}
