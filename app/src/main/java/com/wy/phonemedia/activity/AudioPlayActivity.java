package com.wy.phonemedia.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wy.phonemedia.IMusicPlayerService;
import com.wy.phonemedia.R;
import com.wy.phonemedia.service.MusicPlayerService;
import com.wy.phonemedia.utils.LyricUtils;
import com.wy.phonemedia.utils.Utils;
import com.wy.phonemedia.view.BaseVisualizerView;
import com.wy.phonemedia.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import static com.wy.phonemedia.R.id.iv_icon;

/**
 * Created by Wyman on 2016/10/8.
 * WeChat: wy391920778
 * Effect:
 */
public class AudioPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AudioPlayActivity";
    /**
     * 更新进度条
     */
    public static final int PROGRESS = 1;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 2;
    public static IMusicPlayerService service;

    private int position;
    public static int currentPosition = 0x123;
    private ImageView ivIcon;
    private TextView tvSinger;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekBarAudio;
    private Button btnAudioPlayMode;
    private Button btnAudioPrevious;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnAudioLyric;
    private ShowLyricView tv_showLyricView;
    private AnimationDrawable rocketAnimation;
    private BaseVisualizerView baseVisualizerView;
    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 连接成功
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (!notification) {//从列表
                        if (position != currentPosition) {//是否从同一首歌进入
                            service.openAudio(position);
                        } else {
                            showViewData();
                        }
                        currentPosition = position;
                    } else {//从状态栏
                        if (mVisualizer == null) {
                            setupVisualizerFxAndUi();
                        }
                        showViewData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 断开连接
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected:" );
            if (service != null) {
                try {
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
//    private MyBroadcastReceiver receiver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        //1.得到当前的播放进度
                        if (service != null) {
                            int currentDuration = service.getCurrentDuration();
                            //2.把进度传到showLyric控件中，并且计算高亮那一句
                            tv_showLyricView.setShowNextLyric(currentDuration);
                            //3.实时的发送消息
                            mHandler.removeMessages(SHOW_LYRIC);
                            mHandler.sendEmptyMessage(SHOW_LYRIC);
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;

                case PROGRESS:
                    try {
                        int currentDuration = service.getCurrentDuration();
                        seekBarAudio.setProgress(currentDuration);
                        tvTime.setText(utils.stringForTime(currentDuration) + "/" + utils.stringForTime(service.getDuration()));

                        //没秒更新一次
                        mHandler.removeMessages(PROGRESS);
                        mHandler.sendEmptyMessageDelayed(PROGRESS, 1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
    private Utils utils;
    /**
     * 是否从状态栏进去播放器
     */
    private boolean notification;

    private void findViews() {
        ivIcon = (ImageView) findViewById(iv_icon);
        assert ivIcon != null;
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        /**
         * 初始化动画
         */
        rocketAnimation = (AnimationDrawable) ivIcon.getBackground();

        tvSinger = (TextView) findViewById(R.id.tv_singer);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekBarAudio = (SeekBar) findViewById(R.id.seekBar_audio);
        btnAudioPlayMode = (Button) findViewById(R.id.btn_audio_play_mode);
        btnAudioPrevious = (Button) findViewById(R.id.btn_audio_previous);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnAudioLyric = (Button) findViewById(R.id.btn_audio_lyric);
        tv_showLyricView = (ShowLyricView) findViewById(R.id.tv_showLyricView);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);
        btnAudioPlayMode.setOnClickListener(this);
        btnAudioPrevious.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioLyric.setOnClickListener(this);

        //设置音频的拖曳
        seekBarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-10-10 13:50:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlayMode) {
            if (service != null) {
                setPlayMode();
            }

        } else if (v == btnAudioPrevious) {
            if (service != null) {
                try {
                    service.previous();
                    btnAndAnimStartState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioStartPause) {
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮和动画暂停状态
                        btnAndAnimPauseState();
                    } else {
                        //播放
                        service.start();
                        //按钮和动画播放状态
                        btnAndAnimStartState();

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioNext) {
            if (service != null) {
                try {
                    service.next();
                    btnAndAnimStartState();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if (v == btnAudioLyric) {
            // Handle clicks for btnAudioLyric
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode() {
        if (service != null) {
            try {
                int playMode = service.getPlayMode();
                if (playMode == MusicPlayerService.PLAY_MODE_NORMAL) {
                    playMode = MusicPlayerService.PLAY_MODE_SINGLE;
                    btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_single_selector);
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                } else if (playMode == MusicPlayerService.PLAY_MODE_SINGLE) {
                    playMode = MusicPlayerService.PLAY_MODE_RANDOM;
                    btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_all_selector);
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                } else if (playMode == MusicPlayerService.PLAY_MODE_RANDOM) {
                    playMode = MusicPlayerService.PLAY_MODE_NORMAL;
                    btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_normal_selector);
                    Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
                }
                service.setPlayMode(playMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 校验播放模式
     */
    private void checkPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if (playMode == MusicPlayerService.PLAY_MODE_NORMAL) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_normal_selector);
            } else if (playMode == MusicPlayerService.PLAY_MODE_SINGLE) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_single_selector);
            } else if (playMode == MusicPlayerService.PLAY_MODE_RANDOM) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_mode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * //按钮和动画播放状态
     */
    private void btnAndAnimStartState() {
        //切换按钮状态
        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
        //动画开始
        rocketAnimation.start();
    }

    /**
     * //按钮和动画暂停状态
     */
    private void btnAndAnimPauseState() {
        //切换按钮状态
        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
        //动画停止
        rocketAnimation.stop();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_paly);
        initData();
        findViews();
        getData();
        bindAndStartService();

    }

    private void initData() {
        utils = new Utils();
        /**
         * 使用EventBus代替广播
         */
//        receiver = new MyBroadcastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(MusicPlayerService.NOTIFY_CHANGE);
//        registerReceiver(receiver, filter);

        //1.注册EventBus
        EventBus.getDefault().register(this);

    }

    /**
     * 显示歌曲信息
     */
    private void showViewData() {
        try {
            if (service != null) {
                if (service.isPlaying()) {
                    //按钮和动画播放状态
                    btnAndAnimStartState();
                } else {
                    //按钮和动画暂停状态
                    btnAndAnimPauseState();
                }

                showLyric();
                tvSinger.setText(service.getArtist());
                tvName.setText(service.getName());
                //设置进度条的最大值
                seekBarAudio.setMax(service.getDuration());

                //更新进度条
                mHandler.sendEmptyMessage(PROGRESS);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据
     */
    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.wy.phonemedia.OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);//防止实例化多个服务
    }

    /**
     * 使用eventBus代替广播
     *
     * @param position
     */
//    private class MyBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //接受穿过来的当前播放位置  ，防止再次点击时重新播放该歌曲
//            currentPosition = intent.getIntExtra("currentPosition", 0);
//            showData();
//        }
//    }
//
//    public void showData() {
//        showViewData();
//        checkPlayMode();
//    }

    //3.订阅方法
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void showData(Integer position) {
        Log.e("showData", "showData:" + position);
        //接受穿过来的当前播放位置  ，防止再次点击时重新播放该歌曲
        currentPosition = position;
        //发送消息开始歌词同步
        showLyric();
        showViewData();
        checkPlayMode();
        setupVisualizerFxAndUi();
    }

    private Visualizer mVisualizer;

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        try {
            int audioSessionId = service.getAudioSessionId();
            System.out.println("audioSessionid==" + audioSessionId);
            mVisualizer = new Visualizer(audioSessionId);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void showLyric() {
        LyricUtils lyricUtils = new LyricUtils();
        try {
            String path = service.getAudioPath();

            //得到歌词路径--和歌曲路径.mp3改成.lrc或者.text
            path = path.substring(0, path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if (!file.exists()) {
                file = new File(path + ".text");
            }
            lyricUtils.readLyricFile(file);//解析歌词
            tv_showLyricView.setLyrics(lyricUtils.getLyrics());//设置歌词列表
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        if (lyricUtils.isExistsLyric()) {

            mHandler.sendEmptyMessage(SHOW_LYRIC);
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart:");
        if (mVisualizer == null) {
            setupVisualizerFxAndUi();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause:");
        if (mVisualizer != null) {
            Log.e(TAG, "onPause:" + "mVisualizer != null");

            mVisualizer.release();
            mVisualizer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:");
        try {
            if (mVisualizer == null) {
                setupVisualizerFxAndUi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy:" );
        //取消广播
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
        //取消注册EventBus
        EventBus.getDefault().unregister(this);
        //解绑服务
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        //移除消息
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
