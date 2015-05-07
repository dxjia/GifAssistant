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
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wind.gifassistant.ui.GifProductsListAdapter;
import com.wind.gifassistant.utils.AppUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Djia
 * @time 2014-8-7上午10:19:24
 * @instuction 
 */
public class GifProductsScanTask extends AsyncTask<Void, Void, ArrayList<String>> {

	final Context mContext;
	final private ArrayList<String> mListItem;
	final private PullToRefreshListView mPullRefreshListView;
	final private GifProductsListAdapter mAdapter;
	final private TextView mEmptyNoteTextView;
	final private boolean mSimulateSleepThread;

	public GifProductsScanTask(Context context, ArrayList<String> listItem, TextView emptyNoteView,
			                  PullToRefreshListView pullRefreshListView, GifProductsListAdapter adapter, boolean simulate) {
		mContext = context;
		mListItem = listItem;
		mPullRefreshListView = pullRefreshListView;
		mAdapter = adapter;
		mEmptyNoteTextView = emptyNoteView;
		mSimulateSleepThread = simulate;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		if (mSimulateSleepThread) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
		ArrayList<String> result = new ArrayList<String>();
		try {
			List<File> gifFiles = getGifFileSortByTime(AppUtils.GIF_PRODUCTS_FOLDER_PATH);
			if (gifFiles != null && gifFiles.size() > 0 ) {
				for (File file : gifFiles) {
					result.add(file.getAbsolutePath());					
				}
			}
			//getGifProductsFiles(result, new File(AppConfigs.GIF_PRODUCTS_FOLDER_PATH));
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		return result;
	}

	//这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
	//根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
	@Override
	protected void onPostExecute(ArrayList<String> result) {

		try {
			mListItem.clear();
			if (result != null && result.size() > 0 ) {
			    mListItem.addAll(result);
			}
			
			//通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
			mAdapter.notifyDataSetChanged();
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		super.onPostExecute(result);
	}

    private void getGifProductsFiles(final ArrayList<String> list,File file){
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
					if(name.equalsIgnoreCase(".gif")){
						list.add(file.getAbsolutePath());
						return true;
					}
				}else if(file.isDirectory()){
					getGifProductsFiles(list, file);
				}
				return false;
			}
    	});
    }
    
    /*
     * 获取目录下所有文件
     */
	public static List<File> getFiles(String realpath, List<File> files) {

		File realFile = new File(realpath);
		if (realFile.isDirectory()) {
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath(), files);
				} else {
					String name = file.getName();
					int i = name.indexOf('.');
					if(i != -1){
						name = name.substring(i);
						if(name.equalsIgnoreCase(".gif")){
							files.add(file);
						}
					}					
				}
			}
		}
		return files;
	}

	/*
	 * 获取目录下所有文件(按名称排序)
	 */
	public static List<File> getGifFileSortByTime(String path) {
		List<File> list = getFiles(path, new ArrayList<File>());
		if (list != null && list.size() > 0) {

			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
				
					long fileNameSec = file.lastModified();
					long newFileNameSec = newFile.lastModified();;
					if (fileNameSec < newFileNameSec) {
						return 1;
					} else if (fileNameSec == newFileNameSec) {
						return 0;
					} else {
						return -1;
					}
				}
			});
		}
		return list;
	}

}
