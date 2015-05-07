package com.wind.gifassistant.utils;

import android.os.Environment;

public class AppUtils {
    public static final int MENU_DELETE           = 0;
    public static final int MENU_SHOW             = 1;
    public static final int MENU_DELETE_ALL       = 2;

    public static final int FILE_TYPE_GIF = 0;
    public static final int FILE_TYPE_VIDEO = 1;

    // Root path
    public static final String APP_ROOT_DIRECTORY_NAME = "gif_assistant";
    public static final String APP_ABSOLUTE_ROOT_PATH = Environment
            .getExternalStorageDirectory().toString() + "/" + APP_ROOT_DIRECTORY_NAME;

    // gif 作品库
    public static final String GIF_PRODUCTS_FOLDER_NAME = "products";
    public static final String GIF_PRODUCTS_FOLDER_PATH = APP_ABSOLUTE_ROOT_PATH + "/" + GIF_PRODUCTS_FOLDER_NAME;

    // videos folder, maybe not be needed
    public static final String VIDEOS_FOLDER_NAME = "videos";
    public static final String VIDEOS_FOLDER_PATH = APP_ABSOLUTE_ROOT_PATH + "/" + VIDEOS_FOLDER_NAME;

    // crash path
    public static final String APP_CRASH_FOLDER_NAME = "crash_log";
    public static final String APP_CRASH_PATH = APP_ABSOLUTE_ROOT_PATH  + "/" + APP_CRASH_FOLDER_NAME;

    // temp file folder
    public static final String APP_GIF_TEMP_FILES_FOLDER_NAME = "temp_files";
    public static final String APP_GIF_TEMP_FILES_FOLDER_PATH = APP_ABSOLUTE_ROOT_PATH + "/" + APP_GIF_TEMP_FILES_FOLDER_NAME;

    public static final String KEY_PATH = "file_path";
    public static final String KEY_RESIDE_MENU_BACKGROUD = "reside_menu_backgroud";

    public static final String KEY_GIF_PRODUCT_FRAME_RATE = "gif_frame_rate";
    public static final String KEY_GIF_PRODUCT_delay = "gif_delay";
    public static final String KEY_GIF_PRODUCT_scale = "gif_scale";
    public static final String KEY_GIF_PRODUCT_MAX_TIME = "max_time";

    public static final int DEFAULT_SCALE_VALUE = 0;


    public static final int DEFAULT_RATE_VALUE = 15;
    public static final int DEFAULT_RATE_MIN_VALUE = 10;
    public static final int DEFAULT_RATE_MAX_VALUE = 25;

    public static final int DEFAULT_MAX_TIME_VALUE = 15;
    public static final int DEFAULT_MAX_TIME_MAX_VALUE = 20;
    public static final int DEFAULT_MAX_TIME_MIN_VALUE = 2;


}
