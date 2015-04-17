/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-7-1
 */
package com.wind.gifassistant.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppConfigs;

/**
 *
 * @author Djia
 * @time 2014-7-1上午10:45:37
 * @instuction 欢迎界面
 */
public class WelcomeActivity extends Activity implements AnimationListener {
	
	private ImageView  imageView = null;  
	private Animation alphaAnimation = null; 

    /**
	 * 播放一个动画进行过度。。。
	 */
	public WelcomeActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 super.onCreate(savedInstanceState);
         setContentView(R.layout.welcome);
         imageView = (ImageView)findViewById(R.id.welcome_image_view);  
         alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);  
         alphaAnimation.setFillEnabled(true); //启动Fill保持  
         alphaAnimation.setFillAfter(true);  //设置动画的最后一帧是保持在View上面  
         imageView.setAnimation(alphaAnimation);  
         alphaAnimation.setAnimationListener(this);  //为动画设置监听 
         doInitWork();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}

	private void doInitWork() {
		//AppConfigs.checkAndCreateNecessaryFolders();
	}

	/* (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		//动画结束时结束欢迎界面并转到软件的主界面  
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		WelcomeActivity.this.startActivity(intent);
		WelcomeActivity.this.finish();
	}

	/* (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
