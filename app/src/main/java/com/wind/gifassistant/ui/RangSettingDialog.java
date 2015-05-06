package com.wind.gifassistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.ui.textpicker.TextPicker;

/**
 * Created by djia on 15-4-21.
 */
public class RangSettingDialog extends Dialog {

    private String mTitleStr;
    private TextView mTitleView;
    private TextPicker mPicker;
    private int max = 0;
    private int min = 0;

    public RangSettingDialog(Context context) {
        this(context, R.style.popup_dialog);
    }

    public RangSettingDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setTitle(String title) {
        mTitleStr = title;
    }

    public void setRang(int i, int m) {
        max = m;
        min = i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rang_setting_dialog);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(mTitleStr);

        mPicker = (TextPicker) findViewById(R.id.numberPicker);
        mPicker.setMaxValue(max);
        mPicker.setMinValue(min);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);
    }

}
