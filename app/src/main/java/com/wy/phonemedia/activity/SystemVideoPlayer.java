package com.wy.phonemedia.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wy.phonemedia.R;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.Utils;
import com.wy.phonemedia.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Wyman on 2016/9/19.
 * WeChat: wy391920778
 * Effect:系统的视频播放器
 */
public class SystemVideoPlayer extends AppCompatActivity implements View.OnClickListener {
    /**
     * 更新播放进度
     */
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_CONTROLLER = 2;
    //3s后隐藏控制面板
    private static final long HIDE_CONTROLLER_TIME = 4000;
    /**
     * 视频默认大小
     */
    private static final int DEFAULT_SCREEN = 1;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 2;
    /**
     * 显示网速
     */
    private static final int SHOW_SPEED = 3;
    private VideoView mVideoView;
    private Uri uri;
    private LinearLayout llTop;
    private LinearLayout llTitleBar;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private SeekBar seekBarVoice;
    private Button btnVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private SeekBar seekBarVideo;
    private TextView tvCurrentTime;
    private TextView tvDuration;
    private LinearLayout llBottomBar;
    private Button btnExit;
    private Button btnVideoPrevious;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnSwitchScreen;
    private Utils mUtils;
    private RelativeLayout media_controller;
    private LinearLayout ll_buffer;
    private TextView tv_buffer_netSpeed;
    private TextView loading_net_speed;
    private LinearLayout ll_loading;
    /**
     * 控制声音
     */
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVoice;
    /**
     * 0~15
     * 最大音量
     */
    private int maxVoice;
    /**
     * 手势控制
     */
    private GestureDetector mDetector;
    /**
     * 上一次的播放进度
     */
    private int preCurrentPosition;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED:
                    String speed=mUtils.getNetSpeed(SystemVideoPlayer.this);
                    tv_buffer_netSpeed.setText("缓冲中..."+speed);
                    loading_net_speed.setText("拼命加载中..."+speed);
                    /**
                     * 没两秒更新一次
                     */
                    mHandler.removeMessages(SHOW_SPEED);
                    mHandler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    break;

                case HIDE_CONTROLLER://隐藏控制面板
                    hideController();
                    break;
                case PROGRESS:
                    //获取当前播放进度
                    int currentPosition = mVideoView.getCurrentPosition();
                    //设置给seekBar
                    seekBarVideo.setProgress(currentPosition);
                    //更新文本进度
                    tvCurrentTime.setText(mUtils.stringForTime(currentPosition));
                    //更新系统时间
                    tvSystemTime.setText(getSystemTime());
                    /**
                     * 缓存进度的更新
                     */
                    if (isNetUi) {
                        //只有是网络视频才有缓冲效果
                        int buffer = mVideoView.getBufferPercentage();//0~100
                        int secondaryProgress = buffer * seekBarVideo.getMax();
                        seekBarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频没有缓冲效果
                        seekBarVideo.setSecondaryProgress(0);

                    }

