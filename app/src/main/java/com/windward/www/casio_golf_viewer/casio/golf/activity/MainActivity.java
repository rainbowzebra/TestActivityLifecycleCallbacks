package com.windward.www.casio_golf_viewer.casio.golf.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import com.windward.www.casio_golf_viewer.R;
import com.windward.www.casio_golf_viewer.casio.golf.adapter.SliderMenuAdapter;
import com.windward.www.casio_golf_viewer.casio.golf.entity.SliderMenuItem;
import com.windward.www.casio_golf_viewer.casio.golf.fragment.CasioSecondFragment;
import com.windward.www.casio_golf_viewer.casio.golf.util.APPUtils;
import com.windward.www.casio_golf_viewer.casio.golf.util.PagerSlidingTabStrip;
import com.windward.www.casio_golf_viewer.casio.golf.util.ScreenUtil;
import com.windward.www.casio_golf_viewer.casio.golf.fragment.CasioFirstFragment;

import java.util.ArrayList;
//http://stackoverflow.com/questions/26533510/android-toolbar-center-title-and-custom-font
//http://www.jianshu.com/p/658ca285aa5a
//http://blog.csdn.net/bbld_/article/details/41439715
//http://www.cnblogs.com/PengLee/p/4198563.html
//http://blog.csdn.net/tfslovexizi/article/details/42583303 修改图标
//http://www.kwstu.com/ArticleView/kwstu_201408210609378926 修改图标

