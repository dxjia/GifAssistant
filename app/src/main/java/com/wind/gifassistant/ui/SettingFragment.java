package com.wind.gifassistant.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wind.gifassistant.R;

public class SettingFragment extends Fragment {

	private View mParentView;
	private LinearLayout mBackgroudSetting;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	mParentView = inflater.inflate(R.layout.setting_fragment, container, false);
        setUpViews();
        return mParentView;
	}
	
	void setUpViews() {
		mBackgroudSetting =(LinearLayout) mParentView.findViewById(R.id.scale_setting_area);
		mBackgroudSetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
	}

	
}
