package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;

public class CollectionRemoveActivity extends BaseActivity {
    private RelativeLayout mBackRelativeLayout;
    private RelativeLayout mDeleteVideoRelativeLayout;
    private Dialog mRemoveCollectionDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_remove);
    }

    @Override
    protected void initView() {
        mBackRelativeLayout=(RelativeLayout)findViewById(R.id.backRelativeLayout);
        mDeleteVideoRelativeLayout=(RelativeLayout)findViewById(R.id.addRelativeLayout);
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
            case R.id.addRelativeLayout:
                showDeleteDialog();
                break;
            case R.id.okTextView:
                if (null!= mRemoveCollectionDialog && mRemoveCollectionDialog.isShowing()){
                    mRemoveCollectionDialog.dismiss();
                }
                break;
            case R.id.cancelTextView:
                if (null!= mRemoveCollectionDialog && mRemoveCollectionDialog.isShowing()){
                    mRemoveCollectionDialog.dismiss();
                }
                break;
        }
    }

    private void showDeleteDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_collection_remove, null);
        ScreenUtil.initScale(dialogView);
        mRemoveCollectionDialog= new Dialog(mContext,R.style.dialog);
        mRemoveCollectionDialog.setContentView(dialogView);
        Window dialogWindow = mRemoveCollectionDialog.getWindow();
        // 获取对话框当前的参数值
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = (int) (ScreenUtil.getScreenHeight(mContext) * 0.6);
        layoutParams.width = (int) (ScreenUtil.getScreenWidth(mContext) * 0.9);
        dialogWindow.setAttributes(layoutParams);
        mRemoveCollectionDialog.show();
        initDeleteDialog(dialogView);
    }

    private void initDeleteDialog(View dialogView){
        addListener(dialogView.findViewById(R.id.okTextView));
        addListener(dialogView.findViewById(R.id.cancelTextView));
    }
}
