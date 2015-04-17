/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-14
 */
package com.wind.gifassistant.data;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaMetadataRetriever;

/**
 *
 * @author Djia
 * @time 2014-8-14下午2:14:57
 * @instuction 
 */
public class VideosInfo {
	private String mName;
	private String mLastModifyTime;
	private String mDuration;
	private String mPath;

	/**
	 * 
	 */
	public VideosInfo(String path) {
		// TODO Auto-generated constructor stub
		parseFromPath(path);
		mPath = path;
	}


	public String getName() {
		return mName;
	}

	public String getPath() {
		return mPath;
	}

	public String getLastModifyTime() {
		return mLastModifyTime;
	}

	public String getDuration() {
		return mDuration;
	}

	private void parseFromPath(String path) {
		File f = new File(path);
		String name = f.getName();
		int length = name.length();
		mName = f.getName().substring(0, length - 4);
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		Date date = new Date(f.lastModified());
		mLastModifyTime = formatter.format(date);
		mDuration = formatFileDuration(path);
	}

	private String formatFileDuration(String path) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(path);
		// 取得视频的长度(单位为毫秒)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int totalS = Integer.valueOf(time) / 1000;

		DecimalFormat df = new DecimalFormat("#.00");
		String totalDuration = "";
		if (totalS < 60) {
			totalDuration = df.format((double) totalS) + "秒";
		} else if (totalS < 3600) {
			totalDuration = df.format((double) totalS / 60) + "分";
		} else if (totalS < 3600*24) {
			totalDuration = df.format((double) totalS / 3600) + "时";
		}

		return totalDuration;
	}	
}
