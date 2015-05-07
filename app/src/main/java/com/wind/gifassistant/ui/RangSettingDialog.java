package com.wind.gifassistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.wind.gifassistant.R;
import com.wind.gifassistant.ui.textpicker.TextPicker;
import com.wind.gifassistant.utils.AppUtils;

/**
 * Created by djia on 15-4-21.
 */
public class RangSettingDialog extends Dialog implements DialogInterface.OnDismissListener {

    private String mTitleStr;
    private TextView mTitleView;
    private String mTipsStr;
    private TextView mTipsView;
    private String mUnitStr;
    private TextView mUnitTextView;

    private TextPicker mPicker;

    private Context mContext;

    private RangSettingListener listener;

    public RangSettingDialog(Context context) {
        this(context, R.style.popup_dialog);
    }

    private RangSettingDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setOnDismissListener(this);
    }

    public void setDialogTitle(String title) {
        mTitleStr = title;
    }

    public void setDialogTitle(int titleResId) {
        mTitleStr = mContext.getResources().getString(titleResId);
    }

    public void setUnitText(String unit) {
        mUnitStr = unit;
    }

    public void setUnitText(int unitResId) {
        mUnitStr = mContext.getResources().getString(unitResId);
    }

    public void setTips(String tips) {
        mTipsStr = tips;
    }

    public void setTips(int tipsResId) {
        mTipsStr = mContext.getResources().getString(tipsResId);
    }

    public void setRangListener(RangSettingListener l) {
        listener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rang_setting_dialog);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(mTitleStr);

        mTipsView = (TextView) findViewById(R.id.tips);
        mTipsView.setText(mTipsStr);
        mTipsView.setSelected(true);

        mUnitTextView = (TextView) findViewById(R.id.unit);
        mUnitTextView.setText(mUnitStr);

        mPicker = (TextPicker) findViewById(R.id.numberPicker);
        RangValues rangValues = null;
        if (listener != null) {
            rangValues = listener.getCurrentRang();
        }
        int max = rangValues!=null ? rangValues.max : AppUtils.DEFAULT_RATE_MAX_VALUE;
        int min = rangValues!=null ? rangValues.min : AppUtils.DEFAULT_RATE_MIN_VALUE;
        int current = rangValues!=null ? rangValues.current : AppUtils.DEFAULT_RATE_VALUE;
        mPicker.setMaxValue(max);
        mPicker.setMinValue(min);
        mPicker.setValue(current);
        mPicker.setFocusable(true);
        mPicker.setFocusableInTouchMode(true);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (mPicker != null) {
            if (listener != null) {
                int current = mPicker.getValue();
                listener.commitRangSetting(current);
            }
        }
    }
}
