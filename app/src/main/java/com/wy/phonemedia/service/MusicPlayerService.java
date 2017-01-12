package com.wy.phonemedia.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.wy.phonemedia.IMusicPlayerService;
import com.wy.phonemedia.R;
import com.wy.phonemedia.activity.AudioPlayActivity;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.CashUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Wyman on 2016/10/9.
 * WeChat: wy391920778
 * Effect:音乐的服务类
 */

public class MusicPlayerService extends Service {
    public static final String NOTIFY_CHANGE = "com.wy.mediaphone.notifyChange";
    /**
     * 顺序播放
     */
    public static final int PLAY_MODE_NORMAL = 1;
    /**
     * 单曲循环
     */
    public static final int PLAY_MODE_SINGLE = 2;
    /**
     * 随机播放
     */
    public static final int PLAY_MODE_RANDOM = 3;
    /**
     * 播放模式
     */
    private int playMode = PLAY_MODE_NORMAL;
    private List<MediaItem> mediaItems;
    /**
     * 当前播放的音频文件对象
     */
    private MediaItem mediaItem;
    private int position;
    /**
     * 音频播放器
     */
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        //加载音乐列表
        getDataFromLocal();
        playMode = CashUtils.getPlayMode(this, "playMode");
    }

    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard中的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
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
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();

        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentDuration() throws RemoteException {
            return service.getCurrentDuration();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void previous() throws RemoteException {
            service.previous();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };

    /**
     * 根据位置打开播放器,并且播放
     *
     * @param position
     */
    private void openAudio(int position) {
        this.position = position;
        if (mediaItems != null && mediaItems.size() > 0) {
            mediaItem = mediaItems.get(position);
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            try {
                mediaPlayer = new MediaPlayer();
                //设置监听：播放出错，播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        }

    }

    private NotificationManager notificationManager;

    /**
     * 开始播放
     */
    private void start() {
        mediaPlayer.start();

        notifyAudioPlayer();


    }


    /**
     * 当播放歌曲的时候，在状态栏显示，点击进去音乐播放器页面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notifyAudioPlayer() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, AudioPlayActivity.class);
        intent.putExtra("notification", true);//标识来自于状态栏
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("手机音乐")
                .setContentText("正在播放：" + getName())
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(1, notification);
    }

    /**
     * 暂停播放
     */
    private void pause() {
        mediaPlayer.pause();
        //取消状态栏通知
        notificationManager.cancel(1);
    }

    /**
     * 停止播放
     */
    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        notificationManager.cancel(1); //取消状态栏通知
        AudioPlayActivity.currentPosition = 0x123;//防止再次进入同一首手歌继续播放
        AudioPlayActivity.service = null;//停止服务
    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentDuration() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当总的播放时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到歌手
     *
     * @return
     */
    private String getArtist() {
        return mediaItem.getArtist();
    }

    /**
     * 得到歌曲名称
     *
     * @return
     */
    private String getName() {
        return mediaItem.getName();
    }

    /**
     * 得到歌曲路径
     *
     * @return
     */
    private String getAudioPath() {
        return mediaItem.getData();
    }

    /**
     * 播放下一个
     */
    private void next() {
        if (mediaItems != null && mediaItems.size() > 0) {
            int playMode = getPlayMode();
            if (playMode == PLAY_MODE_NORMAL) {
                position++;
                if (position < mediaItems.size()) {
                    openAudio(position);
                } else {//到最后一个，跳到第一个
                    position = 0;
                    openAudio(position);
                }
            } else if (playMode == PLAY_MODE_SINGLE) {
                position++;
                if (position < mediaItems.size()) {
                    openAudio(position);
                } else {//到最后一个，跳到第一个
                    position = 0;
                    openAudio(position);
                }
            } else if (playMode == PLAY_MODE_RANDOM) {//随机播放
                int randomPosition = new Random().nextInt(mediaItems.size());
                Log.e("randomPosition", "randomPosition:" + randomPosition);
                Log.e("position", "position:" + position);
                if (randomPosition == position && position < mediaItems.size() - 1) {
                    openAudio(randomPosition++);
                } else if (randomPosition == position && position >= mediaItems.size() - 1) {
                    openAudio(randomPosition--);
                }
                openAudio(randomPosition);
            }

        }
    }


    /**
     * 播放上一个
     */
    private void previous() {
        if (mediaItems != null && mediaItems.size() > 0) {
            int playMode = getPlayMode();
            if (playMode == PLAY_MODE_NORMAL) {
                position--;
                if (position >=0) {
                    openAudio(position);
                } else {//到第一个，跳到最后一个
                    position = mediaItems.size()-1;
                    openAudio(position);
                }
            } else if (playMode == PLAY_MODE_SINGLE) {
                position--;
                if (position >=0) {
                    openAudio(position);
                } else {//到第一个，跳到最后一个
                    position = mediaItems.size()-1;
                    openAudio(position);
                }
            } else if (playMode == PLAY_MODE_RANDOM) {//随机播放
                int randomPosition = new Random().nextInt(mediaItems.size());
                Log.e("randomPosition", "randomPosition:" + randomPosition);
                Log.e("position", "position:" + position);
                if (randomPosition == position && position < mediaItems.size() - 1) {
                    openAudio(randomPosition++);
                } else if (randomPosition == position && position >= mediaItems.size() - 1) {
                    openAudio(randomPosition--);
                }
                openAudio(randomPosition);
            }

        }
    }

    /**
     * 设置播放模式
     *
     * @param playMode
     */
    private void setPlayMode(int playMode) {
        this.playMode = playMode;
        CashUtils.putPlayMode(this, "playMode", playMode);

        if (playMode == MusicPlayerService.PLAY_MODE_SINGLE) {
            //单曲循环播放--不会触发播放完成的回调
            mediaPlayer.setLooping(true);

        } else {
            mediaPlayer.setLooping(false);

        }
    }

    /**
     * 得到播放模式
     */
    private int getPlayMode() {
        return playMode;
    }

    /**
     * 是否正在播放
     */
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
            //通知activity更新组件---广播
//            notifyChange(position);
            EventBus.getDefault().post(position);
        }
    }

    private void notifyChange(int currentPosition) {
        Intent intent = new Intent(NOTIFY_CHANGE);
        //当前播放的currentPosition
        intent.putExtra("currentPosition", currentPosition);
        sendBroadcast(intent);

    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    }
}
