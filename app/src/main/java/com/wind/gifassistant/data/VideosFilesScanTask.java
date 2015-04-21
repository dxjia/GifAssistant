/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-7
 */
package com.wind.gifassistant.data;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wind.gifassistant.utils.AppUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 *
 * @author Djia
 * @time 2014-8-7上午10:00:03
 * @instuction 
 */
public class VideosFilesScanTask extends AsyncTask<Void, Void, ArrayList<String>> {

	final Context mContext;
	final private ArrayList<String> mListItem;
	final private PullToRefreshListView mPullRefreshListView;
	final private ListAdapter mAdapter;
	final private TextView mEmptyNoteTextView;

	public VideosFilesScanTask(Context context, ArrayList<String> listItem, TextView emptyNoteView,
            PullToRefreshListView pullRefreshListView, ListAdapter adapter) {
		mContext = context;
		mListItem = listItem;
		mPullRefreshListView = pullRefreshListView;
		mAdapter = adapter;
		mEmptyNoteTextView = emptyNoteView;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		// Simulates a background job.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		ArrayList<String> result = new ArrayList<String>();
		try {
			getVideoFiles(result, new File(AppUtils.VIDEOS_FOLDER_PATH));
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return result;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		mListItem.clear();
		if (result != null && result.size() > 0 ) {
		    mListItem.addAll(result);
		}
		
		//通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
		((BaseAdapter) mAdapter).notifyDataSetChanged();
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshListView.onRefreshComplete();
		if (mEmptyNoteTextView != null) {
		    if (mListItem.size() > 0) {
		    	mEmptyNoteTextView.setVisibility(View.GONE);
		    } else {
			    mEmptyNoteTextView.setVisibility(View.GONE);
			    mEmptyNoteTextView.setText("下拉进行刷新");
		    }
		}

		super.onPostExecute(result);
	}

    private void getVideoFiles(final ArrayList<String> list,File file){
    	file.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				if (file != null && file.length() <= 0) {
					return false;
				}
				String name = file.getName();
				int i = name.indexOf('.');
				if(i != -1){
					name = name.substring(i);
					if(name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp")){
						list.add(file.getAbsolutePath());
						return true;
					}
				}else if(file.isDirectory()){
					getVideoFiles(list, file);
				}
				return false;
			}
    	});
    }
}
