/**
 *
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Author: Djia, Created on 2014-6-27
 */
package com.wind.gifassistant.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

/**
 * @author Djia 2014-6-27 main activity -- GIF list and Video list
 */
public class MainActivity extends FragmentActivity  implements View.OnClickListener {
	private static final String TAG = AppConfigs.APP_TAG + "MainActivity";
	private static final boolean DEBUG = true;
	
    private ResideMenu resideMenu;
    private ResideMenuItem itemGifProductsList;
    private ResideMenuItem itemVideosList;
    private ResideMenuItem itemExit;
    private ResideMenuItem itemSettings;
    
    private static final int CURRENT_FRAGMENT_SHOW_GIFS_LIST = 0;
    private static final int CURRENT_FRAGMENT_SHOW_VIDEOS_LIST = 1;
    private static final int CURRENT_FRAGMENT_SHOW_SETTINGS_LIST = 2;
    
    private int mCurrentFragmentShow;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AppConfigs.checkAndCreateNecessaryFolders();

		setContentView(R.layout.main_activity);
		setupResideMenu();

		if( savedInstanceState == null ) {
            changeFragment(new GifProductsListFragment());
            mCurrentFragmentShow = CURRENT_FRAGMENT_SHOW_GIFS_LIST;
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();

	}

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void setupResideMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        // get backgroud image from setting
        Uri backgroudUri = AppConfigs.getResideMenuConfig(getSharedPreferences(AppUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE));
        if (backgroudUri != null) {
            resideMenu.setBackground(backgroudUri);
        } else {
            resideMenu.setBackground(R.mipmap.menu_background);
        }
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(mResideMenuListener);

        // create menu items;
        itemGifProductsList     = new ResideMenuItem(this, R.mipmap.icon_home,     "作品库");
        itemVideosList  = new ResideMenuItem(this, R.mipmap.icon_profile,  "视频集");
        itemExit = new ResideMenuItem(this, R.mipmap.icon_settings, "设置");
        itemSettings = new ResideMenuItem(this, R.mipmap.icon_calendar, "退出");

        itemGifProductsList.setOnClickListener(this);
        itemVideosList.setOnClickListener(this);
        itemExit.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemGifProductsList, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemVideosList, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemExit, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });	
	}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	switch (ev.getAction()){
        case MotionEvent.ACTION_DOWN:
            break;

        case MotionEvent.ACTION_MOVE:
             break;

        case MotionEvent.ACTION_UP:
            break;

    }
    	return resideMenu.dispatchTouchEvent(ev);
    }

    private ResideMenu.OnMenuListener mResideMenuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
        }

        @Override
        public void closeMenu() {
        }
    };

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        if (v == itemGifProductsList){
        	if (mCurrentFragmentShow != CURRENT_FRAGMENT_SHOW_GIFS_LIST) {
        		changeFragment(new GifProductsListFragment());
        		mCurrentFragmentShow = CURRENT_FRAGMENT_SHOW_GIFS_LIST;
        	}
        }else if (v == itemVideosList){
        	if (mCurrentFragmentShow != CURRENT_FRAGMENT_SHOW_VIDEOS_LIST) {
        	    changeFragment(new VideosListFragment());
        	    mCurrentFragmentShow = CURRENT_FRAGMENT_SHOW_VIDEOS_LIST;
        	}            
        }else if (v == itemExit){
        	if (mCurrentFragmentShow != CURRENT_FRAGMENT_SHOW_SETTINGS_LIST) {
                SettingFragment setting = new SettingFragment();
                setting.setResideMenu(resideMenu);
                setting.setContext(MainActivity.this);
        	    changeFragment(setting);
        	    mCurrentFragmentShow = CURRENT_FRAGMENT_SHOW_SETTINGS_LIST;
        	}
        }else if (v == itemSettings){
        	finish();
        	return;
        }

        resideMenu.closeMenu();
		
	}
	
    // What good method is to access resideMenu
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return  super.onKeyDown(keyCode, event);

    }
}
