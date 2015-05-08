/**
 *
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Author: Djia, Created on 2014-6-30
 */
package com.wind.gifassistant.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wind.gifassistant.R;
import com.wind.gifassistant.gifworker.GifMerger;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;
import com.wind.gifassistant.views.SoundView;
import com.wind.gifassistant.views.SoundView.OnVolumeChangedListener;
import com.wind.gifassistant.views.VideoView;
import com.wind.gifassistant.views.VideoView.SizeChangeLinstener;
import com.wind.gifassistant.views.waveview.Titanic;
import com.wind.gifassistant.views.waveview.TitanicTextView;
import com.wind.gifassistant.views.waveview.Typefaces;

import java.io.File;
import java.util.LinkedList;

/**
 * @author Djia 2014-6-30
 * 
 *         video play activity
 */
public class VideoPlayerActivity extends Activity {

	private final static String TAG = "VideoPlayerActivity";
	private final static boolean DEBUG = true;

	public static LinkedList<MovieInfo> mPlayList = new LinkedList<MovieInfo>();

	public class MovieInfo {
		String mDisplayName;
		String mPath;
	}

	private int mPlayedTime;

	private VideoView mVideoView = null;
	private SeekBar mSeekBar = null;
	private TextView mDurationTextView = null;
	private TextView mPlayedTextView = null;
	private AudioManager mAudioManager = null;

	private int mMaxVolume = 0;
	private int mCurrentVolume = 0;

	private ImageButton mOpenBtn = null;
	private ImageButton mActionBtn = null;
	private ImageButton mPlayPauseBtn = null;
	private ImageButton mVolumeBtn = null;

	private View controlView = null;
	private PopupWindow controler = null;

	private SoundView mSoundView = null;
	private PopupWindow mSoundWindow = null;

	private static int mScreenWidth = 0;
	private static int mScreenHeight = 0;
	private static int mControlHeight = 0;

	private final static int TIME = 5000;

	private boolean isPaused = false;
	private boolean isSilent = false;
	private boolean isSoundShow = false;

	private String mCurrentVideoPath;
	// 最大的时间选择区间，以S为单位
	private int mPositionSecondHead = -1;
	private int mPositionSecondTail = -1;
	private EditText mGifProductNameEditText;

	
	private TitanicTextView mWaveTextView;
	private Titanic mWaveTitanic;
	private TextView mProcessTipTextView;

