package com.wind.gifassistant.ui;

/**
 * Created by djia on 15-5-7.
 */
public interface RangSettingListener {

    /**
     * 将设定保存
     * @param current 设定值
     * @return 成功 true， 失败 false
     */
    public boolean commitRangSetting(int current);

    /**
     * 获取当前的设定
     * @return
     */
    public RangValues getCurrentRang();
}
