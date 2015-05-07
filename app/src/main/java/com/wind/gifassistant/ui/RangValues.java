package com.wind.gifassistant.ui;

/**
 * Created by djia on 15-5-7.
 */
public class RangValues {

    public int max;
    public int min;
    public int current;

    /**
     *
     * @param m 最大值
     * @param i 最小值
     * @param c 当前值
     */
    public RangValues(int m, int i, int c) {
        max = m;
        min = i;
        current = c;
    }
}