    private SharedPreferences mSharedPreferences;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_player);
		// 禁止灭屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Log.d("OnCreate", getIntent().toString());
		

		controlView = getLayoutInflater().inflate(
				R.layout.video_player_controler, null);
		controler = new PopupWindow(controlView);
		mDurationTextView = (TextView) controlView.findViewById(R.id.duration);
		mPlayedTextView = (TextView) controlView.findViewById(R.id.has_played);

		mSoundView = new SoundView(this);
		mSoundView.setOnVolumeChangeListener(new OnVolumeChangedListener() {
			@Override
			public void setVolume(int index) {
				cancelDelayHide();
				updateVolume(index);
				hideControllerDelay();
			}
		});

		mSoundWindow = new PopupWindow(mSoundView);

		mOpenBtn = (ImageButton) controlView.findViewById(R.id.button1);
		mActionBtn = (ImageButton) controlView.findViewById(R.id.button2);
		mPlayPauseBtn = (ImageButton) controlView.findViewById(R.id.button3);
		mVolumeBtn = (ImageButton) controlView.findViewById(R.id.button5);

		mVideoView = (VideoView) findViewById(R.id.vv);

		Uri uri = getIntent().getData();
		if (uri != null) {
			if (mVideoView.getVideoHeight() == 0) {
				mVideoView.setVideoURI(uri);
			}
			mPlayPauseBtn.setImageResource(R.mipmap.pause);
		} else {
			mPlayPauseBtn.setImageResource(R.mipmap.play);
		}

		mVideoView.setSizeChangeLinstener(new SizeChangeLinstener() {

			@Override
			public void doThings() {
				// TODO Auto-generated method stub
				setVideoScale(SCREEN_DEFAULT);
			}

		});

		mOpenBtn.setAlpha(0xBB);
		mActionBtn.setAlpha(0xBB);
		mPlayPauseBtn.setAlpha(0xBB);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mCurrentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mVolumeBtn.setAlpha(findAlphaFromSound());

		mOpenBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				doPlayOrPause();
				int ms = mVideoView.getCurrentPosition();
				mPositionSecondHead = ms / 1000;
				Toast.makeText(VideoPlayerActivity.this, "请移动进度条选择时间区间",
						Toast.LENGTH_LONG).show();
			}
		});

        mSharedPreferences = getSharedPreferences(AppUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		mActionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int ms = mVideoView.getCurrentPosition();
				mPositionSecondTail = ms / 1000;
				int minPos = Min(mPositionSecondTail, mPositionSecondHead);
				int maxPos = Max(mPositionSecondTail, mPositionSecondHead);

                mPositionSecondHead = minPos;
                mPositionSecondTail = maxPos;
				int duration = maxPos - minPos;

				if (duration == 0) {
					return;
				}

                int max = AppConfigs.getGifProductMaxTimeSetting(mSharedPreferences);
                logd(mSharedPreferences.toString());
                logd("rate = " + max);
				if (duration > max) {
					Toast.makeText(
							VideoPlayerActivity.this,
							"很抱歉,你选定的时间区间超过了" + max
									+ "S，将按最大值处理。", Toast.LENGTH_LONG).show();
					duration = max;
					maxPos = minPos + duration;
				}

				showNameEditDialog(minPos, maxPos);

			}

		});

		mPlayPauseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doPlayOrPause();
			}

		});

		mVolumeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelDelayHide();
				if (isSoundShow) {
					mSoundWindow.dismiss();
				} else {
					if (mSoundWindow.isShowing()) {
						mSoundWindow.update(15, 0, SoundView.MY_WIDTH,
								SoundView.MY_HEIGHT);
					} else {
						mSoundWindow.showAtLocation(mVideoView, Gravity.RIGHT
								| Gravity.CENTER_VERTICAL, 15, 0);
						mSoundWindow.update(15, 0, SoundView.MY_WIDTH,
								SoundView.MY_HEIGHT);
					}
				}
				isSoundShow = !isSoundShow;
				hideControllerDelay();
			}
		});

		mVolumeBtn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (isSilent) {
					mVolumeBtn.setImageResource(R.mipmap.soundenable);
				} else {
					mVolumeBtn.setImageResource(R.mipmap.sounddisable);
				}
				isSilent = !isSilent;
				updateVolume(mCurrentVolume);
				cancelDelayHide();
				hideControllerDelay();
				return true;
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

		getScreenSize();

		new GestureDetector(new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(final MotionEvent e) {
				// TODO Auto-generated method stub
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onSingleTapConfirmed(final MotionEvent e) {
				// TODO Auto-generated method stub
				if (controler != null && !(controler.isShowing())) {
					showController();
					hideControllerDelay();
				} else {
					cancelDelayHide();
					hideController();
				}
				// return super.onSingleTapConfirmed(e);
				return true;
			}

			@Override
			public void onLongPress(final MotionEvent e) {
				// TODO Auto-generated method stub
				if (isPaused) {
					mVideoView.start();
					mPlayPauseBtn.setImageResource(R.mipmap.pause);
					cancelDelayHide();
					hideControllerDelay();
				} else {
					mVideoView.pause();
					mPlayPauseBtn.setImageResource(R.mipmap.play);
					cancelDelayHide();
					showController();
				}
				isPaused = !isPaused;
				// super.onLongPress(e);
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

		// 从intent里取videos path
		String path = getIntent().getStringExtra(AppUtils.KEY_PATH);
		if (!TextUtils.isEmpty(path)) {
			mVideoView.setVideoPath(path);
			mCurrentVideoPath = path;
		}
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

	private void showNameEditDialog(int minPos, int maxPos) {
		mGifProductNameEditText = new EditText(this);
		mGifProductNameEditText.setHint("输入要制作的GIF名字");

		new AlertDialog.Builder(this)
				.setTitle("输入名字")
				.setView(mGifProductNameEditText)
				.setPositiveButton("确定",new GifMakerWorkerListener(minPos, maxPos,getApplicationContext()))
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
	
	

	private final static int EVENT_PROCESS_UPDATE = 0;

	private class GifMakerWorkerListener implements
			DialogInterface.OnClickListener {
		private final Context mContext;
		private final int mMinPos;
		private final int mMaxPos;

		public GifMakerWorkerListener(int minPos, int maxPos, Context context) {
			mContext = context;
			mMinPos = minPos;
			mMaxPos = maxPos;
		}

		public void onClick(DialogInterface dialog, int which) {
			final String gifName = mGifProductNameEditText.getText().toString();

			/*final ProgressDialog progressDialog = new ProgressDialog(
					VideoPlayerActivity.this);
			progressDialog.setTitle("请稍等");
			progressDialog.setMessage("正在准备中...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMax(100);*/
			
			final Dialog progressDialog = createLoadingDialog(VideoPlayerActivity.this);

			// 显示进度
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub

					switch (msg.what) {
					case EVENT_PROCESS_UPDATE:
						String processStr = (String)msg.obj;
						logd("processInt = " + processStr);
						mProcessTipTextView.setText("制作中请稍侯" + processStr + "...");
						break;
					}
					super.handleMessage(msg);
				}
			};

			progressDialog.show();
			mWaveTitanic = new Titanic();
	        // start animation
			mWaveTitanic.start(mWaveTextView);

			new Thread(new Runnable() {
				public void run() {
					try {
						String productName = AppUtils.GIF_PRODUCTS_FOLDER_PATH
								+ File.separator + gifName + ".gif";

						/* 生产Gif */
                        int rate = AppConfigs.getGifProductFrameRateSetting(mSharedPreferences);
                        int scale = AppConfigs.getGifProductScaleSetting(mSharedPreferences);

						GifMerger.generateGifProduct(productName, mCurrentVideoPath,
                                mPositionSecondHead, mPositionSecondTail, rate, scale);

                        /* reset */
						mPositionSecondHead = mPositionSecondTail = -1;
					} finally {
						handler.removeMessages(0);
						/* mWaveTitanic.cancel(); */
						progressDialog.dismiss();
						/* VideoPlayerActivity.this.finish(); */
					}
				}
			}).start();
		}
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;

	private Handler myHandler = new Handler() {
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

		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (controler != null && !(controler.isShowing())) {
				showController();
				hideControllerDelay();
			} else {
				cancelDelayHide();
				hideController();
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
		mVideoView.seekTo(mPlayedTime);
		mVideoView.start();
		if (mVideoView.getVideoHeight() != 0) {
			mPlayPauseBtn.setImageResource(R.mipmap.pause);
			hideControllerDelay();
		}
		
		getScreenSize();
		Log.d("REQUEST", "NEW AD !");

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if (controler.isShowing()) {
			controler.dismiss();
		}
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
		}

		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);

		mPlayList.clear();

		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		mScreenHeight = display.getHeight();
		mScreenWidth = display.getWidth();
		mControlHeight = mScreenHeight / 4;
		logd("getScreenSize (" + mScreenHeight + ", " + mScreenWidth + ")");
	}

	private void hideController() {
		if (controler.isShowing()) {
			controler.dismiss();

		}

		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
		}
		isSoundShow = false;
	}

	private void hideControllerDelay() {
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	private void showController() {
		logd("showController size (" + mScreenHeight + ", " + mControlHeight + ")");
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
					// Log.i("@@@", "image too tall, correcting");
					mHeight = mWidth * videoHeight / videoWidth;
				} else if (videoWidth * mHeight < mWidth * videoHeight) {
					// Log.i("@@@", "image too wide, correcting");
					mWidth = mHeight * videoWidth / videoHeight;
				} else {

				}
			}

			mVideoView.setVideoScale(mWidth, mHeight);

			//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			break;
		}
	}

	private int findAlphaFromSound() {
		if (mAudioManager != null) {
			int alpha = mCurrentVolume * (0xCC - 0x55) / mMaxVolume + 0x55;
			return alpha;
		} else {
			return 0xCC;
		}
	}

	@SuppressWarnings("deprecation")
	private void updateVolume(int index) {
		if (mAudioManager != null) {
			if (isSilent) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			} else {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index,
						0);
			}
			mCurrentVolume = index;
			mVolumeBtn.setAlpha(findAlphaFromSound());
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
