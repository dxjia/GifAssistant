/**
 *
 * Copyright  2014  Djia
 * All right reserved.
 *
 * Author: Djia, Created on 2014-6-30
 */
package com.wind.gifassistant.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wind.gifassistant.R;
import com.wind.gifassistant.gifworker.GifMerger;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;
import com.wind.gifassistant.views.VideoView;
import com.wind.gifassistant.views.waveview.Titanic;
import com.wind.gifassistant.views.waveview.TitanicTextView;
import com.wind.gifassistant.views.waveview.Typefaces;

import java.io.File;

/**
 * @author Djia 2014-6-30
 *         <p/>
 *         video play activity
 */
public class VideoPlayerActivity extends Activity {

    private final static String TAG = "VideoPlayerActivity";
    private final static boolean DEBUG = true;

    private int mPlayedTime = 0;

    private static VideoView mVideoView = null;
    private static SeekBar mSeekBar = null;
    private static TextView mDurationTextView = null;
    private static TextView mPlayedTextView = null;

    private ImageButton mCutBtn = null;
    private ImageButton mPlayPauseBtn = null;

    private View controlView = null;
    private static PopupWindow controler = null;

    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;
    private static int mControlHeight = 0;

    private final static int TIME = 5000;

    private boolean isPaused = false;

    private String mCurrentVideoPath;

    private TitanicTextView mWaveTextView;
    private Titanic mWaveTitanic;
    private TextView mProcessTipTextView;

    private SharedPreferences mSharedPreferences;

    private FloatingActionsMenu mGifSettingMenu;
    private FloatingActionButton mGifScaleSettingButton;
    private FloatingActionButton mGifMaxLengthSettingButton;
    private FloatingActionButton mGifRateSettingButton;

    private float mTouchPosX;
    private float mTouchPosY;
    private float mMaxDistance;
    private static float DEFAULT_MOVE_FLAG = 10;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
        // 禁止灭屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 从intent里取videos path
        mCurrentVideoPath = getIntent().getStringExtra(AppUtils.KEY_PATH);

        mSharedPreferences = getSharedPreferences(AppUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        initViews();
    }

