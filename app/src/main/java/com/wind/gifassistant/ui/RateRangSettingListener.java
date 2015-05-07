package com.wind.gifassistant.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

/**
 * Created by djia on 15-5-7.
 */
public class RateRangSettingListener implements RangSettingListener {

    final Context mContext;
    final SharedPreferences mSharedPreferences;

    public RateRangSettingListener(Context context, SharedPreferences sp) {
        mContext = context;
        mSharedPreferences = sp;
    }

    @Override
    public boolean commitRangSetting(int current) {
        return AppConfigs.setGifProductFrameRateSetting(mSharedPreferences, current);
    }

    @Override
    public RangValues getCurrentRang() {
        int current =
                AppConfigs.getGifProductFrameRateSetting(mSharedPreferences);
        return new RangValues(
                   AppUtils.DEFAULT_RATE_MAX_VALUE,
                   AppUtils.DEFAULT_RATE_MIN_VALUE, current);
    }
}