/**
 * 1 在initToolBar()修改了ActionBar的返回图标
 * 2 在main.xml中修改了另外两个图标
 * 3 在toolbar中设定了ActionBar的title
 */

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ShareActionProvider mShareActionProvider;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private ListView mListView;
    private SliderMenuAdapter mSliderMenuAdapter;
    private PopupWindow popupWindow;
    private View popupWindowView;
    private TextView mPopuWindow_textView1;
    private AlertDialog mFirstEditDialog;
    private Dialog mFirstCategoryDialog;
    private ClickListenerImpl mClickListenerImpl;
    private Intent mIntent;
    private int currentPosition=0;
    private Dialog mSecondCategoryDialog;
    private AlertDialog mSecondEditDialog;
    private View mSelcetedImageView;
    private RelativeLayout mCategoryRelativeLayout1;
    private RelativeLayout mCategoryRelativeLayout2;
    private RelativeLayout mCategoryRelativeLayout3;
    private RelativeLayout mCategoryRelativeLayout4;
    private RelativeLayout mCategoryRelativeLayout5;
    private RelativeLayout mCategoryRelativeLayout6;
    private RelativeLayout mCategoryRelativeLayout7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        mClickListenerImpl=new ClickListenerImpl();
        initView();
        initListener();
        initData();
    }


    protected void initView() {
        initToolBar();
    }


    protected void initListener() {
    }


    protected void initData() {
//        ContentResolver contentResolver=mContext.getContentResolver();
//        VideoUtils.getVideoThumbnail(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    }

//http://blog.csdn.net/chencehnggq/article/details/21492417 back
    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ScreenUtil.initScale(mToolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ScreenUtil.initScale(mDrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);

        //修改返回图标
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_gr);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                currentPosition=arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        initSliderMeunListView();
        initPagerSlidingTabStrip();
    }

    //初始化侧滑菜单
    private void initSliderMeunListView(){
        mListView=(ListView)findViewById(R.id.sliderMenuListView);
        ArrayList<SliderMenuItem> arrayList=new ArrayList<>();
        String[][] menus = {
                { "ヘルプ", "" },
                { "機能紹介", "" },
                { "ソフトウェア使用許諾契約", "" ,},
                { "ライセンス情報", "" ,},
                { "バージョン", APPUtils.getAppVersionName(mContext) ,}
        };

        for (int i = 0; i < menus.length; i++) {
                arrayList.add(new SliderMenuItem(menus[i][0],menus[i][1]));
        }
        mSliderMenuAdapter=new SliderMenuAdapter(mContext,arrayList);
        mListView.setAdapter(mSliderMenuAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mIntent = new Intent(mContext, HelpActivity.class);
                        startActivity(mIntent);
                        break;
                    case 1:
                        mIntent = new Intent(mContext, IntroduceActivity.class);
                        startActivity(mIntent);
                        break;
                    case 2:
                        mIntent = new Intent(mContext, LicenseActivity.class);
                        startActivity(mIntent);
                        break;
                    case 3:
                        mIntent = new Intent(mContext, InformationActivity.class);
                        startActivity(mIntent);
                        break;
                    case 4:
                        break;
                    default:

                        break;
                }
                closeDrawerLayout();
            }
        });
    }

    //关闭抽屉
    private void closeDrawerLayout(){
        if (mDrawerLayout!=null&&mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //打开抽屉
    private void openDrawerLayout(){
        if (mDrawerLayout!=null&&mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * 设置PagerSlidingTabStrip
     */
    private void initPagerSlidingTabStrip() {
        // 设置Tab是自动填充满屏幕
        mPagerSlidingTabStrip.setShouldExpand(true);
        // 底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#426E50"));
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#F7F7F7"));
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(Color.parseColor("#1C1C1C"));
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(Color.parseColor("#949494"));
        // 文字的大小
        mPagerSlidingTabStrip.setTextSize(ScreenUtil.getScalePxValue(45));
        // 取消点击Tab时的背景色
        mPagerSlidingTabStrip.setTabBackground(0);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mDrawerLayout!=null&&mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
           closeDrawerLayout();
        }else{
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (currentPosition==0){
                    showFirstEditDialog();
                }

                if (currentPosition==1){
                    showSecondEditDialog();
                }
                break;
            case R.id.action_share:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showFirstEditDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_introduce, null);
        ScreenUtil.initScale(dialogView);
        mFirstEditDialog = new AlertDialog.Builder(mContext,R.style.dialog).create();
        mFirstEditDialog.show();
        Window window = mFirstEditDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mFirstEditDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mFirstEditDialog.getWindow().setAttributes(layoutParams);
        mFirstEditDialog.setContentView(dialogView);
        initFirstEditDialog(dialogView);
    }


    private void initFirstEditDialog(View view){
        view.findViewById(R.id.introduce_dialog_textView1).setOnClickListener(mClickListenerImpl);
        view.findViewById(R.id.introduce_dialog_textView2).setOnClickListener(mClickListenerImpl);
        view.findViewById(R.id.introduce_dialog_textView3).setOnClickListener(mClickListenerImpl);
        view.findViewById(R.id.introduce_dialog_textView4).setOnClickListener(mClickListenerImpl);
    }


    private void showFirstCategoryDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_category, null);
        ScreenUtil.initScale(dialogView);
        mFirstCategoryDialog = new AlertDialog.Builder(mContext,R.style.dialog).create();
        mFirstCategoryDialog.show();
        Window window = mFirstCategoryDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mFirstEditDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mFirstCategoryDialog.getWindow().setAttributes(layoutParams);
        mFirstCategoryDialog.setContentView(dialogView);
        initFirstCategoryDialog(dialogView);
    }

    private void initFirstCategoryDialog(View view){
        mSelcetedImageView =view.findViewById(R.id.doneImageView1);
        mCategoryRelativeLayout1 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout1);
        mCategoryRelativeLayout1 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout2 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout2);
        mCategoryRelativeLayout2 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout3 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout3);
        mCategoryRelativeLayout3 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout4 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout4);
        mCategoryRelativeLayout4 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout5 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout5);
        mCategoryRelativeLayout5 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout6 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout6);
        mCategoryRelativeLayout6 .setOnClickListener(mClickListenerImpl);
        mCategoryRelativeLayout7 =(RelativeLayout)view.findViewById(R.id.category_RelativeLayout7);
        mCategoryRelativeLayout7 .setOnClickListener(mClickListenerImpl);
    }




    private void showSecondEditDialog(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        ScreenUtil.initScale(dialogView);
        mSecondEditDialog = new AlertDialog.Builder(mContext,R.style.dialog).create();
        mSecondEditDialog.show();
        Window window = mSecondEditDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mSecondEditDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width = ScreenUtil.getScreenWidth(mContext);
        mSecondEditDialog.getWindow().setAttributes(layoutParams);
        mSecondEditDialog.setContentView(dialogView);
        initSecondEditDialog(dialogView);
    }

    private void initSecondEditDialog(View view){
        mClickListenerImpl=new ClickListenerImpl();
        view.findViewById(R.id.edit_dialog_textView1).setOnClickListener(mClickListenerImpl);
        view.findViewById(R.id.edit_dialog_textView2).setOnClickListener(mClickListenerImpl);
    }



    private void closeSecondEditDialog(){
        if(null!= mSecondEditDialog && mSecondEditDialog.isShowing()){
            mSecondEditDialog.dismiss();
        }
    }

    private void closeFirstdEditDialog(){
        if(null!= mFirstEditDialog && mFirstEditDialog.isShowing()){
            mFirstEditDialog.dismiss();
        }
    }

    private void showSecondCategoryDialog(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_category, null);
        ScreenUtil.initScale(dialogView);
        mSecondCategoryDialog = new AlertDialog.Builder(mContext,R.style.dialog).create();
        mSecondCategoryDialog.show();
        Window window = mSecondCategoryDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //去掉Dialog本身的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = mSecondEditDialog.getWindow().getAttributes();
        //设置宽度为屏幕宽度
        layoutParams.width =ScreenUtil.getScreenWidth(mContext);
        mSecondCategoryDialog.getWindow().setAttributes(layoutParams);
        mSecondCategoryDialog.setContentView(dialogView);
        initSecondCategoryDialog(dialogView);
    }

    private void initSecondCategoryDialog(View view){

    }


    private class ClickListenerImpl implements OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.introduce_dialog_textView1:
                    System.out.println("排序");
                    showFirstCategoryDialog();
                    closeFirstdEditDialog();
                    break;
                case R.id.introduce_dialog_textView2:
                    System.out.println("删除视频");
                    mIntent=new Intent(mContext,DeleteOneVideoActivity.class);
                    startActivity(mIntent);
                    closeFirstdEditDialog();
                    break;
                case R.id.introduce_dialog_textView3:
                    System.out.println("添加收藏");
                    mIntent=new Intent(mContext,CollectionAddActivity.class);
                    startActivity(mIntent);
                    closeFirstdEditDialog();
                    break;
                case R.id.introduce_dialog_textView4:
                    System.out.println("删除收藏");
                    mIntent=new Intent(mContext,CollectionRemoveActivity.class);
                    startActivity(mIntent);
                    closeFirstdEditDialog();
                    break;
                case R.id.edit_dialog_textView1:
                    closeSecondEditDialog();
                    showSecondCategoryDialog();
                    break;
                case R.id.edit_dialog_textView2:
                    System.out.println("删除视频");
                    mIntent=new Intent(mContext,DeleteTwoVideoActivity.class);
                    startActivity(mIntent);
                    closeSecondEditDialog();
                    break;
                case R.id.category_RelativeLayout1:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout1.findViewById(R.id.doneImageView1);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout2:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout2.findViewById(R.id.doneImageView2);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout3:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout3.findViewById(R.id.doneImageView3);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout4:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout4.findViewById(R.id.doneImageView4);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout5:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout5.findViewById(R.id.doneImageView5);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout6:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout6.findViewById(R.id.doneImageView6);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
                case R.id.category_RelativeLayout7:
                    mSelcetedImageView.setVisibility(View.INVISIBLE);
                    mSelcetedImageView = mCategoryRelativeLayout7.findViewById(R.id.doneImageView7);
                    mSelcetedImageView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void showPopuWindow(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupWindowView = inflater.inflate(R.layout.introduce_popuwindow, null);
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT,true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置PopupWindow的弹出和消失效果
        popupWindow.setAnimationStyle(R.style.popupAnimation);
        mPopuWindow_textView1=(TextView)findViewById(R.id.popuWindow_textView1);
       // popupWindow.showAtLocation(mPopuWindow_textView1, Gravity.CENTER, 0, 0);
       // 设置layout在PopupWindow中显示的位置
        popupWindow.showAtLocation(findViewById(R.id.linearLayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = { "1 画面再生", "2 画面再生"};
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return CasioFirstFragment.getFragment(position);
            }

            if(position==1){
                return CasioSecondFragment.getFragment(position,mContext);
            }
            return null;
        }

    }



}
