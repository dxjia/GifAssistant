/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-11
 */
package com.wind.gifassistant.ui;

import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.views.gifview.GifView;
import com.wind.gifassistant.views.gifview.GifView.GifShowGravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author Djia
 * @time 2014-8-11下午3:05:32
 * @instuction 
 */
public class GifShowActivity extends Activity {

	private GifView mGifView = null;

	/**
	 * 
	 */
	public GifShowActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 super.onCreate(savedInstanceState);
         setContentView(R.layout.gif_show_activity);
         mGifView = (GifView) findViewById(R.id.gif_show);
         Intent intent = getIntent();
         String gifPath = intent.getStringExtra(AppConfigs.KEY_PATH);
         mGifView.setGifShowGravity(GifShowGravity.CENTER_FULL);
         mGifView.showGifImage(gifPath);
	}
}
