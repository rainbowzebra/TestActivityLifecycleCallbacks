package com.cc.testactivitylifecyclecallbacks;

import java.util.LinkedList;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
/**
 * 原创作者:
 * 谷哥的小弟 http://blog.csdn.net/lfdfhl
 *
 * Demo示例:
 * ActivityLifecycleCallbacks使用示例
 *
 * ActivityLifecycleCallbacks用于对应用中Activity的生命周期的追踪和回调
 *
 * 使用ActivityLifecycleCallbacks可实现:
 * 1 判断App是否在后台运行
 * 2 关闭该应用所有Activity
 *
 *
 * (1)判断App是否在后台运行
 * 在该实现中采用activityCounter记录Activity的个数.
 * 通过对于activityCounter是否为0判断当前APP是否在前台运行.
 *
 * 从ActivityLifecycleCallbacks的实现方法中我们的惯性思维是:
 * 在ActivityLifecycleCallbacks的onActivityResumed()中activityCounter+1.
 * 在onActivityPaused()中activityCounter-1,
 * 但是这么对么？
 *
 * 先看一下界面跳转时两个Activity的生命周期
 * 当从Activity A跳转到Activity B时,两个Activity的生命周期如下:
 * A.onPause()->B.onCreate()->B.onStart()-> B.onResume()-> A.onStop()
 *
 * 假若按照上述方式那么:
 * 在A启动后activityCounter=1;
 * 当从A跳转到B时在调用A.onPause()时执行:activityCounter-1=0;
 * 即该APP运行在后台.
 * 这当然是错的,两个界面切换的过程中APP当然是运行在前台的。
 *
 * 如下修改:
 * 在onActivityStarted()中对于activityCounter+1
 * 在onActivityStopped()中对于activityCounter-1
 * 小结:注意Activity跳转时的生命周期
 *
 * (2)关闭该应用所有Activity
 *    2.1 利用LinkedList<Activity>管理应用中的界面
 *    2.2 在ActivityLifecycleCallbacks的onActivityCreated()
 *        将Activity添加到LinkedList中
 *        在ActivityLifecycleCallbacks的onActivityDestroyed()
 *        将Activity从LinkedList中移除
 *    2.3 关闭应用时销毁LinkedList中所有Activity
 *
 * 备注说明:
 * 1 ActivityLifecycleCallbacks是在API 14及其以上才有的
 * 2 在Android4.0以下可以在BaseActivity中按照该思路实现
 *
 */
public class MyApplication extends Application {
    private static int activityCounter=0;
    private static MyApplication mApplicationInstance;
    private static LinkedList<Activity> mActivityLinkedList;
    private ActivityLifecycleCallbacksImpl mActivityLifecycleCallbacksImpl;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInstance=new MyApplication();
        mActivityLinkedList=new LinkedList<Activity>();
        mActivityLifecycleCallbacksImpl=new ActivityLifecycleCallbacksImpl();
        this.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacksImpl);
    }

    public static MyApplication getInstance() {
        if (null==mApplicationInstance) {
            mApplicationInstance=new MyApplication();
        }
        return mApplicationInstance;
    }

    //判断App是否在后台运行
    public boolean isAppRunningBackground(){
        boolean flag=false;
        if(activityCounter==0){
            flag=true;
        }
        return flag;
    }


    //退出应用
    public void finishAllActivity(){
        //unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacksImpl);
        System.out.println("--> mActivityLinkedList.size()="+mActivityLinkedList.size());
        if(null!=mActivityLinkedList){
            for(Activity activity:mActivityLinkedList){
                if(null!=activity){
                    activity.finish();
                }
            }
        }
    }


    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks{
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            System.out.println("onActivityCreated --> "+activity.getClass().getName());
            if (null!=mActivityLinkedList&&null!=activity) {
                mActivityLinkedList.addFirst(activity);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCounter++;
            System.out.println("onActivityStarted --> "+activity.getClass().getName()+",activityCounter="+activityCounter);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            System.out.println("onActivityResumed --> "+activity.getClass().getName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            System.out.println("onActivityPaused --> "+activity.getClass().getName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCounter--;
            System.out.println("onActivityStopped --> "+activity.getClass().getName()+",activityCounter="+activityCounter);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            System.out.println("onActivitySaveInstanceState --> "+activity.getClass().getName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            System.out.println("onActivityDestroyed --> "+activity.getClass().getName());
            if (null!=mActivityLinkedList&&null!=activity) {
                if (mActivityLinkedList.contains(activity)) {
                    mActivityLinkedList.remove(activity);
                }
            }
        }

    }

}
