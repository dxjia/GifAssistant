package com.wind.gifassistant.ui.naviwelcome;

import java.util.ArrayList;
import java.util.List;

import com.wind.gifassistant.R;
import com.wind.gifassistant.ui.MainActivity;
import com.chenupt.springindicator.SpringIndicator;
import com.ryanharter.viewpager.PagerAdapter;
import com.ryanharter.viewpager.ViewPager;
import com.ryanharter.viewpager.ViewPager.OnPageChangeListener;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class NavigationWelcomeActivity extends FragmentActivity implements
		OnPageChangeListener {

	private ViewPager mViewPager;
	private VerticalFragementPagerAdapter mPagerAdapter;

	SpringIndicator mSpringIndicator;

	private List<View> mViewPagersList = new ArrayList<View>();

	private static int VIEW_PAGER_1 = 0;
	private static int VIEW_PAGER_2 = 1;
	private static int VIEW_PAGER_3 = 2;
	private static int VIEW_PAGER_4 = 3;
	private static int VIEW_PAGER_MAX = 65535;

	private int mPreViewPagerIndex = VIEW_PAGER_1;
	private AnimationDrawable mNav1TimeShowAnimationDrawable;

	/**
	 * 定义第一个引导界面里的动画VIEW，都是以imageview进行定义 再使用PropertyAnimation作用于VIEW
	 */
	// 时间变换动画
	private ImageView mNav1TimeShowImageView;
	// 旋转的电池图标
	private ImageView mNav1BatteryImageView;
	// 顶上的固定图片，以缩放动画出现，之后固定不变
	private ImageView mNav1TopStaticImageView;

	/**
	 * 定义第二个引导页面的动画VIEW
	 */
	// 顶上渐入的静态图片
	private ImageView mNav2TopStaticImageView;
	// 中间部分膨大显示的图片
	private ImageView mNav2MiddleEveryThingShowImageView;

	/**
	 * 定义第三个引导页面的动画VIEW，该动画中有移动的云彩,
	 * 而且是在中间一块特定区域内，所以需要在布局加载后计算位置再应用动画
	 */

	// 顶部静态图片
	private ImageView mNav3TopStaticImageView;

	// 4张云彩图片
	private ImageView mNav3CloudImageView1;
	private ImageView mNav3CloudImageView2;
	private ImageView mNav3CloudImageView3;
	private ImageView mNav3CloudImageView4;
	// 火箭
	private ImageView mRocketImageView;

	// 中间火箭所在区域
	private RelativeLayout mRocketAreaCenterLayout;
	// 动画位移坐标
	private int mCloudFromX1, mCloudFromY1, mCloudToX1, mCloudToY1;
	private int mCloudFromX2, mCloudFromY2, mCloudToX2, mCloudToY2;
	private int mCloudFromX3, mCloudFromY3, mCloudToX3, mCloudToY3;
	private int mCloudFromX4, mCloudFromY4, mCloudToX4, mCloudToY4;

	private ObjectAnimator mCloudTransAnimationX1, mCloudTransAnimationY1;
	private ObjectAnimator mCloudTransAnimation2;
	private ObjectAnimator mCloudTransAnimation3;
	private ObjectAnimator mCloudTransAnimation4;

	private boolean mCurrentPager3Flag = false;
	
	private int mCurrentPagerPosition;

	private AnimationDrawable mRocketAnimationDrawable;

	
	/**
	 * 定义第四个引导页面的动画VIEW
	 */
	private ImageView mNav4TopImageView;
	private ImageView mNav4bottomTextImageView;
	private Button mNav4Button;
	
	
	/**
	 * SKIP BUTTON AREA
	 */
	private FrameLayout mSkipArea;
	private Button mSkipWelcomButton;
	private boolean mMouseMoving = false;
	

	/**
	 * 程序首次使用引导界面 争取做到酷炫
	 */
	public NavigationWelcomeActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 检查是否是第一次启动，如果不是，说明已经运行过引导
		// 接跳转到正常界面
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_welcome_activity);

		mViewPager = (ViewPager) findViewById(R.id.navigation_welcome_viewpager);
		
		initViewPagers();

		// view pager adapter
		mPagerAdapter = new VerticalFragementPagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);

		mSpringIndicator = (SpringIndicator) findViewById(R.id.indicator);
		mSpringIndicator.setViewPager(mViewPager);
		
		mSkipArea = (FrameLayout) findViewById(R.id.skip_backgroud_area);
		mSkipWelcomButton = (Button) mSkipArea.findViewById(R.id.skip_wlecom_button);
		mSkipWelcomButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(NavigationWelcomeActivity.this, MainActivity.class);
				NavigationWelcomeActivity.this.startActivity(intent);
				NavigationWelcomeActivity.this.finish();
			}
		});

		doAnimAction(VIEW_PAGER_1);
	}

	private void initViewPagers() {
		initPager1();
		initPager2();
		initPager3();
		initPager4();
	}

	private void initPager1() {
		// 初始化第一页
		View navView1 = LayoutInflater.from(this).inflate(R.layout.navigation_1_layout, null);
		mNav1TimeShowImageView = (ImageView) navView1.findViewById(R.id.nav_1_time_show);
		mNav1BatteryImageView = (ImageView) navView1.findViewById(R.id.nav_1_battery_show);
		mNav1TopStaticImageView = (ImageView) navView1.findViewById(R.id.nav_1_static_image);
		mViewPagersList.add(navView1);
	}
	
	private void initPager2() {
		// 初始化第二页
		View navView2 = LayoutInflater.from(this).inflate(R.layout.navigation_2_layout, null);
		mNav2TopStaticImageView = (ImageView) navView2.findViewById(R.id.nav_2_top_static);
		mNav2MiddleEveryThingShowImageView = (ImageView) navView2.findViewById(R.id.nav_2_everything_show);
		mViewPagersList.add(navView2);	
	}
	
	private void initPager3() {
		// 初始化第三页
		View navView3 = LayoutInflater.from(this).inflate(R.layout.navigation_3_layout, null);
		mNav3TopStaticImageView = (ImageView) navView3.findViewById(R.id.nav_3_top_static);
		mRocketImageView = (ImageView) navView3.findViewById(R.id.rocket_view);
		mNav3CloudImageView1 = (ImageView) navView3.findViewById(R.id.nav_3_cloud_1);
		mNav3CloudImageView2 = (ImageView) navView3.findViewById(R.id.nav_3_cloud_2);
		mNav3CloudImageView3 = (ImageView) navView3.findViewById(R.id.nav_3_cloud_3);
		mNav3CloudImageView4 = (ImageView) navView3.findViewById(R.id.nav_3_cloud_4);
		mRocketAreaCenterLayout = (RelativeLayout) navView3.findViewById(R.id.center_rocket_area);
		mViewPagersList.add(navView3);
		// 增加layout listener，以便在layout结束后计算坐标
		navView3.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						int rocketAreaHeightTop = mRocketAreaCenterLayout
								.getTop();
						int rocketAreaHeightBottom = mRocketAreaCenterLayout
								.getBottom();
						// 获取屏幕的宽度
						DisplayMetrics dm = getApplicationContext()
								.getResources().getDisplayMetrics();
						int screenWidth = dm.widthPixels;

						mCloudFromX1 = mNav3CloudImageView1.getTop() + mNav3CloudImageView1.getHeight();
						mCloudFromY1 = -mNav3CloudImageView1.getTop() - mNav3CloudImageView1.getHeight();
						mCloudToX1 = -mNav3CloudImageView1.getWidth() - mNav3CloudImageView1.getLeft();
						mCloudToY1 = mNav3CloudImageView1.getTop() + mNav3CloudImageView1.getLeft()
								+ mNav3CloudImageView1.getWidth();

						mCloudFromX2 = mNav3CloudImageView2.getTop() + mNav3CloudImageView2.getHeight();
						mCloudFromY2 = -mNav3CloudImageView2.getTop() - mNav3CloudImageView2.getHeight();
						mCloudToX2 = -mNav3CloudImageView2.getWidth() - mNav3CloudImageView2.getLeft();
						mCloudToY2 = mNav3CloudImageView2.getTop() + mNav3CloudImageView2.getLeft()
								+ mNav3CloudImageView2.getWidth();

						mCloudFromX3 = screenWidth - mNav3CloudImageView3.getLeft();
						mCloudFromY3 = -(screenWidth - mNav3CloudImageView3.getLeft());
						mCloudToX3 = -(rocketAreaHeightBottom - rocketAreaHeightTop - mNav3CloudImageView3.getTop());
						mCloudToY3 = rocketAreaHeightBottom - rocketAreaHeightTop - mNav3CloudImageView3.getTop();

						mCloudFromX4 = screenWidth - mNav3CloudImageView4.getLeft();
						mCloudFromY4 = -(screenWidth - mNav3CloudImageView4.getLeft());
						mCloudToX4 = -(rocketAreaHeightBottom - rocketAreaHeightTop - mNav3CloudImageView4.getTop());
						mCloudToY4 = rocketAreaHeightBottom - rocketAreaHeightTop - mNav3CloudImageView4.getTop();
					}
				});

	}
	
	private void initPager4() {
		// 初始化第四页
		View navView4 = LayoutInflater.from(this).inflate(R.layout.navigation_4_layout, null);
		mNav4TopImageView = (ImageView) navView4.findViewById(R.id.nav_4_zhaopai);
		mNav4bottomTextImageView = (ImageView) navView4.findViewById(R.id.nav_4_bottom_text);
		mNav4Button = (Button) navView4.findViewById(R.id.nav_4_button);
		mNav4Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(NavigationWelcomeActivity.this, MainActivity.class);
				NavigationWelcomeActivity.this.startActivity(intent);
				NavigationWelcomeActivity.this.finish();
			}
		});
		mViewPagersList.add(navView4);
	}
	
	private class VerticalFragementPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViewPagersList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == o;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(mViewPagersList.get(position));
			return mViewPagersList.get(position);

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub
		mSpringIndicator.onPageScrolled(position, positionOffset,
				positionOffsetPixels);
		if (positionOffset == 0 && positionOffsetPixels == 0) {
			mMouseMoving = false;
		} else {
			mMouseMoving = true;
		}
		//Log.d("Djia", "position = " + position + ", positionOffset = " + positionOffset + ", positionOffsetPixels = " + positionOffsetPixels);
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		// 进入ViewPager某一页开始动画，position标示哪一页
		//Log.v("Djia", "onPageSelected, position = " + position);
		mCurrentPagerPosition = position;
		if (position == 3) {
			mSkipArea.setVisibility(View.INVISIBLE);
		} else {
			mSkipArea.setVisibility(View.VISIBLE);
		}
		mSpringIndicator.onPageSelected(position);
		doAnimAction(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
		//Log.e("Djia", "onPageScrollStateChanged, state = " + state);
		//hideSkipAreaWhenScrolling(state);
		mSpringIndicator.onPageScrollStateChanged(state);
	}
	
	private AnimatorSet mSkipButtonShow;
	private void hideSkipAreaWhenScrolling(int state) {
		if (state == 1 && mMouseMoving) {
			mSkipArea.setVisibility(View.INVISIBLE);
		} else {
			if (mSkipArea.getVisibility() == View.VISIBLE) {
				return;				
			}
			if (mSkipButtonShow != null && mSkipButtonShow.isRunning()) {
				return;
			}
			mSkipButtonShow = (AnimatorSet) AnimatorInflater
					.loadAnimator(NavigationWelcomeActivity.this,
							R.anim.nav_2_everything_show);
			mSkipArea.setVisibility(View.VISIBLE);
			mSkipButtonShow
					.setTarget(mSkipArea);
			mSkipButtonShow.start();
		}
	}

	private void doAnimAction(int pagerIndex) {
		switch (pagerIndex) {
		case 0:
			doViewPagerAnimation1(pagerIndex);
			break;
		case 1:
			doViewPagerAnimation2(pagerIndex);
			break;
		case 2:
			doViewPagerAnimation3(pagerIndex);
			break;
		case 3:
			doViewPagerAnimation4(pagerIndex);
			break;
		default:
			break;
		}

		mPreViewPagerIndex = pagerIndex;

	}

	private AnimatorSet mNavBatteryRotateAnimationSet;
	private AnimatorSet mNavTopStaticAnimationSet;

	private void doViewPagerAnimation1(int pagerIndex) {
		if (mPreViewPagerIndex > pagerIndex) {
			mNav2MiddleEveryThingShowImageView.setVisibility(View.INVISIBLE);
		}

		// 时间变换动画
		mNav1TimeShowImageView
				.setImageResource(R.drawable.nav_1_time_show_animation);
		mNav1TimeShowAnimationDrawable = (AnimationDrawable) mNav1TimeShowImageView
				.getDrawable();
		mNav1TimeShowAnimationDrawable.start();

		// 电池图标旋转动画
		mNavBatteryRotateAnimationSet = (AnimatorSet) AnimatorInflater
				.loadAnimator(NavigationWelcomeActivity.this,
						R.anim.nav_1_battery_rotate);
		LinearInterpolator lin = new LinearInterpolator();
		mNavBatteryRotateAnimationSet.setInterpolator(lin);
		mNav1BatteryImageView.setVisibility(View.VISIBLE);
		mNavBatteryRotateAnimationSet.setTarget(mNav1BatteryImageView);
		mNavBatteryRotateAnimationSet.start();

		// 上部图片进入动画
		mNavTopStaticAnimationSet = (AnimatorSet) AnimatorInflater
				.loadAnimator(NavigationWelcomeActivity.this,
						R.anim.nav_1_zoom_top);
		mNavTopStaticAnimationSet.setTarget(mNav1TopStaticImageView);
		mNavTopStaticAnimationSet.start();

	}

	private AnimatorSet mNav2AnimationEverythingShow;

	private void doViewPagerAnimation2(int pagerIndex) {
		if (mPreViewPagerIndex > pagerIndex) {
			// 从图3退到了图2，需要停止图3上的动画
			mCurrentPager3Flag = false;
			if (mCloudTransAnimationX1.isRunning()) {
				mCloudTransAnimationX1.cancel();
				mCloudTransAnimationY1.cancel();

				mCloudTransAnimation2.cancel();
				mCloudTransAnimation3.cancel();
				mCloudTransAnimation4.cancel();
			}
			mNav3CloudImageView1.setVisibility(View.INVISIBLE);
			mNav3CloudImageView2.setVisibility(View.INVISIBLE);
			mNav3CloudImageView3.setVisibility(View.INVISIBLE);
			mNav3CloudImageView4.setVisibility(View.INVISIBLE);
			mRocketAnimationDrawable.stop();
		} else {
			// 是从图1前进到图2，所以要停止图1上的动画
			mNav1TimeShowAnimationDrawable.stop();
			mNavBatteryRotateAnimationSet.cancel();
			mNav1BatteryImageView.setVisibility(View.INVISIBLE);
		}

		mNav2AnimationEverythingShow = (AnimatorSet) AnimatorInflater
				.loadAnimator(NavigationWelcomeActivity.this,
						R.anim.nav_2_everything_show);
		mNav2MiddleEveryThingShowImageView.setVisibility(View.VISIBLE);
		mNav2AnimationEverythingShow
				.setTarget(mNav2MiddleEveryThingShowImageView);
		mNav2AnimationEverythingShow.start();

		mNavTopStaticAnimationSet.setTarget(mNav2TopStaticImageView);
		mNavTopStaticAnimationSet.start();
	}

	private void doViewPagerAnimation3(int pagerIndex) {
		mRocketImageView.setImageResource(R.drawable.nav_3_rocket_animation);
		mRocketAnimationDrawable = (AnimationDrawable) mRocketImageView
				.getDrawable();

		mCloudTransAnimationX1 = ObjectAnimator.ofFloat(mNav3CloudImageView1,
				"translationX", mCloudFromX1, mCloudToX1);
		mCloudTransAnimationX1.setDuration(800);
		mCloudTransAnimationX1.setRepeatCount(Animation.INFINITE);// Animation.INFINITE
		mCloudTransAnimationX1.setRepeatMode(Animation.RESTART);
		mCloudTransAnimationX1.setInterpolator(new LinearInterpolator());

		mCloudTransAnimationY1 = ObjectAnimator.ofFloat(mNav3CloudImageView1,
				"translationY", mCloudFromY1, mCloudToY1);
		mCloudTransAnimationY1.setDuration(800);
		mCloudTransAnimationY1.setRepeatCount(Animation.INFINITE);// Animation.INFINITE
		mCloudTransAnimationY1.setRepeatMode(Animation.RESTART);
		mCloudTransAnimationY1.setInterpolator(new LinearInterpolator());

		PropertyValuesHolder pvhX3 = PropertyValuesHolder.ofFloat(
				"translationX", mCloudFromX2, mCloudToX2);
		PropertyValuesHolder pvhY3 = PropertyValuesHolder.ofFloat(
				"translationY", mCloudFromY2, mCloudToY2);
		mCloudTransAnimation2 = ObjectAnimator.ofPropertyValuesHolder(
				mNav3CloudImageView2, pvhX3, pvhY3);
		mCloudTransAnimation2.setDuration(1200);
		mCloudTransAnimation2.setRepeatCount(Animation.INFINITE);
		mCloudTransAnimation2.setRepeatMode(Animation.RESTART);
		mCloudTransAnimation2.setInterpolator((new LinearInterpolator()));

		PropertyValuesHolder pvhX4 = PropertyValuesHolder.ofFloat(
				"translationX", mCloudFromX3, mCloudToX3);
		PropertyValuesHolder pvhY4 = PropertyValuesHolder.ofFloat(
				"translationY", mCloudFromY3, mCloudToY3);
		mCloudTransAnimation3 = ObjectAnimator.ofPropertyValuesHolder(
				mNav3CloudImageView3, pvhX4, pvhY4);
		mCloudTransAnimation3.setDuration(1200);
		mCloudTransAnimation3.setRepeatCount(Animation.INFINITE);
		mCloudTransAnimation3.setRepeatMode(Animation.RESTART);
		mCloudTransAnimation3.setInterpolator((new LinearInterpolator()));

		PropertyValuesHolder pvhX5 = PropertyValuesHolder.ofFloat(
				"translationX", mCloudFromX4, mCloudToX4);
		PropertyValuesHolder pvhY5 = PropertyValuesHolder.ofFloat(
				"translationY", mCloudFromY4, mCloudToY4);
		mCloudTransAnimation4 = ObjectAnimator.ofPropertyValuesHolder(
				mNav3CloudImageView4, pvhX5, pvhY5);
		mCloudTransAnimation4.setDuration(800);
		mCloudTransAnimation4.setRepeatCount(Animation.INFINITE);
		mCloudTransAnimation4.setRepeatMode(Animation.RESTART);
		mCloudTransAnimation4.setInterpolator((new LinearInterpolator()));

		mCurrentPager3Flag = true;

		// 延迟1秒
		new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				// TODO Auto-generated method stub
				if (mCurrentPager3Flag)
					super.dispatchMessage(msg);
			}

			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					mNav3CloudImageView1.setVisibility(View.VISIBLE);
					mNav3CloudImageView2.setVisibility(View.VISIBLE);
					mNav3CloudImageView3.setVisibility(View.VISIBLE);
					mNav3CloudImageView4.setVisibility(View.VISIBLE);

					mCloudTransAnimationX1.start();
					mCloudTransAnimationY1.start();

					mCloudTransAnimation2.start();
					mCloudTransAnimation3.start();
					mCloudTransAnimation4.start();

					mRocketAnimationDrawable.start();
				}
			};
		}.sendEmptyMessageDelayed(1, 1000);// 1秒

		mNavTopStaticAnimationSet.setTarget(mNav3TopStaticImageView);
		mNavTopStaticAnimationSet.start();
	}

	private void doViewPagerAnimation4(int pagerIndex) {
		// 停掉页面3的动画及显示
		mCurrentPager3Flag = false;
		if (mCloudTransAnimationX1.isRunning()) {
			mCloudTransAnimationX1.cancel();
			mCloudTransAnimationY1.cancel();

			mCloudTransAnimation2.cancel();
			mCloudTransAnimation3.cancel();
			mCloudTransAnimation4.cancel();
		}
		mNav3CloudImageView1.setVisibility(View.INVISIBLE);
		mNav3CloudImageView2.setVisibility(View.INVISIBLE);
		mNav3CloudImageView3.setVisibility(View.INVISIBLE);
		mNav3CloudImageView4.setVisibility(View.INVISIBLE);
		mRocketAnimationDrawable.stop();

		ObjectAnimator objAnim = ObjectAnimator.ofFloat(mNav4TopImageView, "rotation", 0f, 10f);
		CycleInterpolator interpolator = new CycleInterpolator(3.0f);
		objAnim.setStartDelay(500);
		objAnim.setDuration(3000);
		objAnim.setRepeatCount(Animation.INFINITE);// Animation.INFINITE
		objAnim.setRepeatMode(Animation.RESTART);
		objAnim.setInterpolator(interpolator);
		mNav4TopImageView.setPivotX(mNav4TopImageView.getWidth()*0.47f);
		mNav4TopImageView.setPivotY(mNav4TopImageView.getHeight()*0.05f);
		objAnim.start();
		
		mNavTopStaticAnimationSet.setTarget(mNav4bottomTextImageView);  
		mNavTopStaticAnimationSet.start();
	}
}
