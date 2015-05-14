/**
 * Copyright  2014  Djia
 * All right reserved.
 *
 * Created on 2014-8-11
 */
package com.wind.gifassistant.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppUtils;
import com.wind.gifassistant.views.gifview.GifView;
import com.wind.gifassistant.views.gifview.GifView.GifShowGravity;

import java.io.File;

/**
 * @author Djia
 * @time 2014-8-11下午3:05:32
 * @instuction show gif
 */
public class GifShowActivity extends Activity {

    private GifView mGifView = null;

    private FloatingActionsMenu mFloatingMenu;
    private float mTouchPosX;
    private float mTouchPosY;
    private float mMaxDistance;
    private static float DEFAULT_MOVE_FLAG = 10;

    private String mGifPath;

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

        Intent intent = getIntent();
        mGifPath = intent.getStringExtra(AppUtils.KEY_PATH);

        mGifView = (GifView) findViewById(R.id.gif_show);
        mGifView.setGifShowGravity(GifShowGravity.CENTER_FULL);
        mGifView.showGifImage(mGifPath);

        mFloatingMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        FloatingActionButton actionShareButton = (FloatingActionButton) findViewById(R.id.action_share);
        actionShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                File file = new File(mGifPath);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, getTitle()));
                mFloatingMenu.collapse();
            }
        });


        FloatingActionButton actionDeleteButton = (FloatingActionButton) findViewById(R.id.action_delete);
        actionDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(mGifPath);
                if (!file.exists()) {
                    return;
                }

                file.delete();
                mFloatingMenu.collapse();
                finish();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
            if (mMaxDistance < DEFAULT_MOVE_FLAG) {
                if (mFloatingMenu != null) {
                    mFloatingMenu.toggle();
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
