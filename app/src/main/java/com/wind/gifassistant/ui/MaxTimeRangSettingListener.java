package com.wind.gifassistant.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

/**
 * Created by djia on 15-5-7.
 */
public class MaxTimeRangSettingListener implements RangSettingListener {

    final Context mContext;
    final SharedPreferences mSharedPreferences;

    public MaxTimeRangSettingListener(Context context, SharedPreferences sp) {
        mContext = context;
        mSharedPreferences = sp;
    }

    @Override
    public boolean commitRangSetting(int current) {
        return AppConfigs.setGifProductMaxTimeSetting(mSharedPreferences, current);
    }

    @Override
    public RangValues getCurrentRang() {
        int current =
                AppConfigs.getGifProductMaxTimeSetting(mSharedPreferences);
        return new RangValues(
                   AppUtils.DEFAULT_MAX_TIME_MAX_VALUE,
                   AppUtils.DEFAULT_MAX_TIME_MIN_VALUE, current);
    }
}