                    /**
                     * 自定义的监听卡
                     */
                    if (!isUseSystem && mVideoView.isPlaying()) {
                        if (mVideoView.isPlaying()) {
                            if (currentPosition - preCurrentPosition < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                   preCurrentPosition=currentPosition;




                    /**
                     * 每秒更新一次
                     */
                    //1.销毁当前的message
                    mHandler.removeMessages(PROGRESS);
                    //2.重新发送新消息（每秒）
                    mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };
    /**
     * 传递过来的播放列表
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 传递过来的具体播放位置
     */
    private int position;
    /**
     * MediaController是否隐藏
     */
    private boolean isMediaController;
    /**
     * 是否全屏
     */
    private boolean isScreenFull = false;
    /**
     * 视频的真是宽高
     */
    private int mediaVideoWidth;
    private int mediaVideoHeight;
    /**
     * 屏幕的宽和高
     */
    private int mScreenWidth;
    private int mScreenHeight;
    /**
     * 是否静音
     */
    private boolean isMute = false;
    /**
     * 是否是网络视频
     */
    private boolean isNetUi;
    /**
     * 是否使用系统的监听卡
     */
    private boolean isUseSystem=false;


    /**
     * 获取系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }

    /**
     * 监听电量变化的广播
     */
    private BroadcastReceiver mReceiver;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-09-20 15:04:40 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        loading_net_speed = (TextView) findViewById(R.id.loading_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_buffer_netSpeed = (TextView) findViewById(R.id.tv_buffer_net_speed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llTitleBar = (LinearLayout) findViewById(R.id.ll_titleBar);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        seekBarVoice = (SeekBar) findViewById(R.id.seekBar_voice);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBar_video);
        tvCurrentTime = (TextView) findViewById(R.id.tv_currentTime);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        llBottomBar = (LinearLayout) findViewById(R.id.ll_bottomBar);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPrevious = (Button) findViewById(R.id.btn_video_previous);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPrevious.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-09-20 15:04:40 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {

        mHandler.removeMessages(HIDE_CONTROLLER);

        if (v == btnVoice) {
            isMute = !isMute;
            updateVoice(currentVoice, isMute);
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            finish();
        } else if (v == btnVideoPrevious) {
            playPreviousVideo();
        } else if (v == btnVideoStartPause) {
            // Handle clicks for btnVideoStartPause
            setMediaPlayState();
        } else if (v == btnVideoNext) {
            playNextVideo();
        } else if (v == btnSwitchScreen) {
            setScreenState();
        }
        //重新发送隐藏消息
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
    }

    /**
     * 播放上一个
     */
    private void playPreviousVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                ll_loading.setVisibility(View.GONE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUi = mUtils.isNetUri(mediaItem.getData());
                mVideoView.setVideoPath(mediaItem.getData());
            }
        }
        setButtonState();
    }

    /**
     * 播放下一个
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position <= mediaItems.size()) {
                ll_loading.setVisibility(View.GONE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUi = mUtils.isNetUri(mediaItem.getData());
                mVideoView.setVideoPath(mediaItem.getData());
            }

        }
        setButtonState();
    }


    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {//只有一个视频  都设置为不可点
                btnVideoPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPrevious.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            } else if (mediaItems.size() == 2) {//只有两个视频
                if (position == 0) {//选中第一个 --上一个不可点，下一个可点
                    btnVideoPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPrevious.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == 1) {//选中第二个
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPrevious.setBackgroundResource(R.drawable.btn_video_previous_selector);
                    btnVideoPrevious.setEnabled(true);
                }
            } else {
                if (position == 0) {//选中第一个 --上一个不可点，下一个可点
                    btnVideoPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPrevious.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {//选中最后一个，上一个可点
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPrevious.setBackgroundResource(R.drawable.btn_video_previous_selector);
                    btnVideoPrevious.setEnabled(true);
                } else {//两个都可点
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                    btnVideoPrevious.setBackgroundResource(R.drawable.btn_video_previous_selector);
                    btnVideoPrevious.setEnabled(true);
                }
            }
        } else if (uri != null) {//只有一个视频  都设置为不可点
            btnVideoPrevious.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPrevious.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_video_player);
        findViews();
        initData();
        //获取播放列表
        getData();
        //设置播放列表
        setData();
        setListener();
        //设置系统的控制面板(我使用自己写的控制面板)
//        mVideoView.setMediaController(new MediaController(this));
        getSystemTime();


    }

    /**
     * 设置播放列表
     */
    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            isNetUi = mUtils.isNetUri(mediaItem.getData());
            mVideoView.setVideoPath(mediaItem.getData());
            setButtonState();
        } else if (uri != null) {
            tvName.setText(uri.toString());
            isNetUi = mUtils.isNetUri(uri.toString());
            mVideoView.setVideoURI(uri);
            setButtonState();
        } else {
            Toast.makeText(SystemVideoPlayer.this, "没有数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取播放列表
     */
    private void getData() {
        //获取播放地址（一条数据）
        uri = getIntent().getData();//来自于手机内部的文件夹，图片浏览器
        //获取列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {

        mUtils = new Utils();
        //注册电量广播,ACTION_BATTERY_CHANGED广播为粘性广播，所以一注册就会会调用onReceive,获取到当前的电量信息。
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        //当电量变化的时候发广播
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, filter);

        /**
         * 设置手势监听事件
         */
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                setMediaPlayState();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isMediaController) {//如果显示就隐藏
                    hideController();
                    //把隐藏的消息移除
                    mHandler.removeMessages(HIDE_CONTROLLER);
                } else {
                    showController();
                    //发送消息4S后自动隐藏
                    mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setScreenState();

                return super.onDoubleTap(e);
            }
        });
        /**
         * 获取屏幕的宽和高
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //最大音量和SeekBar关联
        seekBarVoice.setMax(maxVoice);
        //设置当前进度
        seekBarVoice.setProgress(currentVoice);

        /**
         * 显示网络速度
         */
        mHandler.sendEmptyMessage(SHOW_SPEED);

    }

    /**
     * 切换视屏的状态
     */
    private void setScreenState() {
        if (isScreenFull) {//全屏
            //设置为默认
            setVideoType(DEFAULT_SCREEN);
        } else {//默认
            //设置为全屏
            setVideoType(FULL_SCREEN);
        }
    }

    /**
     * 设置视频的显示状态
     *
     * @param defaultScreen 是否全屏
     */
    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case DEFAULT_SCREEN://默认
                //设置视频的大小
                int mVideoWidth = mediaVideoWidth;
                int mVideoHeight = mediaVideoHeight;
                int width = mScreenWidth;
                int height = mVideoHeight;
                /**
                 * 等比例缩放的算法
                 */
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                mVideoView.setMediaSize(width, height);
                //改变按钮样式
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                isScreenFull = false;

                break;
            case FULL_SCREEN://全屏
                //设置视频的大小 -屏幕有多大就设置多大
                mVideoView.setMediaSize(mScreenWidth, mScreenHeight);
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                isScreenFull = true;
                break;
        }
    }


    /**
     * 显示控制面板
     */
    private void showController() {
        media_controller.setVisibility(View.VISIBLE);
        isMediaController = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideController() {
        media_controller.setVisibility(View.GONE);
        isMediaController = false;
    }

    /**
     * 切换播放状态
     */
    private void setMediaPlayState() {
        if (mVideoView.isPlaying()) {
            //点击后暂停
            mVideoView.pause();
            //改变button样式
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //点击后播放
            mVideoView.start();
            //改变button样式
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//0~100
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //准备好的监听
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        //放完成的监听
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        //seekBar状态变化的滑动监听
        seekBarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        seekBarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
        if (isUseSystem) {//系统的监听卡，适合直播m3u8格式
            //监听视频播放卡
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mVideoView.setOnInfoListener(new VideoOnInfoListener());
            }
        }


    }

    private class VideoOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    Toast.makeText(SystemVideoPlayer.this, "视频卡了。。。", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);

                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_buffer.setVisibility(View.GONE);
//                    Toast.makeText(SystemVideoPlayer.this, "视频卡结束了。。。", Toast.LENGTH_SHORT).show();
                    break;

            }
            return false;
        }
    }
    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updateVoice(progress, isMute);

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_CONTROLLER);//移除隐藏控制面板的消息
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
        }
    }


    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当手指滑动的时候，会引起seekbar进度变化，会回调该方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的为true,不是用户引起（自动更新进度）的为false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mVideoView.seekTo(progress);//播放指定进度
            }
        }

        /**
         * 当手指触碰的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_CONTROLLER);//移除隐藏控制面板的消息

        }

        /**
         * 当手指离开的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaVideoWidth = mp.getVideoWidth();
            mediaVideoHeight = mp.getVideoHeight();
            //开始播放
            mVideoView.start();
            hideController();
            /**
             * 更新seekBar
             */
            //1.获取视频的总时长
            int totalSize = mVideoView.getDuration();
            //2.设置给seekBar
            seekBarVideo.setMax(totalSize);
            //3.通知handler去更新seekBar
            mHandler.sendEmptyMessage(PROGRESS);
            //设置总时长
            tvDuration.setText(mUtils.stringForTime(totalSize));

            /**
             * 设置视频的宽和高为默认值（等比例缩放后）
             */
            setVideoType(DEFAULT_SCREEN);
            /**
             * 隐藏加载页面
             */
            ll_loading.setVisibility(View.GONE);


