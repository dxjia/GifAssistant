package com.wind.gifassistant.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.wind.gifassistant.R;

/**
 * Created by djia on 15-4-21.
 */
public class RangSetDialog extends Dialog {

    public RangSetDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rang_setting_dialog);
        setTitle("set value");
    }

}
