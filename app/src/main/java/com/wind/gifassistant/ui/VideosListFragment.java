package com.wind.gifassistant.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.special.ResideMenu.ResideMenu;
import com.wind.gifassistant.R;
import com.wind.gifassistant.data.VideosFilesScanTask;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

public class VideosListFragment extends Fragment implements DataLoadCallBack {

	private View parentView;
	private ResideMenu mResideMenu;
	
	private ArrayList<String> mVideosListItem = new ArrayList<String>();
    private PullToRefreshListView mVideoPullListView;
    private TextView mVideoEmptyNoteView;
    
    private VideosListAdapter mVideosListAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.main_screen_pager_fragment, container, false);
        setUpViews();
        return parentView;
    }

    private void setUpViews() {
    	MainActivity parentActivity = (MainActivity) getActivity();
    	mResideMenu = parentActivity.getResideMenu();
    	
    	mVideoPullListView = (PullToRefreshListView) parentView.findViewById(R.id.pull_refresh_list);
		mVideoEmptyNoteView = (TextView) parentView.findViewById(R.id.empty_list_text);

		mVideosListAdapter = new VideosListAdapter(parentActivity, mVideosListItem);
		mVideoPullListView.setAdapter(mVideosListAdapter);

		final Context vContext = parentActivity;
		
		mVideoPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 先隐藏empty note text
				mVideoEmptyNoteView.setVisibility(View.GONE);
				String label = DateUtils.formatDateTime(vContext, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new VideosFilesScanTask(vContext, mVideosListItem, mVideoEmptyNoteView, mVideoPullListView, mVideosListAdapter).execute();
			}
		});

		// click
		ListView actualListView = mVideoPullListView.getRefreshableView();
		actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int index = 0;
				if(position > 0 && position <= mVideosListItem.size()) {
					index = position - 1;
				}
				String path = mVideosListItem.get(index);
				if (TextUtils.isEmpty(path)) {
					return;
				}
        		Intent intent = new Intent(vContext, VideoPlayerActivity.class);
        		intent.putExtra(AppConfigs.KEY_PATH, path);
        		vContext.startActivity(intent);
			}
		});
		
		ListContextMenuListener videoListMenuCreateListener = new ListContextMenuListener(
				vContext, mVideosListAdapter, AppUtils.FILE_TYPE_VIDEO, this,
				mResideMenu);
		actualListView.setOnCreateContextMenuListener(videoListMenuCreateListener);
		loadData(vContext);
    }

    public void loadData(Context context) {
		// load videos
		if (mVideosListAdapter != null) {
			new VideosFilesScanTask(context, mVideosListItem,
					mVideoEmptyNoteView, mVideoPullListView, mVideosListAdapter)
					.execute();
		}
	}
}
