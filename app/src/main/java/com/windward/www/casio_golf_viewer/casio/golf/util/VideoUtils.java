package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.FileOutputStream;
import java.util.Comparator;
import com.windward.www.casio_golf_viewer.casio.golf.entity.ListItemInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//http://blog.csdn.net/eclipsexys/article/details/8633793
public class VideoUtils {
    private static final File DCIM_FILES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);//DCIMフォルダパス
    private final String MOVIE_DIR = "/Camera";
    private final String[] MOVIE_EXTENSION_LIST = {"mov","mp4","mpeg","avi"};
    private ArrayList<ListItemInfo> mMovieList;
    private static ArrayList<ListItemInfo> mFixedArrayList;


    public static Bitmap getBitmapsFromVideo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        int seconds = Integer.valueOf(time) / 1000;
        // 得到第一秒的图片
        Bitmap bitmap = retriever.getFrameAtTime(1*1000*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }



    //获取设备上所有的视频信息
    public void getVideosInfo(Context context) {
        ContentResolver contentResolver=context.getContentResolver();
        String [] videoColumns=new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE
        };
//      两种方法均可
//		Cursor cursor=
//	    this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
        Cursor cursor=contentResolver.query
                (MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, null);
        while (cursor.moveToNext()) {
            String _id=
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String filePath=
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String title=
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String mime_type=
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

            System.out.println("---------> filePath="+filePath);
        }
    }






    //获取设备上的视频的时间
    //路径确实是  path=/storage/emulated/0/DCIM/Camera/CIMG0084.MOV
    public static String getVideoTime(Context context,String path) {

        String data,_id,title,mime_type,date_added=null;
        ContentResolver contentResolver=context.getContentResolver();

        //需要查询的数据,其中MediaStore.Video.Media.DATA表示路径
        String [] videoColumns=new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED
        };

        String selection =   MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs =  new String[]{"%" + path + "%"};
       // Cursor cursor=contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,  selection, selectionArgs, null);
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, MediaStore.Video.Media.DATA + " like ?", new String[]{"%" + path + "%"}, null);


        //cursor.moveToFirst();
        System.out.println("----> 获取结果,cursor.getCount()="+cursor.getCount());

        while (cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            _id= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            title= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            mime_type= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            date_added= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
            System.out.println("----> data="+data+",_id="+_id+",title="+title+",mime_type="+mime_type+",date_added="+date_added);
        }
        return date_added;
    }



    /**
     * 获取图片或者视频路径
     *
     * @param context
     * @param uri
     * @return
     */

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        System.out.println("----44---->data="+data);
        return data;
    }


    public static void getT(final Context context,String path){
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        ContentResolver cr = context.getContentResolver();
                        long datemodified = 0;
                        long dateadded = 0;
                        Cursor cursor = cr.query(uri, null, null, null, null);
                        System.out.println("----44---->cursor.getCount()="+cursor.getCount());
                        if (cursor != null && cursor.moveToFirst()) {
                            datemodified = cursor.getLong(cursor
                                    .getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
                            dateadded = cursor.getLong(cursor
                                    .getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));

                            System.out.println("-------->dateadded="+dateadded);
                            cursor.close();
                        }

                        ContentValues values = new ContentValues();
                        if (datemodified > 0
                                && String.valueOf(datemodified).length() > 10) {
                            values.put(MediaStore.MediaColumns.DATE_MODIFIED,
                                    datemodified / 1000);
                        }
                        if (dateadded > 0
                                && String.valueOf(dateadded).length() > 13) {
                            values.put(MediaStore.MediaColumns.DATE_ADDED,
                                    dateadded / 1000);
                        }

                        if (values.size() > 0) {
                            cr.update(uri, values, null, null);
                        }
                    }
                });
    }



    //整理List
    public ArrayList<ListItemInfo> fixVideoArrayList(Context context,ArrayList<ListItemInfo> arrayList){
        ArrayList<String> daysList=new ArrayList<String>();
        ArrayList<ListItemInfo> fixedArrayList=new ArrayList<ListItemInfo>();

        //对原视频排序,否则可能出现顺序的错乱
        Collections.sort(arrayList, new Comparator<ListItemInfo>() {
            @Override
            public int compare(ListItemInfo lhs, ListItemInfo rhs) {

                return -(lhs.getmTime().compareTo(rhs.getmTime()));
            }
        });


        for(int i=0;i<arrayList.size();i++){
            ListItemInfo listItemInfo=arrayList.get(i);
            if(!daysList.contains(listItemInfo.getmDay())){
                daysList.add(listItemInfo.getmDay());
                ListItemInfo l=new ListItemInfo(context,listItemInfo.getFilePath());
                l.setmDay(listItemInfo.getmDay());
                l.setIsShowVideo(false);
                fixedArrayList.add(l);
                fixedArrayList.add(listItemInfo);
            }else {
                fixedArrayList.add(listItemInfo);
            }
        }

       mFixedArrayList=fixedArrayList;

        return fixedArrayList;

    }


    public static ArrayList<ListItemInfo> getFixedVideoArrayList(){
       return mFixedArrayList;
    }



    //获取设备中的视频
    public ArrayList<ListItemInfo> getVideoList(Context context){
        if(checkFilePath()){

        mMovieList = new ArrayList<ListItemInfo>();
        File[] files = new File(String.valueOf(DCIM_FILES) + MOVIE_DIR).listFiles();
        //File[] files =new File("/storage/extSdCard/DCIM/Camera").listFiles();//三星专用
        // System.out.println("-----> 遍历的路径 ="+String.valueOf(DCIM_FILES) + MOVIE_DIR);
        // System.out.println("-----> 该路径下文件的个数="+files.length);
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String path = files[i].getPath();
                    //System.out.println("-----> 每个的路径path="+path);
                    if (checkFileExtension(path)) {
                        //满足条件的视频的路径 删选出mp4文件
                        System.out.println("-----> 满足条件的视频 path="+path);
                        mMovieList.add(new ListItemInfo(context,path));
                    }
                }
            }
        }
        }
        return mMovieList;
    }


    private boolean checkFileExtension(String aFileName){
        String fileExtension = getFileExtension(aFileName);
        //System.out.println("-----> fileExtension="+fileExtension);
        for(int count=0;count<MOVIE_EXTENSION_LIST.length;count++){
            if(fileExtension.equalsIgnoreCase(MOVIE_EXTENSION_LIST[count])){
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String aFileName){
        String[] split = aFileName.split("\\.");
        return split[split.length - 1];
    }

    private boolean checkFilePath(){
        //ディレクトリの確認
        File dir = new File(String.valueOf(DCIM_FILES) + MOVIE_DIR);
        if(!dir.exists()||!dir.isDirectory()){
            File directory = DCIM_FILES;
            if (directory.exists()){
                if(directory.canWrite()){
                    File file = new File(String.valueOf(DCIM_FILES) + MOVIE_DIR);
                    return file.mkdir();
                } else {
                    //ディレクトリの書き込み不可
                    return false;
                }
            } else {
                //内部ストレージのディレクトリが存在しない
                return false;
            }
        }
        return true;
    }




    //通知媒体库进行更新
    public static void scanMedia(Context context){
        // SDK版本大于等于4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
            intent.setData(uri);
            context.sendBroadcast(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
            intent.setData(uri);
            context.sendBroadcast(intent);
        }
    }


    public class MediaScannerConnectionImpl implements MediaScannerConnection.MediaScannerConnectionClient {
        private File mFile;
        private MediaScannerConnection mMediaScannerConnection;

        public MediaScannerConnectionImpl(Context context, File file) {
            mFile = file;
            mMediaScannerConnection = new MediaScannerConnection(context, this);
            mMediaScannerConnection.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMediaScannerConnection.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMediaScannerConnection.disconnect();
        }

    }


    /**
     * Scanning the file in the Gallery database
     *
     * @param path
     * @param isDelete
     */
    private void scanFile(final Context context,String path, final boolean isDelete) {
        try {
            MediaScannerConnection.scanFile(context, new String[] { path },
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void test(String path){
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        String date= mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        String duration= mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        System.out.println("------test----> date="+date+",duration="+duration+",path="+path);
    }

    private void  scan(){
         //第一种
        // Uri uri = Uri.fromFile(new File(path));
        // intent.setData(uri);

        //第二种
        //intent.setData( MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

    }

    public static void mediaScan1(Context context, File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {

            public void onScanCompleted(String path, Uri uri) {
               // msc.disconnect();
            }

        });




    }

    private void mediaScan2(Context context, String fileName) {
        MediaScannerConnection.scanFile(context,
                new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + fileName}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {


                    }

                });
    }

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

//        String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
//        Uri u = Uri.fromFile(new File(path));
//        //intent.setData(  MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        System.out.println("----> MediaStore.Images.Media.EXTERNAL_CONTENT_URI=" +MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        System.out.println("----> uri=" + u);



//        Cursor curor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null, null, null,null);
//        System.out.println("---->  cursor.getCount()=" + curor.getCount());


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
