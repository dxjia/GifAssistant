/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-7
 */
package com.wind.gifassistant.utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 *
 * @author Djia
 * @time 2014-8-7上午10:06:29
 * @instuction 
 */
public class AppConfigs {
	
	public static final String APP_TAG = "GIF_A ";
	//private static String tag = "AppConfigs";

	/**
	 * 
	 */
	public AppConfigs() {
		// TODO Auto-generated constructor stub
	}

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

	// 检查主要的几个目录，没有的话就创建
	public static void checkAndCreateNecessaryFolders() {

		// 判断SD卡在不在
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}

		// 程序根目录
		File file = new File(APP_ABSOLUTE_ROOT_PATH);
		if (!file.exists()) {
			file.mkdir();
		}

		// Gif 图片作品保存目录
		file = new File(GIF_PRODUCTS_FOLDER_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
		
		// Videos 目录
		file = new File(VIDEOS_FOLDER_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
		
		// gif 制作时的中间文件
		file = new File(APP_GIF_TEMP_FILES_FOLDER_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	// 判断程序根目录是否存在
	public static boolean isAppRootFolderExist() {
		boolean exist = false;
		File file = new File(APP_ABSOLUTE_ROOT_PATH);
		if (file.exists()) {
			exist = true;
		}
		return exist;
	}
	
	// 清空temp文件夹
	public static void cleanUpTemp(String path) {
		// gif 制作时的中间文件
		File file = new File(path);
		if (!file.exists()) {
			return;
		}

		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			File temp = new File(path + File.separator + tempList[i]);
	        temp.delete();
		}
		
		file.delete();
	}

	// 创建临时文件夹，以要输出的gif名为文件夹名
	public static String createTempFolder(String name) {
		// gif 制作时的中间文件
		File file = new File(APP_GIF_TEMP_FILES_FOLDER_PATH + File.separator + name);

		if (!file.exists()) {
			file.mkdir();
		}

		String[] tempList = file.list();
		if (tempList == null) {
			return null;
			
		}
		for (int i = 0; i < tempList.length; i++) {
			File temp = new File(APP_GIF_TEMP_FILES_FOLDER_PATH + File.separator + tempList[i]);
	        temp.delete();
		}
		
		return file.getAbsolutePath();
	}

    public static Uri getResideMenuConfig(SharedPreferences sp) {
        if (sp != null) {
            String uriString =sp.getString(AppUtils.KEY_RESIDE_MENU_BACKGROUD, null);
            if(TextUtils.isEmpty(uriString)) {
                return null;
            }
            return Uri.parse(uriString);
        }
        return null;
    }
}
