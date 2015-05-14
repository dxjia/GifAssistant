package com.wind.gifassistant.ui;

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
import com.wind.gifassistant.data.GifProductsScanTask;
import com.wind.gifassistant.utils.AppUtils;

import java.util.ArrayList;

public class GifProductsListFragment extends Fragment  implements DataLoadCallBack {

    private View mParentView;
    private ResideMenu mResideMenu;

    private PullToRefreshListView mGifPullListView;
    private TextView mGifEmptyNoteView;
    private GifProductsListAdapter mGifProductsListAdapter;
    private ArrayList<String> mGifListItem = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mParentView = inflater.inflate(R.layout.main_screen_pager_fragment, container, false);
        setUpViews();
        return mParentView;
    }

    private void setUpViews() {
        MainActivity parentActivity = (MainActivity) getActivity();
        mResideMenu = parentActivity.getResideMenu();

        // gif list
		mGifPullListView = (PullToRefreshListView) mParentView.findViewById(R.id.pull_refresh_list);
		mGifEmptyNoteView = (TextView) mParentView.findViewById(R.id.empty_list_text);

		mGifProductsListAdapter = new GifProductsListAdapter(parentActivity, mGifListItem);
		mGifPullListView.setAdapter(mGifProductsListAdapter);


		final Context vContext = parentActivity;

		// set refresh listener
		mGifPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 先隐藏empty note text
				mGifEmptyNoteView.setVisibility(View.GONE);
				String label = DateUtils.formatDateTime(vContext, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GifProductsScanTask(vContext, mGifListItem, mGifEmptyNoteView, mGifPullListView, mGifProductsListAdapter, true).execute();
			}
		});
		
		// click
		ListView actualListView = mGifPullListView.getRefreshableView();
		actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int index = 0;
				if(position > 0 && position <= mGifListItem.size()) {
					index = position - 1;
				}
				String path = mGifListItem.get(index);
				if (TextUtils.isEmpty(path)) {
					return;
				}
        		Intent intent = new Intent(vContext, GifShowActivity.class);
        		intent.putExtra(AppUtils.KEY_PATH, path);
        		vContext.startActivity(intent);
			}
		});
		
		/*ListContextMenuListener gifListMenuCreateListener = new ListContextMenuListener(
				vContext, mGifProductsListAdapter, AppUtils.FILE_TYPE_GIF,
				this, mResideMenu);
		actualListView.setOnCreateContextMenuListener(gifListMenuCreateListener);*/
		loadData(vContext);
    }

    public void loadData(Context context) {
		// load list
		if (mGifProductsListAdapter != null) {
			new GifProductsScanTask(context, mGifListItem, mGifEmptyNoteView,
					mGifPullListView, mGifProductsListAdapter, false).execute();
		}
	}

    @Override
    public void onResume() {
        loadData(getActivity());
        super.onResume();
    }
}
