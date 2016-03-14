package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Iterator;

//http://blog.csdn.net/eclipsexys/article/details/8633793
public class VideoUtils {
    public static ArrayList<String> dayArrayList=new ArrayList<>();
//    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
//        Bitmap bitmap = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inDither = false;
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        String[] projection=new String[] { MediaStore.Video.Media._ID};
//        projection=null;
//        Cursor cursor = cr.query(uri,projection, null, null, null);
//
//        if (cursor == null || cursor.getCount() == 0) {
//            return null;
//        }
//        cursor.moveToFirst();
//        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
//        String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
//        //WWUitls.timeIsToday(date_added);
//        System.out.println("-------> videoId="+videoId+",date_added="+date_added+",cursor.getCount()="+cursor.getCount());
//        if (videoId == null) {
//            return null;
//        }
//        cursor.close();
//        long videoIdLong = Long.parseLong(videoId);
//        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, MediaStore.Images.Thumbnails.MICRO_KIND, options);
//
//        return bitmap;
//    }


    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String[] projection=new String[] { MediaStore.Video.Media._ID};
        projection=null;
        Cursor cursor = cr.query(uri,projection, null, null, null);
        int columnIndex=cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED);
        String columnName=cursor.getColumnName(columnIndex);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        while (cursor.moveToNext()){
            String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            getVideoDays(date_added);
        }
        cursor.close();
        System.out.println("----> dayArrayList.size()=" + dayArrayList.size());
        Iterator iterator = dayArrayList.iterator();
        while (iterator.hasNext()) {
            String day = (String)iterator.next();

            //某一天的时间戳
            long timeStamp= WWUitls.string2Long(WWUitls.date2TimeStamp(day,"yyyy/MM/dd"));
            //后一天的时间戳
            long beforDayTime=timeStamp+1000*60*60*24;
            System.out.println("---->timeStamp=" + timeStamp+",beforDayTime="+beforDayTime);

            projection=null;
            cursor = cr.query(uri,projection,timeStamp+"<="+columnName+"<="+beforDayTime, null, null);
            while (cursor.moveToNext()){
                String date_added = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                System.out.println("----> 筛选出来的 date_added="+ date_added);
            }

        }


        return bitmap;
    }


    public static void getVideoDays(String time){
        String day=WWUitls.getDay(time);
        if (!dayArrayList.contains(day)){
            dayArrayList.add(day);
        }
    }


}