    private void initViews() {
        controlView = getLayoutInflater().inflate(
                R.layout.video_player_controler, null);
        controler = new PopupWindow(controlView);
        mDurationTextView = (TextView) controlView.findViewById(R.id.duration);
        mPlayedTextView = (TextView) controlView.findViewById(R.id.has_played);

        mCutBtn = (ImageButton) controlView.findViewById(R.id.cut);
        mPlayPauseBtn = (ImageButton) controlView.findViewById(R.id.play_pause);

        mVideoView = (VideoView) findViewById(R.id.vv);

        if (!TextUtils.isEmpty(mCurrentVideoPath)) {
            if (mVideoView.getVideoHeight() == 0) {
                mVideoView.setVideoPath(mCurrentVideoPath);
            }
            mPlayPauseBtn.setImageResource(R.mipmap.pause);
        } else {
            mPlayPauseBtn.setImageResource(R.mipmap.play);
        }


        mCutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                generateGifFromCurrentPos();
            }
        });

        mPlayPauseBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                doPlayOrPause();
            }

        });


        mSeekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                if (fromUser) {
                    mVideoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                myHandler.removeMessages(HIDE_CONTROLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
            }
        });


        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer arg0) {
                // TODO Auto-generated method stub
                setVideoScale(SCREEN_DEFAULT);
                if (controler != null && !(controler.isShowing())) {
                    showController();
                }

                int i = mVideoView.getDuration();
                Log.d("onCompletion", "" + i);
                mSeekBar.setMax(i);
                i /= 1000;
                int minute = i / 60;
                int hour = minute / 60;
                int second = i % 60;
                minute %= 60;
                mDurationTextView.setText(String.format("%02d:%02d:%02d", hour,
                        minute, second));

                mVideoView.start();
                mPlayPauseBtn.setImageResource(R.mipmap.pause);
                hideControllerDelay();
                myHandler.sendEmptyMessage(PROGRESS_CHANGED);
            }
        });

        mGifSettingMenu = (FloatingActionsMenu) findViewById(R.id.gif_setting_floating_menu);
        mGifMaxLengthSettingButton = (FloatingActionButton) findViewById(R.id.action_max_length_setting);
        mGifMaxLengthSettingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaxLengthSettingDialog();
                mGifSettingMenu.toggle();
            }
        });
        mGifRateSettingButton = (FloatingActionButton) findViewById(R.id.action_rate_setting);
        mGifRateSettingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showRateSettingDialog();
                mGifSettingMenu.toggle();
            }
        });
        mGifScaleSettingButton = (FloatingActionButton) findViewById(R.id.action_scale_setting);
        mGifScaleSettingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showScaleSettingDialog();
                mGifSettingMenu.toggle();
            }
        });
    }

    private void showRateSettingDialog() {
        RangSettingDialog dialog = new RangSettingDialog(VideoPlayerActivity.this);
        dialog.setDialogTitle(R.string.gif_rate_setting_dialog_title);
        dialog.setTips(R.string.rate_rang_setting_tips);
        dialog.setUnitText(R.string.gif_rate_unit);
        dialog.setRangListener(new RateRangSettingListener(VideoPlayerActivity.this, mSharedPreferences));
        dialog.show();
    }

    private void showScaleSettingDialog() {
        GifScaleSettingDialog dialog = new GifScaleSettingDialog(VideoPlayerActivity.this);
        dialog.setDialogTitle(R.string.gif_quality_setting_dialog_title);
        dialog.show();
    }

    private void showMaxLengthSettingDialog() {
        RangSettingDialog dialog = new RangSettingDialog(VideoPlayerActivity.this);
        dialog.setDialogTitle(R.string.gif_max_length_setting_dialog_title);
        dialog.setTips(R.string.max_time_setting_tips);
        dialog.setUnitText(R.string.gif_max_time_length_unit);
        dialog.setRangListener(new MaxTimeRangSettingListener(VideoPlayerActivity.this, mSharedPreferences));
        dialog.show();
    }

    private void generateGifFromCurrentPos() {
        final int currentPos = mVideoView.getCurrentPosition() / 1000;
        int max = AppConfigs.getGifProductMaxTimeSetting(mSharedPreferences);
        int videoLength = mVideoView.getDuration();
        if ((currentPos + max) > videoLength) {
            max = videoLength - currentPos;
        }

        final String gifName = generateGifName(mCurrentVideoPath, currentPos, max);

        final Dialog progressDialog = createLoadingDialog(VideoPlayerActivity.this);

        progressDialog.show();
        mWaveTitanic = new Titanic();
        // start animation
        mWaveTitanic.start(mWaveTextView);

        final int finalMax = max;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String productName = AppUtils.GIF_PRODUCTS_FOLDER_PATH
                            + File.separator + gifName + ".gif";

                    //生产Gif
                    int rate = AppConfigs.getGifProductFrameRateSetting(mSharedPreferences);
                    int scale = AppConfigs.getGifProductScaleSetting(mSharedPreferences);

                    GifMerger.generateGifProduct(productName, mCurrentVideoPath,
                            currentPos, currentPos + finalMax, rate, scale);

                } finally {
                    progressDialog.dismiss();
                }
            }
        }).start();

    }


    /**
     * @param currentPos
     * @param max
     * @return
     */
    private String generateGifName(String videoPath, int currentPos, int max) {
        String gifN = "gif";
        File file = new File(videoPath);
        String name = file.getName();
        int i = name.indexOf('.');
        if (i != -1) {
            gifN = name.substring(0, i - 1);
        }
        gifN = gifN + "-" + currentPos + "-" + max;
        return gifN;
    }

    private void doPlayOrPause() {
        cancelDelayHide();
        if (isPaused) {
            mVideoView.start();
            mPlayPauseBtn.setImageResource(R.mipmap.pause);
            hideControllerDelay();
        } else {
            mVideoView.pause();
            mPlayPauseBtn.setImageResource(R.mipmap.play);
        }
        isPaused = !isPaused;
    }

    private final static int PROGRESS_CHANGED = 0;
    private final static int HIDE_CONTROLER = 1;

    private static Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {

                case PROGRESS_CHANGED:

                    int i = mVideoView.getCurrentPosition();
                    mSeekBar.setProgress(i);

                    i /= 1000;
                    int minute = i / 60;
                    int hour = minute / 60;
                    int second = i % 60;
                    minute %= 60;
                    mPlayedTextView.setText(String.format("%02d:%02d:%02d", hour,
                            minute, second));

                    sendEmptyMessage(PROGRESS_CHANGED);
                    break;

                case HIDE_CONTROLER:
                    hideController();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchPosX = event.getX();
            mTouchPosY = event.getY();
            mMaxDistance = 0;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            float disX = x > mTouchPosX ? (x - mTouchPosX) : (mTouchPosX - x);
            float disY = y > mTouchPosY ? (y - mTouchPosY) : (mTouchPosY - y);
            float dis = disX > disY ? disX : disY;
            if (dis > mMaxDistance) {
                mMaxDistance = dis;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (controler == null || mGifSettingMenu == null) {
                return super.onTouchEvent(event);
            }

            if (!controler.isShowing() && mGifSettingMenu.isExpanded()) {
                if (mMaxDistance < DEFAULT_MOVE_FLAG) {
                    mGifSettingMenu.toggle();
                }
                return super.onTouchEvent(event);
            }

            if (!(controler.isShowing())) {
                showController();
                hideControllerDelay();
            } else {
                cancelDelayHide();
                hideController();
            }

            if (mMaxDistance < DEFAULT_MOVE_FLAG) {
                if (mGifSettingMenu.isExpanded()) {
                    mGifSettingMenu.toggle();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mPlayedTime = mVideoView.getCurrentPosition();
        mVideoView.pause();
        mPlayPauseBtn.setImageResource(R.mipmap.play);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        getScreenSize();
        mVideoView.seekTo(mPlayedTime);
        mVideoView.start();
        if (mVideoView.getVideoHeight() != 0) {
            mPlayPauseBtn.setImageResource(R.mipmap.pause);
            hideControllerDelay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (controler.isShowing()) {
            controler.dismiss();
        }

        myHandler.removeMessages(PROGRESS_CHANGED);
        myHandler.removeMessages(HIDE_CONTROLER);

        super.onDestroy();
    }

    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();
        mControlHeight = mScreenHeight / 7;
    }

    private static void hideController() {
        if (controler.isShowing()) {
            controler.dismiss();
        }

    }

    private void hideControllerDelay() {
        myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
    }

    private void showController() {
        if (controler != null && mVideoView.isShown()) {
            controler.showAtLocation(mVideoView, Gravity.BOTTOM, 0, 0);
            controler.update(0, 0, mScreenWidth, mControlHeight);
        }
    }

    private void cancelDelayHide() {
        myHandler.removeMessages(HIDE_CONTROLER);
    }

    private final static int SCREEN_FULL = 0;
    private final static int SCREEN_DEFAULT = 1;

    private void setVideoScale(int flag) {

        switch (flag) {
            case SCREEN_FULL:

			/*Log.d(TAG, "screenWidth: " + mScreenWidth + " screenHeight: "
                    + mScreenHeight);
			mVideoView.setVideoScale(mScreenWidth, mScreenHeight);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

                break;

            case SCREEN_DEFAULT:

                int videoWidth = mVideoView.getVideoWidth();
                int videoHeight = mVideoView.getVideoHeight();
                int mWidth = mScreenWidth;
                int mHeight = mScreenHeight - 25;

                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {
                        mHeight = mWidth * videoHeight / videoWidth;
                    } else if (videoWidth * mHeight < mWidth * videoHeight) {
                        mWidth = mHeight * videoWidth / videoHeight;
                    } else {

                    }
                }

                mVideoView.setVideoScale(mWidth, mHeight);

                logd("size: [" + mWidth + ", " + mHeight + "]");



                break;
        }
    }


    private int Min(int pos1, int pos2) {
        return (pos1 > pos2) ? pos2 : pos1;
    }

    private int Max(int pos1, int pos2) {
        return (pos1 > pos2) ? pos1 : pos2;
    }

    private void logd(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }

    /**
     * 得到自定义的wave progress Dialog
     *
     * @param context
     * @return
     */
    public Dialog createLoadingDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.wave_process_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.wave_dialog);// 加载布局

        mWaveTextView = (TitanicTextView) v.findViewById(R.id.wave_text_view);
        // set fancy typeface
        mWaveTextView.setTypeface(Typefaces.get(context, "Satisfy-Regular.ttf"));

        mProcessTipTextView = (TextView) v.findViewById(R.id.tipTextView);
        Dialog loadingDialog = new Dialog(context);

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;

    }

}
