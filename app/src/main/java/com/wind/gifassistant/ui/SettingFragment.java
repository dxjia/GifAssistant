package com.wind.gifassistant.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.special.ResideMenu.ResideMenu;
import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppUtils;

public class SettingFragment extends Fragment {

    private final static int FILE_SELECT_CODE = 0;

	private View mParentView;
	private LinearLayout mBackgroudSetting;
    private ResideMenu mResideMenu;

    SharedPreferences mSharedPreferences = null;

    public void setResideMenu(ResideMenu resideMenu) {
        mResideMenu = resideMenu;
    }
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	mParentView = inflater.inflate(R.layout.setting_fragment, container, false);
        setUpViews();
        return mParentView;
	}
	
	void setUpViews() {
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		mBackgroudSetting =(LinearLayout) mParentView.findViewById(R.id.reside_menu_backgroud_setting_area);
		mBackgroudSetting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                showFileChooser();
			}
		});
	}

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a backgroud picture"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(AppUtils.KEY_RESIDE_MENU_BACKGROUD, uri.toString());
                    editor.commit();
                    if (mResideMenu != null) {
                        mResideMenu.setBackground(uri);
                    }
                }
                break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
