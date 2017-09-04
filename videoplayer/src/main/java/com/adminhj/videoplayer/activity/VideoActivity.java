package com.adminhj.videoplayer.activity;

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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adminhj.videoplayer.R;
import com.adminhj.videoplayer.ben.LocalVideoInfo;
import com.adminhj.videoplayer.utils.Utils;
import com.adminhj.videoplayer.wedigt.CustomToast;
import com.adminhj.videoplayer.wedigt.MyVideoView;

import java.util.List;

import static com.adminhj.videoplayer.utils.Utils.durationFromat;
import static com.adminhj.videoplayer.utils.Utils.getSystemTime;

/**
 * Created by AdminHeJun on 2017/7/2.
 * </p>
 * Content:自由影音播放控件
 * </p>
 * Modified:
 * </p>
 * Version:
 * </p>
 */

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * update system time
     */
    private static final int SYS_TIME = 3;
    /**
     * update network speed
     */
    private static final int SHOW_SPEED = 4;
    /**
     * 是否是 系统自带监听卡顿
     */
    private boolean isUseSystem = true;
    /**
     * update progressbar
     */
    private static final int PROGRESS = 1;
    /**
     * hide control panel
     */
    private static final int HIDE_CONTROLLER = 2;
    /**
     * full screen
     */
    private static final int FULL_SCREEN = 100;
    /**
     * acquiescence screen
     */
    private static final int DEFAULT_SCREEN = 102;
    private MyVideoView videoView;
    /**
     * pass to uri
     */
    private Uri uri;
    private ImageView mBack;
    private ImageView mBattery;
    private ImageView mPrevious;
    private ImageView mPlay;
    private ImageView mNext;
    private ImageView mFullScreen;

    private TextView mName;
    private TextView mSysTime;
    private TextView mCurrentTime;
    private TextView mDuration;

    /**
     * show network loading speed
     */
    private LinearLayout mNetShow;
    /**
     * show network speed view
     */
    private TextView mBuffer;
    /**
     * video play progressbar
     */
    private SeekBar mSeekBar;

    /**
     * loading page
     */
    private LinearLayout mLoading;
    /**
     * loading progressBar
     */
    private ProgressBar mLoadBar;
    /**
     * loading hint
     */
    private TextView mLoadText;
    private MyBatteryReciver batteryReciver;
    private List<LocalVideoInfo> videolist;

    private RelativeLayout media_contorler;
    private boolean isShowMediaController = false;
    /**
     * list position
     */
    private int position;

    /**
     * 手势识别器
     */
    private GestureDetector detector;
    /**
     * is full screen
     */
    private boolean isFullScreen = false;

    /**
     * screen width
     */
    private int screenWidth = 0;
    /**
     * screen height
     */
    private int screenHeight = 0;
    /**
     * video width
     */
    private int videoWidth;
    /**
     * video height
     */
    private int videoHeight;
    /**
     * custom toast
     */
    private CustomToast customToast;
    /**
     * audio manager
     */
    private AudioManager audioManager;

    /**
     * max voice
     * 0~15
     */
    private int maxVoice;

    /**
     * 手指按下时的X
     */
    private float startX;
    /**
     * 手指按下时的Y
     */
    private float startY;

    /**
     * 手指按下时屏幕的高
     */
    private float touchRang;
    /**
     * 手指按下时屏幕的宽度
     */
    private float touchWidth;
    /**
     * 手指按下时的音量
     */
    private int downVol;
    /**
     * 按下时的进度
     */
    private int downProgress;
    /**
     * 判断是否是网络资源
     */
    private boolean netUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setView();
        mHandler.sendEmptyMessage(SYS_TIME);
    }


    private void setView() {
        findViews();
        setLinistener();
        hideMediaController();
        getData();
        initData();
        mHandler.sendEmptyMessage(SHOW_SPEED);
        if (netUri) {
            if (Utils.isWifi(this)) {
                customToast.showToast("", "正在使用WIFI观看");
            } else {
                customToast.showToast("", "正在使用数据流量观看");
            }
        }
    }

    private void initData() {
        batteryReciver = new MyBatteryReciver();
        IntentFilter intentFilter = new IntentFilter();
        customToast = CustomToast.getInstance(VideoActivity.this);
        //电量变化时发的广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReciver, intentFilter);
        //实例化手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isFullScreen) {
                    setVideoType(DEFAULT_SCREEN);
                } else {
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    hideMediaController();
                    mHandler.removeMessages(HIDE_CONTROLLER);
                } else {
                    showMediaController();
                    mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        screenWidth = Utils.getScreenWidth(this);
        screenHeight = Utils.getScreenHeight(this);
        //得到声音
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置videoview是否全屏播放
     *
     * @param fullScreen
     */
    private void setVideoType(int fullScreen) {
        switch (fullScreen) {
            case FULL_SCREEN:
                isFullScreen = true;
                videoView.setVideoSize(screenWidth, screenHeight);
                mFullScreen.setImageResource(R.drawable.video_switch_scrren_selector);
                break;
            case DEFAULT_SCREEN:
                isFullScreen = false;
                //视频真正的宽高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                //屏幕的宽高
                int width = screenWidth;
                int height = screenHeight;
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoView.setVideoSize(width, height);
                mFullScreen.setImageResource(R.drawable.video_switch_scrren_default);
                break;
        }
    }

    private void getData() {
        uri = getIntent().getData();

        videolist = (List<LocalVideoInfo>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
        setData();
    }

    private void setData() {
        if (videolist != null && videolist.size() > 0) {
            LocalVideoInfo info = videolist.get(position);
            mName.setText(info.getName());
            netUri = isNetUri(info.getData());
            videoView.setVideoPath(info.getData());
        } else if (uri != null) {
            mName.setText(uri.toString());
            netUri = isNetUri(uri.toString());
            videoView.setVideoURI(uri);
        } else {
            Toast.makeText(VideoActivity.this, "没有播放资源", Toast.LENGTH_SHORT).show();
        }
    }

    private void findViews() {
        videoView = (MyVideoView) findViewById(R.id.videoview);

        media_contorler = (RelativeLayout) findViewById(R.id.media_contorler);
        mNetShow = (LinearLayout) findViewById(R.id.ll_buffer);
        mBuffer = (TextView) findViewById(R.id.buffer_hint);
        mLoading = (LinearLayout) findViewById(R.id.ll_loading);
        mLoadText = (TextView) findViewById(R.id.tv_load_text);
        mLoadBar = (ProgressBar) findViewById(R.id.pb_load);
        mBack = (ImageView) findViewById(R.id.btn_video_back);
        mBattery = (ImageView) findViewById(R.id.iv_battery);
        mPrevious = (ImageView) findViewById(R.id.btn_previous);
        mPlay = (ImageView) findViewById(R.id.btn_pause);
        mNext = (ImageView) findViewById(R.id.btn_next);
        mFullScreen = (ImageView) findViewById(R.id.btn_fullsc);

        mName = (TextView) findViewById(R.id.tv_name);
        mSysTime = (TextView) findViewById(R.id.tv_system_time);
        mCurrentTime = (TextView) findViewById(R.id.tv_play_time);
        mDuration = (TextView) findViewById(R.id.tv_totle_time);

        mSeekBar = (SeekBar) findViewById(R.id.sb_video);
    }

    private void setLinistener() {
        //准备好了的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());

        //监听视频播放是否卡顿
        if (isUseSystem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyInfoListener());
            }
        } else {
            isUseSystem = false;
            Log.i("是否使用系统监听卡顿----", "不是啊！");
        }
        mSeekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        mBack.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_video_back) {
            finish();

        } else if (i == R.id.btn_previous) {
            playPreviousVideo();

        } else if (i == R.id.btn_pause) {
            startAndPause();
        } else if (i == R.id.btn_next) {
            playNextVideo();

        } else if (i == R.id.btn_fullsc) {
            if (isFullScreen) {
                setVideoType(DEFAULT_SCREEN);
            } else {
                setVideoType(FULL_SCREEN);
            }

        }
        mHandler.removeMessages(HIDE_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
    }

    /**
     * 播放和暫停
     */
    private void startAndPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
            mPlay.setImageResource(R.drawable.video_play_selector);
        } else {
            videoView.start();
            mPlay.setImageResource(R.drawable.video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreviousVideo() {
        if (videolist != null && videolist.size() > 0) {
            position--;
            if (position >= 0) {
                mLoading.setVisibility(View.VISIBLE);
                LocalVideoInfo info = videolist.get(position);
                mName.setText(info.getName());
                netUri = isNetUri(info.getData());
                videoView.setVideoPath(info.getData());
            } else {
                position++;
                Toast.makeText(VideoActivity.this, "没有上一个视频了", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(VideoActivity.this, "没有上一个视频了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (videolist != null && videolist.size() > 0) {
            position++;
            if (position < videolist.size()) {
                mLoading.setVisibility(View.VISIBLE);
                LocalVideoInfo info = videolist.get(position);
                mName.setText(info.getName());
                netUri = isNetUri(info.getData());
                videoView.setVideoPath(info.getData());
            } else {
                position--;
                Toast.makeText(VideoActivity.this, "没有下一个视频了", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(VideoActivity.this, "没有下一个视频了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上一次的播放进度
     */
    private int preCurrentProgress = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SYS_TIME:
                    mSysTime.setText(getSystemTime());
                    mHandler.removeMessages(SYS_TIME);
                    mHandler.sendEmptyMessageDelayed(SYS_TIME, 1000 * 60);
                    break;
                case PROGRESS:
                    int currentProgress = videoView.getCurrentPosition();
                    mSeekBar.setProgress(currentProgress);
                    mCurrentTime.setText(Utils.durationFromat(currentProgress));

                    if (netUri) {
                        int bufferPercentage = videoView.getBufferPercentage();
                        int totleBuffer = mSeekBar.getMax() * bufferPercentage;
                        int secondaryProgress = totleBuffer / 100;
                        mSeekBar.setSecondaryProgress(secondaryProgress);
                    } else {
                        mSeekBar.setSecondaryProgress(0);
                    }
                    //自定义监听卡
                    if (!isUseSystem) {
                        if (videoView.isPlaying()) {
                            int buf = currentProgress - preCurrentProgress;
                            if (buf < 500) {
                                mNetShow.setVisibility(View.VISIBLE);
                            } else {
                                mNetShow.setVisibility(View.GONE);
                            }
                        } else {
                            mNetShow.setVisibility(View.GONE);
                        }
                    }
                    mLoading.setVisibility(View.GONE);
                    preCurrentProgress = currentProgress;
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_CONTROLLER:
                    hideMediaController();
                    break;
                case SHOW_SPEED:
                    String netSpeed = Utils.getNetSpeed(VideoActivity.this);
                    if (netUri) {
                        mLoadText.setText("正在玩命加载..." + netSpeed);
                    } else {
                        mLoadText.setText("正在玩命加载...");
                    }
                    mBuffer.setText("正在玩命加载..." + netSpeed);
                    removeMessages(SHOW_SPEED);
                    sendEmptyMessageDelayed(PROGRESS, 2000);
                    break;
            }

        }
    };

    /**
     * 电量监听广播
     */
    class MyBatteryReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//0~100
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            mBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            mBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            mBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            mBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            mBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            mBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            mBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            mBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 滑动时回调
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        /**
         * 触碰时回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(HIDE_CONTROLLER);
        }

        /**
         * 手指离开时回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
        }
    }

    /**
     * 系统监听卡顿
     */
    class MyInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡，拖动卡
                    mNetShow.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://卡顿结束
                    mNetShow.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    /**
     * 准备好播放
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoView.start();
            int duration = videoView.getDuration();
            mSeekBar.setMax(duration);
            mDuration.setText(durationFromat(duration));
            setVideoType(DEFAULT_SCREEN);
            setPlayBtnStatus(false);
            hideMediaController();
            mHandler.sendEmptyMessage(PROGRESS);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mHandler.removeMessages(PROGRESS);
            setPlayBtnStatus(true);
            mLoading.setVisibility(View.VISIBLE);
            mLoadBar.setVisibility(View.GONE);
            return true;
        }
    }


    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (videolist != null && videolist.size() > 0) {
                position++;
                if (position < videolist.size()) {
                    LocalVideoInfo info = videolist.get(position);
                    mName.setText(info.getName());
                    videoView.setVideoPath(info.getData());
                } else {
                    position--;
                    setPlayBtnStatus(true);
                    Toast.makeText(VideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
                }
            } else {
                setPlayBtnStatus(true);
                Toast.makeText(VideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (batteryReciver != null) {
            unregisterReceiver(batteryReciver);
            batteryReciver = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传递给手势识别器（注：对事件只进行了解析处理，没有拦截，解析成手势识别的单击、双击、长按）
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                startX = event.getX();
                touchRang = Math.min(screenHeight, screenWidth);
                touchWidth = Math.max(
                        screenHeight, screenWidth);
                downVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                downProgress = videoView.getCurrentPosition();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                float endX = event.getX();
                float distanceY = startY - endY;
                float distanceX = endX - startX;
                if (startX > touchWidth / 4 * 3) {
                    //屏幕右半部分上滑，声音变大，下滑，声音变小
                    float changVoice = (distanceY / touchRang) * maxVoice;
                    //改变后的声音
                    int volume = (int) Math.min(Math.max(0, downVol + changVoice), maxVoice);
                    setAudio(volume);
                } else if (startX < touchWidth / 4) {
                    //屏幕左半部分上滑，亮度变大，下滑，亮度变小
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distanceY > FLING_MIN_DISTANCE && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setLightness(10);
                    }
                    if (distanceY < FLING_MIN_DISTANCE
                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                        setLightness(-10);
                    }
                } else if (startX > touchWidth / 4 && startX < touchWidth / 4 * 3) {
                    float changProgress = (distanceX / (touchWidth * 2)) * videoView.getDuration();
                    //改变后的进度
                    int endProgress = (int) Math.min(Math.max(0, downProgress + changProgress), videoView.getDuration());
                    videoView.seekTo(endProgress);
                    customToast.showToast(durationFromat(endProgress) + "/", durationFromat(videoView.getDuration()));
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置播放控件状态
     *
     * @param isPlay true为暂停状态 false为播放状态
     */
    private void setPlayBtnStatus(boolean isPlay) {
        if (isPlay) {
            mPlay.setImageResource(R.drawable.video_play_selector);
        } else {
            mPlay.setImageResource(R.drawable.video_pause_selector);
        }
    }

    /**
     * 控制栏隐藏
     */
    private void showMediaController() {
        media_contorler.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    /**
     * 控制栏隐藏
     */
    private void hideMediaController() {
        media_contorler.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    /**
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     *
     * @param brightness
     */

    public void setLightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0) {
            lp.screenBrightness = 0;
        }
        getWindow().setAttributes(lp);
        float sb = lp.screenBrightness;
        customToast.showToast("亮度:", (int) Math.ceil(sb * 100) + "%");
    }

    /**
     * 加减音量
     *
     * @param volume
     */
    public void setAudio(int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 1);
    }

    /**
     * 判断uri是不是网络地址
     *
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean result = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }
}
