package com.wind.gifassistant.ui;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;

import com.special.ResideMenu.ResideMenu;
import com.wind.gifassistant.utils.AppUtils;

import java.io.File;


public class ListContextMenuListener  implements OnCreateContextMenuListener {

    private BaseAdapter mAdapter = null;
	int mFileType;
	Context mContext;
	private final DataLoadCallBack mCallBack;
	private final ResideMenu mResideMenu;
	
	public ListContextMenuListener(Context context, BaseAdapter adapter, int fileType) {
		mContext = context;
		mAdapter = adapter;
		mFileType = fileType;
		mCallBack = null;
		mResideMenu = null;
	}

	public ListContextMenuListener(Context context, BaseAdapter adapter,
			int fileType, DataLoadCallBack callback, ResideMenu resideMenu) {
		mContext = context;
		mAdapter = adapter;
		mFileType = fileType;
		mCallBack = callback;
		mResideMenu = resideMenu;
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnCreateContextMenuListener#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (mResideMenu != null && mResideMenu.isOpened()) {
			return;
		}
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int pos = info.position;
		menu.clear();
		menu.setHeaderTitle("选项:");
		String filePath = (String) mAdapter.getItem(pos);
		ListMenuClickListener mListMenuClickListener = new ListMenuClickListener(mContext, filePath, mFileType);
		menu.add(0, AppUtils.MENU_DELETE, 0, "删除").setOnMenuItemClickListener(mListMenuClickListener);
		menu.add(0, AppUtils.MENU_SHOW, 0, "查看").setOnMenuItemClickListener(mListMenuClickListener);
	}
	
	
	   /**
     * Context menu handlers for the message list view.
     */
    private final class ListMenuClickListener implements MenuItem.OnMenuItemClickListener {

    	private String path;
    	private int type;
    	private Context mContext;
    	public ListMenuClickListener(Context context, String filePath, int fileType) {
    		mContext = context;
    		path = filePath;
    		type = fileType;    		
    	}

    	/* (non-Javadoc)
		 * @see android.view.MenuItem.OnMenuItemClickListener#onMenuItemClick(android.view.MenuItem)
		 */
		@Override
		public boolean onMenuItemClick(MenuItem menuItem) {
			// TODO Auto-generated method stub
            if (menuItem == null) {
                return false;
            }

            switch (menuItem.getItemId()) {
                case AppUtils.MENU_DELETE:
                	deleteFile(path);
                	if (mCallBack != null) {
                	    mCallBack.loadData(mContext);
                	}
                    return true;

                case AppUtils.MENU_SHOW:
                	Intent intent = null;
                	if (type == AppUtils.FILE_TYPE_GIF) {
                		intent = new Intent(mContext, GifShowActivity.class);
                	} else if (type == AppUtils.FILE_TYPE_VIDEO) {
                		intent = new Intent(mContext, VideoPlayerActivity.class);
                	}
            		intent.putExtra(AppUtils.KEY_PATH, path);
            		mContext.startActivity(intent);
                    return true;
                
                case AppUtils.MENU_DELETE_ALL:
                	if (type == AppUtils.FILE_TYPE_GIF) {
                		
                	} else if (type == AppUtils.FILE_TYPE_VIDEO) {
                		
                	}
                	return true;

                default:
                    return false;
            }
		}
    }

    private void deleteFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		
		file.delete();
    }
}
