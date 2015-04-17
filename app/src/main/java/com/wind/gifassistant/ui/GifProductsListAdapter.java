/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-7
 */
package com.wind.gifassistant.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.data.GifProductInfo;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.views.gifview.GifView;
import com.wind.gifassistant.views.gifview.GifView.GifShowGravity;

/**
 *
 * @author Djia
 * @time 2014-8-7上午10:29:40
 * @instuction 
 */
public class GifProductsListAdapter extends BaseAdapter {
	final private LayoutInflater mInflater;
	final private ArrayList<String> mListItem;
	final private static String TAG = AppConfigs.APP_TAG + "GifProductsListAdapter";
	final private static boolean DEBUG = true;

	/**
	 * 
	 */
	public GifProductsListAdapter(Context context, ArrayList<String> listItem) {
		mInflater = LayoutInflater.from(context);
		mListItem = listItem;		
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListItem.size();
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
		if (pos > mListItem.size()) {
			return null;
		}
		
		return mListItem.get((pos-1));
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public final class ViewHolder{
		public GifView gif;
		public TextView name;
		public TextView time;
		public TextView fileSize;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder=new ViewHolder();  
			convertView = mInflater.inflate(R.layout.gif_products_list_item, null);
			holder.gif = (GifView)convertView.findViewById(R.id.gif_product);
			holder.name = (TextView)convertView.findViewById(R.id.gif_product_name);
			holder.time = (TextView)convertView.findViewById(R.id.gif_product_created_time);
			holder.fileSize = (TextView) convertView.findViewById(R.id.gif_product_file_size);
			convertView.setTag(holder);
		}else {
			logd("convertView != null, reuse");
			holder = (ViewHolder)convertView.getTag();
		}
		
		GifProductInfo gifInfo = new GifProductInfo(mListItem.get(position));
		holder.gif.setGifShowGravity(GifShowGravity.CENTER_FULL);
		holder.gif.showGifImage(gifInfo.getPath(), true);
		holder.name.setText("文件名：" + gifInfo.getName());
		holder.time.setText("时间：" + gifInfo.getLastModifyTime());
		holder.fileSize.setText("大小：" + gifInfo.getFileSize());

		return convertView;
	}

	private static void logd(String message) {
		if(DEBUG) {
			Log.d(TAG, message);
		}
	}
}
