/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-19
 */
package com.wind.gifassistant.gifworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.wind.gifassistant.utils.AppConfigs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

/**
 *
 * @author Djia
 * @time 2014-8-19下午4:08:37
 * @instuction 
 */
public class GifMerger {

	private final static String TAG = AppConfigs.APP_TAG + "GifMerger";
	private final static boolean DEBUG = true;

	/**
	 * 
	 */
	public GifMerger() {
		// TODO Auto-generated constructor stub
	}

	static {
		System.loadLibrary("gifflen");
	}

	private native static int Init(String name, int w, int h, int numColors, int quality, int frameDelay);
	private native static int AddFrame(int[] pixels);
	private native static void Close();	

	public static void encode(String output, Bitmap[] bitmaps, int delay) {
		if(bitmaps == null || bitmaps.length == 0) {
			throw new NullPointerException("Bitmaps should have content!!!");
		}

		int width = bitmaps[0].getWidth();
		int height = bitmaps[0].getHeight();
		if(Init(output, width, height, 256, 100, delay) != 0) {
			Log.e(TAG, "init failed");
			return;
		}

		for(Bitmap bp: bitmaps) {
			int pixels[] = new int[width * height];
			bp.getPixels(pixels, 0, width, 0, 0, width, height);
			AddFrame(pixels);
		}

		Close();
	}

    /*
     * 获取目录下所有文件
     */
	public static List<File> getFiles(String realpath, List<File> files) {

		File realFile = new File(realpath);
		if (realFile.isDirectory()) {
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath(), files);
				} else {
					files.add(file);
				}
			}
		}
		return files;
	}

	/*
	 * 获取目录下所有文件(按名称排序)
	 */
	public static List<File> getFileSort(String path) {
		List<File> list = getFiles(path, new ArrayList<File>());
		if (list != null && list.size() > 0) {

			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
					String fileName = file.getName();
					String newFileName = newFile.getName();
					String fileNameP = fileName.substring(0, fileName.lastIndexOf("."));
					String newFileNameP = newFileName.substring(0, newFileName.lastIndexOf("."));
					
					int fileNameSec = Integer.valueOf(fileNameP);
					int newFileNameSec = Integer.valueOf(newFileNameP);;
					if (fileNameSec < newFileNameSec) {
						return -1;
					} else if (fileNameSec == newFileNameSec) {
						return 0;
					} else {
						return 1;
					}
				}
			});
		}
		return list;
	}

	/*
	 * 提取图片时自动计算一个合理的缩放比率
	 */	
	public static void encodeReasonableScale(String output, String sourceFilesPath, int delay, Handler handler) {
		File sourceFileFolder = new File(sourceFilesPath);
		if (!sourceFileFolder.exists()) {
			loge("source folder not exsit");
			return;
		}
		
		String[] tempList = sourceFileFolder.list();
		String fileName = tempList[0];
		String path;
		if (sourceFilesPath.endsWith(File.separator)) {
			path = sourceFilesPath + fileName;
		} else {
			path = sourceFilesPath + File.separator + fileName;				
		}
		int scale = getReasonableScale(path);
		logd("scale = " + scale);
		encode(output, sourceFilesPath, scale, delay, handler);
	}
	
	/*
	 * output 输出的gif文件名
	 * sourceFilesPath 源文件路径，其下的所有图片将被遍历
	 * delay 播放间隔
	 */
	public static void encode(String output, String sourceFilesPath, int scale, int delay, Handler handler) {
		File sourceFileFolder = new File(sourceFilesPath);
		if (!sourceFileFolder.exists()) {
			loge("source folder not exsit -- when encode");
			return;
		}

		List<File> list = getFileSort(sourceFilesPath);
		
		int filesNumber = list.size();
		logd("total = " + filesNumber);

		SparseArray<String> resultList = new SparseArray<String>();

		int index = 0;
		for (File file : list) {
			String fileName = file.getName();
			String path;
			if (sourceFilesPath.endsWith(File.separator)) {
				path = sourceFilesPath + fileName;
			} else {
				path = sourceFilesPath + File.separator + fileName;				
			}

			resultList.put(index, path);
			index++;

			logd("add file [" + path + "]"); 
        }
		
		Bitmap bitmap = readImgFile(resultList.get(0), scale);
		if (bitmap == null) {
			logd("the first imag get failed, stop encode");
			return;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		logd("output size width*height [" + width + "*" + height + "]");

		String checkedOutput = fixRepeatFileName(output);
		logd("to init for output [" + checkedOutput + "]");
		if (Init(checkedOutput, width, height, 256, 100, delay) != 0) {
			Log.e(TAG, "init failed");
			return;
		}
		
		logd("init done");

		try {
			int pixels[] = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			AddFrame(pixels);
			bitmap.recycle();
			bitmap = null;
			logd("addframe " + 0 + " done");
			if (handler != null) {
				String processStr = "(0/" + filesNumber + ")";
				handler.sendMessage(handler.obtainMessage(0, processStr));
			}
			pixels = null;

			for (int i = 1; i < filesNumber; i++) {
				bitmap = readImgFile(resultList.get(i), scale);
				if (bitmap == null) {
					return;
				}
				int pixelsP[] = new int[width * height];
				bitmap.getPixels(pixelsP, 0, width, 0, 0, width, height);
				AddFrame(pixelsP);
				bitmap.recycle();
				bitmap = null;
				pixelsP = null;
				logd("addframe " + i + " done");
				if (handler != null) {
					String processStr = "(" + i + "/" + filesNumber + ")";
					handler.sendMessage(handler.obtainMessage(0, processStr));
				}
			}
		} finally {
			Close();
		}

		logd("done");
	}
	
	private static Bitmap readImgFile(String path, int scale) {
    	File f = new File(path);
    	InputStream is;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = scale;
			return BitmapFactory.decodeStream(is, null, o);
		} catch (Exception e) {
			Log.e(TAG, "Init: failed to decode bitmaps.");
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	public static int getReasonableScale(String path) {
		int scale = 1;
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			// inJustDecodeBounds 为true时不会产生bitmap输出
			o.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			BitmapFactory.decodeFile(path, o);
			//计算缩放比
			int scale1 = (int)(o.outHeight / (float)200);
			int scale2 = (int)(o.outWidth / (float)200);
			scale = (scale1 > scale2) ? scale1 : scale2;
			if (scale <= 0) {
				scale = 1;
			}
		} catch (Exception e) {
			Log.e(TAG, "getReasonableScale() " + e);
			return scale;
		}
		return scale;
	}

	private static String fixRepeatFileName(String output) {
        String outPutFileName = output.substring((output.lastIndexOf(File.separator) + 1), output.length());
		String folderPath = output.substring(0, output.lastIndexOf(File.separator));
		File folder = new File(folderPath);
		String[] allFilesName = folder.list();
		boolean haveRepeat = false;
		int repeattimes = 0;
		do {
			haveRepeat = false;
			for (int i = 0; i < allFilesName.length; i++) {
				if (allFilesName[i].equalsIgnoreCase(outPutFileName)) {
				    haveRepeat = true;
				    break;
				}
			}
			if (haveRepeat) {
		        String name = outPutFileName.substring(0, outPutFileName.lastIndexOf("."));
		        name = name + "_" + repeattimes;
		        repeattimes ++;
		        outPutFileName = name + ".gif";
			}
		} while(haveRepeat);
		
		String outpath = folderPath + File.separator + outPutFileName;
		return outpath;
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
