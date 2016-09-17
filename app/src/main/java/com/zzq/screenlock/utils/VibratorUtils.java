package com.zzq.screenlock.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * 功能：调用手机的震动功能
 * Created by zhuzhiqiang on 2016/8/17 0017.
 */
public class VibratorUtils {
    private Vibrator vibrator;
    private static VibratorUtils vibratorUtils = null;
    private Context context = null;

    private VibratorUtils(Context context) {
        this.context = context;
    }

    public static VibratorUtils getInstance(Context context) {
        if (vibratorUtils == null) {
            vibratorUtils = new VibratorUtils(context);
        }
        return vibratorUtils;
    }

    /*开始震动*/
    public void vibrator() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {20, 50}; /*{间隔时间，持续时间}*/
        vibrator.vibrate(pattern, -1);/*如果只想震动一次，index设为-1*/
    }
}
