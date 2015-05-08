/**
 * Copyright  2014  Djia
 * All right reserved.
 *
 * Created on 2014-8-19
 */
package com.wind.gifassistant.gifworker;

import android.util.Log;

import com.wind.ffmpeghelper.FFmpegCommandBuilder;
import com.wind.ffmpeghelper.FFmpegNativeHelper;
import com.wind.gifassistant.utils.AppConfigs;
import com.wind.gifassistant.utils.AppUtils;

/**
 * @author Djia
 * @time 2014-8-19下午4:08:37
 * @instuction
 */
public class GifMerger {

    private final static String TAG = AppConfigs.APP_TAG + "GifMerger";
    private final static boolean DEBUG = true;

    private final static String FFMPEG_TAG = "ffmpeg";

    /**
     *
     */
    public GifMerger() {
        // TODO Auto-generated constructor stub
    }

    private final static FFmpegNativeHelper mFFmpegNativeHelper = new FFmpegNativeHelper();

    /**
     * @param output     输出的gif文件名
     * @param sourceFile 源文件路径，视频文件
     * @param ss         起始时间，单位s
     * @param se         终点时间，单位s
     * @return true success, false failed.
     */
    public static boolean generateGifProduct(String output, String sourceFile, int ss, int se) {
        if (mFFmpegNativeHelper.ffmpegRunCommand(
                getffmpegCommand(output, sourceFile, ss, se,
                        AppUtils.DEFAULT_RATE_VALUE, AppUtils.DEFAULT_SCALE_VALUE)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean generateGifProduct(String output, String sourceFile,
                                             int ss, int se, int rate) {
        if (mFFmpegNativeHelper.ffmpegRunCommand(getffmpegCommand(output,
                sourceFile, ss, se, rate,  AppUtils.DEFAULT_SCALE_VALUE)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean generateGifProduct(String output, String sourceFile, int ss, int se, int rate, int scale) {
        if (mFFmpegNativeHelper.ffmpegRunCommand(getffmpegCommand(output, sourceFile, ss, se, rate, scale)) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * example command:
     * ffmpeg -ss 45 -t 2 -i sourceFile -vf scale=300:-1 -gifflags -transdiff -r 15 -y output
     *
     * @param output     输出的gif文件名
     * @param sourceFile 源文件路径，视频文件
     * @param ss         起始时间，单位s
     * @param se         终点时间，单位s
     * @param rate       帧率
     * @return command string
     */
    private static String getffmpegCommand(String output,
                                           String sourceFile,
                                           int ss, int se,
                                           int rate, int scale) {
        String command = "";
        FFmpegCommandBuilder commandBuilder = new FFmpegCommandBuilder();
        commandBuilder.append("-ss");
        commandBuilder.append(ss);
        commandBuilder.append("-t");
        commandBuilder.append(se - ss);
        commandBuilder.append("-i");
        commandBuilder.append(sourceFile);
        if (scale != 0 && scale != 1) {
            String scaleStr = getScaleCommandStr(scale);
            if (scaleStr != null && !(scaleStr.isEmpty())) {
                commandBuilder.append("-vf");
                commandBuilder.append(scaleStr);
            }
        }
        commandBuilder.append("-gifflags");
        commandBuilder.append("-transdiff");
        commandBuilder.append("-r");
        commandBuilder.append(rate);
        commandBuilder.append("-y");
        commandBuilder.append(output);

        command = commandBuilder.getCommand();
        return command;
    }

    /**
     *
     * @param scale 缩小倍率
     * @return ex: scale=iw/2:ih/2
     */
    private static String getScaleCommandStr(int scale) {
        if (scale == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("scale=iw/");
        sb.append(scale);
        sb.append(":ih/");
        sb.append(scale);

        return sb.toString();
    }

    private static void logd(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }

    private static void loge(String message) {
        if (DEBUG) {
            Log.e(TAG, message);
        }
    }
}