//            mVideoView.setMediaSize(400,400);

//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            startVitamioPlayer();
            return true;
        }
    }
    /**
     * 启动万能播放器
     */
    private void startVitamioPlayer() {
        if (mVideoView!=null) {
            mVideoView.stopPlayback();
        }
        Intent intent = new Intent(this,VitamioVideoPlayer.class);
        if (mediaItems!=null&&mediaItems.size()>0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        } else if (uri!=null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();

    }
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(SystemVideoPlayer.this, "播放完成了", Toast.LENGTH_SHORT).show();
            //改变button样式
            if (mediaItems != null && mediaItems.size() > 0) {
                if (position < mediaItems.size() - 1) {
                    playNextVideo();
                } else {
                    Toast.makeText(SystemVideoPlayer.this, "大姐，后面没有视频了！", Toast.LENGTH_SHORT).show();
                    btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
                }
            } else if (uri != null) {
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }

        }
    }

    /**
     * 更新音量进度
     *
     * @param progress
     */
    private void updateVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekBarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekBarVoice.setProgress(progress);
            currentVoice = progress;
        }

    }
    @Override
    protected void onDestroy() {
        //移除消息
        mHandler.removeCallbacksAndMessages(null);

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        super.onDestroy();
    }


    private float mVol;//按下时的当前音量
    private  float startY;
    private float endY;
    private float offsetY;//偏移量
    private float startX;
    private float endX;
    private float offsetX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                startY = event.getY();
                startX = event.getX();
                mHandler.removeMessages(HIDE_CONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                endY = event.getY();
                endX = event.getX();
                offsetY = startY-endY;
                offsetX = endX - startX;
                Log.e("onTouchEvent: startX" ,startX+"");
                Log.e("onTouchEvent: endX" ,endX+"");
                Log.e("onTouchEvent: offsetX" ,offsetX+"");
                //改变的声音=（滑动距离:总距离）*最大音量
                int deltaVoice = (int) ((offsetY / mScreenHeight) * maxVoice);
                //最终音量=原来的音量+改变的音量
                int voice = (int) Math.min(deltaVoice+mVol, maxVoice);
                if (Math.abs(offsetX)<=10) {
                    updateVoice(voice,false);
                }

                break;
            case MotionEvent.ACTION_UP:
                mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 监听物理键，实现调节声音的大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            currentVoice = Math.max(0, currentVoice);
            updateVoice(currentVoice, false);
            mHandler.removeMessages(HIDE_CONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
            return true;
        } else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            currentVoice = Math.min(15, currentVoice);
            updateVoice(currentVoice, false);
            mHandler.removeMessages(HIDE_CONTROLLER);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, HIDE_CONTROLLER_TIME);
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }


}
