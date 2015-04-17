/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-13
 */
package com.wind.gifassistant.data;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.wind.gifassistant.utils.AppConfigs;

/**
 * 
 * @author Djia
 * @time 2014-8-13下午4:50:43
 * @instuction
 */
public class ExtractPicturesWorker {

	private static final String TAG = AppConfigs.APP_TAG + "ExtractPicturesWorker";
	private static final boolean DEBUG = true;
	
	private static final int DEFAULT_FRAME_RATE = 10;
	private static final int MAX_FRAME_RATE = 25;
	private static final int MAX_FRAME_COUNT = 200;

	/**
	 * 
	 */
	public ExtractPicturesWorker() {
		// TODO Auto-generated constructor stub
	}

	// default
	public static boolean extractPictureToFile(String videoPath, int second) {
		return extractPictureToFile(videoPath, second,
				AppConfigs.APP_GIF_TEMP_FILES_FOLDER_PATH, second + "");
	}

	/*
	 * 从指定的video中提取指定时间点的图片文件 目前只能生成为jep格式
	 */
	public static boolean extractPictureToFile(String videoPath, int second,
			String outPath, String fileName) {
		boolean success = true;

		if (TextUtils.isEmpty(videoPath)) {
			logd("empty video path");
			return false;
		}

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(videoPath);
		// 取得视频的长度(单位为毫秒)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int total = Integer.valueOf(time) / 1000;

		if (second < 0 || second > total) {
			loge("unavalible second(" + second + "), total(" + total + ")");
			return false;
		}

		Bitmap bitmap = retriever.getFrameAtTime(second * 1000 * 1000,
				MediaMetadataRetriever.OPTION_CLOSEST);
		String path = outPath + "/" + fileName + ".jpg";

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.close();
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		return success;
	}
	
	/*
	 * videoPath: 视频路径
	 * beginPos: 起始点，单位秒
	 * endPos: 结束点，单位秒
	 * frameRate：截取率，大小为 x帧/秒，人眼能容忍的一般为25帧/秒，这样的gif看起来比较连贯
	 */
	public static int extractPicturesToFile(String videoPath, String outPath, int beginPos, int endPos, int frameRate) {

		if (TextUtils.isEmpty(videoPath)) {
			loge("empty video path");
			return 0;
		}
		
		if (frameRate <= 0) {
			loge("unavalible frameRate( " + frameRate + ")");
			return 0;			
		}

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(videoPath);
		// 取得视频的长度(单位为毫秒)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int total = Integer.valueOf(time) / 1000;

		if (endPos <= beginPos || endPos < 0 || endPos > total) {
			loge("unavalible beginPos(" + beginPos + "), and unavalible endPos("  + endPos +"), total(" + total + ")");
			return 0;
		}
		
		int count = 0;
		
		// 计算时间点并导出图片
		long beginPosUs = beginPos*1000*1000;
		int usSpace = (1*1000*1000) / frameRate;
		int totalS = endPos - beginPos;
		int totalFrames = frameRate*totalS + 1;
		logd("need total frames = " + totalFrames);
		for (int i = 0; i < totalFrames; i++) {
			long usP = (beginPosUs + (i * usSpace));
			Bitmap bitmap = retriever.getFrameAtTime(usP, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			
			if (bitmap == null) {
				logd("cant get frame in us(" + usP + ")");
				continue;				
			}

			logd("got frame in us(" + usP + ")");
			String path = outPath + "/" + i + ".jpg";

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bitmap.compress(CompressFormat.JPEG, 80, fos);
				fos.close();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		}

		return count;
	}

	/*
	 * videoPath: 视频路径
	 * outPath: 解析出的图片保存位置
	 * beginPos: 起始点，单位秒
	 * endPos: 结束点，单位秒
	 * 该方法估计一个合理的frameRate，然后再截取
	 */
	public static int extractPicturesToFile(String videoPath,
			String outPath, int beginPos, int endPos) {
		int frameRate = 0;
		int totalSecs = endPos - beginPos;
		
		if (totalSecs <= 0) {
			loge("unavalible secs");
			return 0;
		}
		frameRate = MAX_FRAME_COUNT / totalSecs;
		if (frameRate < DEFAULT_FRAME_RATE) {
			frameRate = DEFAULT_FRAME_RATE;			
		}
		
		if (frameRate > MAX_FRAME_RATE) {
			frameRate = MAX_FRAME_RATE;
		}

		logd("resonable framerate = " + frameRate);

		return extractPicturesToFile(videoPath, outPath, beginPos, endPos, frameRate);
	}

	/*
	 * videoPath: 视频路径
	 * outPath: 解析出的图片保存位置
	 * beginPos: 起始点，单位秒
	 * endPos: 结束点，单位秒
	 * 该方法每秒只取3张图
	 */
	public static int extractPicturesToFileHalf(String videoPath,
			String outPath, int beginPos, int endPos) {
		int totalSecs = endPos - beginPos;
		if (totalSecs <= 0) {
			loge("unavalible secs");
			return 0;
		}

		return extractPicturesToFile(videoPath, outPath, beginPos, endPos, 2);

	}

	/*
	 * 从指定的video中提取指定时间点的图片, 返回bitmap格式对象
	 */
	public static Bitmap extractBitmap(String videoPath, int second) {
		if (TextUtils.isEmpty(videoPath)) {
			logd("extractBitmap empty video path");
			return null;
		}

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(videoPath);
		// 取得视频的长度(单位为毫秒)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int total = Integer.valueOf(time) / 1000;

		if (second < 0 || second > total) {
			loge("unavalible second(" + second + "), total(" + total + ")");
			return null;
		}

		Bitmap bitmap = retriever.getFrameAtTime(second * 1000 * 1000,
				MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

		return bitmap;
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
