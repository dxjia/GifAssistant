package com.wind.gifassistant.ui;

import android.app.Activity;
import android.os.Bundle;

import com.wind.gifassistant.R;
import com.wind.gifassistant.ui.numberpicker.NumberPicker;

/**
 * Created by djia on 15-4-21.
 */
public class RangSetting extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rang_setting_dialog);

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMaxValue(20);
        np.setMinValue(0);
        np.setFocusable(true);
        np.setFocusableInTouchMode(true);

    }

}
