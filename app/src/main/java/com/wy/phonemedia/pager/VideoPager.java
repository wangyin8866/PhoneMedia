package com.wy.phonemedia.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wy.phonemedia.R;
import com.wy.phonemedia.activity.AudioPlayActivity;
import com.wy.phonemedia.activity.SystemVideoPlayer;
import com.wy.phonemedia.adapter.VideoAdapter;
import com.wy.phonemedia.base.BasePager;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wyman on 2016/9/14.
 * WeChat: wy391920778
 * Effect: 本地视频
 */
public class VideoPager extends BasePager {
    private static final String TAG = "VideoPager";
    private RecyclerView rv_video;
    private TextView tv_video;
    private ProgressBar pb_video;
    private VideoAdapter adapter;
    /**
     * 装数据的集合
     */
    private List<MediaItem> mediaItems;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                //显示数据
                rv_video.setLayoutManager(new LinearLayoutManager(context));
                adapter = new VideoAdapter(context,mediaItems,true);
                rv_video.setAdapter(adapter);
                rv_video.setItemAnimator(new DefaultItemAnimator());//设置点击动画

                //设置recyclerView的点击事件
                adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.e(TAG, "onItemClick:" + "onItemClick");
                        MediaItem mediaItem = mediaItems.get(position);
//                         //1. 调用所有可以播放的播放器，隐式调用
//                        Intent intent = new Intent();
//                        intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
//                        context.startActivity(intent);
                        //2.调用系统的播放器，显示调用
//                        Intent intent = new Intent(context,SystemVideoPlayer.class);
//                        //传递一个地址给播放器
//                        intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");
                        //传递集合列表给播放器
                        Intent intent = new Intent(context,SystemVideoPlayer.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("videolist", (Serializable) mediaItems);
                        intent.putExtras(bundle);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                        if (AudioPlayActivity.service != null) {//播放视频的时候关闭音乐
                            try {
                                AudioPlayActivity.service.stop();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }


                        }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
                //隐藏text
                tv_video.setVisibility(View.GONE);
            } else {
                //没有数据
                //显示text
                tv_video.setVisibility(View.VISIBLE);
            }
            //隐藏pb
            pb_video.setVisibility(View.GONE);
        }
    };
    public VideoPager(Context context) {
        super(context);

    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        rv_video = (RecyclerView) view.findViewById(R.id.rv_video);
        tv_video = (TextView) view.findViewById(R.id.tv_video);
        pb_video = (ProgressBar) view.findViewById(R.id.pb_video);
        rv_video = (RecyclerView) view.findViewById(R.id.rv_video);

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频的数据被初始化了...");
        //加载本地数据
        getDataFromLocal();
        //视频内容
    }

    /**
     * 从本地sd卡读取数据
     * <p/>
     * 两种方法：1.遍历sd卡，读取所有的后缀名（数据量大时会很慢，不采用）
     * 2.从系统的内容提供者中读取
     * 3.如果是6.0以上的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {
       new Thread(){
           @Override
           public void run() {
               super.run();
               isGrantExternalRW((Activity) context);//解决6.0的读取sdcard权限
               mediaItems = new ArrayList<>();
               ContentResolver resolver = context.getContentResolver();
               Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
               String[] objs={
                       MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard中的名称
                       MediaStore.Video.Media.DURATION,//视频总时长
                       MediaStore.Video.Media.SIZE,//视频大小
                       MediaStore.Video.Media.DATA,//视频的绝对地址
                       MediaStore.Video.Media.ARTIST,//歌曲的演唱者
               };
               Cursor cursor= resolver.query(uri, objs, null, null, null);
               if (cursor!=null) {
                   while (cursor.moveToNext()) {
                       MediaItem mediaItem = new MediaItem();
                       mediaItems.add(mediaItem);
                       String name = cursor.getString(0);//视频文件在sdcard中的名称
                       mediaItem.setName(name);
                       long duration = cursor.getLong(1);//视频总时长
                       mediaItem.setDuration(duration);
                       long size = cursor.getLong(2);//视频大小
                       mediaItem.setSize(size);
                       String data = cursor.getString(3);//视频的绝对地址
                       mediaItem.setData(data);
                       String artist = cursor.getString(4);//歌曲的演唱者
                       mediaItem.setArtist(artist);
                   }
                   cursor.close();
               }
               mHandler.sendEmptyMessage(10);
           }
       }.start();
    }

    /**
     * 解决android 6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
            return false;
        }
        return true;

    }


}
