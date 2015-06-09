/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-12
 */
package com.wind.gifassistant.ui;

import java.util.ArrayList;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.data.ExtractPicturesWorker;
import com.wind.gifassistant.data.VideosInfo;
import com.wind.gifassistant.utils.AppConfigs;

/**
 *
 * @author Djia
 * @time 2014-8-12下午1:33:10
 * @instuction 
 */
public class VideosListAdapter extends BaseAdapter {
	final private LayoutInflater mInflater;
	final private ArrayList<String> mVideosPaths;
	final private static String TAG = AppConfigs.APP_TAG + "VideosListAdapter";
	final private static boolean DEBUG = true;

	/**
	 * 
	 */
	public VideosListAdapter(Context context, ArrayList<String> pathList) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		mVideosPaths = pathList;	
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mVideosPaths.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		if (pos < 1) {
			return null;
		}
		if (pos > mVideosPaths.size()) {
			return null;
		}
		
		return mVideosPaths.get((pos-1));
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private final class ViewHolder{
		public ImageView thumb;
		public TextView name;
		public TextView time;
		public TextView duration;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder=new ViewHolder();  
			convertView = mInflater.inflate(R.layout.videos_list_item, null);
			holder.thumb = (ImageView)convertView.findViewById(R.id.video_thumb);
			holder.name = (TextView)convertView.findViewById(R.id.video_name);
			holder.time = (TextView)convertView.findViewById(R.id.video_file_created_time);
			holder.duration = (TextView) convertView.findViewById(R.id.video_total_duration);
			convertView.setTag(holder);
		}else {
			logd("convertView != null, reuse");
			holder = (ViewHolder)convertView.getTag();
		}
		
		VideosInfo videoInfo = new VideosInfo(mVideosPaths.get(position));
		//holder.thumb.setImageBitmap(ExtractPicturesWorker.extractBitmap(mVideosPaths.get(position), 0));
		holder.thumb.setImageBitmap(ThumbnailUtils.createVideoThumbnail(mVideosPaths.get(position), Thumbnails.MINI_KIND));
		holder.name.setText("文件名：" + videoInfo.getName());
		holder.time.setText("时间：" + videoInfo.getLastModifyTime());
		holder.duration.setText("时长：" + videoInfo.getDuration());

		return convertView;
	}

	private static void logd(String message) {
		if(DEBUG) {
			Log.d(TAG, message);
		}
	}

}
