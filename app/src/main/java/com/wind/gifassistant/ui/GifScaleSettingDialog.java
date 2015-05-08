package com.wind.gifassistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

/**
 * Created by djia on 15-5-8.
 */
public class GifScaleSettingDialog extends Dialog {

    private String mTitleStr;
    private TextView mTitleView;
    private Context mContext;
    private RadioGroup mRadioGroup;

    private RadioButton mQualityRadioButton1;
    private RadioButton mQualityRadioButton2;
    private RadioButton mQualityRadioButton3;
    private RadioButton mQualityRadioButton4;
    private RadioButton mQualityRadioButton5;

    public GifScaleSettingDialog(Context context) {
        this(context, R.style.popup_dialog);
    }

    public GifScaleSettingDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setDialogTitle(String title) {
        mTitleStr = title;
    }

    public void setDialogTitle(int titleResId) {
        mTitleStr = mContext.getResources().getString(titleResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_quality_setting_dialog);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(mTitleStr);

        initRadioButtons();
    }

    /**
     * 初始化radiobuttons， 并且绑定变化监听，有改变就保存值
     */
    private void initRadioButtons() {

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mQualityRadioButton1 = (RadioButton) findViewById(R.id.quality_1);
        mQualityRadioButton2 = (RadioButton) findViewById(R.id.quality_2);
        mQualityRadioButton3 = (RadioButton) findViewById(R.id.quality_3);
        mQualityRadioButton4 = (RadioButton) findViewById(R.id.quality_4);
        mQualityRadioButton5 = (RadioButton) findViewById(R.id.quality_5);

        int current = getCurrentSetting();

        switch (current) {
            case AppUtils.GIF_SCALE_VALUE_1:
                mQualityRadioButton1.setChecked(true);
                break;
            case AppUtils.GIF_SCALE_VALUE_2:
                mQualityRadioButton2.setChecked(true);
                break;
            case AppUtils.GIF_SCALE_VALUE_3:
                mQualityRadioButton3.setChecked(true);
                break;
            case AppUtils.GIF_SCALE_VALUE_4:
                mQualityRadioButton4.setChecked(true);
                break;
            case AppUtils.GIF_SCALE_VALUE_5:
                mQualityRadioButton5.setChecked(true);
                break;
            default:
                break;
        }

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int resId) {
                if (mQualityRadioButton1.isChecked()) {
                    setSetting(AppUtils.GIF_SCALE_VALUE_1);
                }

                if (mQualityRadioButton2.isChecked()) {
                    setSetting(AppUtils.GIF_SCALE_VALUE_2);
                }

                if (mQualityRadioButton3.isChecked()) {
                    setSetting(AppUtils.GIF_SCALE_VALUE_3);
                }

                if (mQualityRadioButton4.isChecked()) {
                    setSetting(AppUtils.GIF_SCALE_VALUE_4);
                }

                if (mQualityRadioButton5.isChecked()) {
                    setSetting(AppUtils.GIF_SCALE_VALUE_5);
                }

            }
        });

    }


    private int getCurrentSetting() {
        SharedPreferences sp =
                mContext.getSharedPreferences(AppUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return AppConfigs.getGifProductScaleSetting(sp);
    }

    private boolean setSetting(int select) {
        SharedPreferences sp =
                mContext.getSharedPreferences(AppUtils.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return AppConfigs.setGifProductScaleSetting(sp, select);
    }

}
