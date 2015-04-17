/**
 * Copyright  2014  Djia
 * All right reserved.
 * 
 * Created on 2014-8-11
 */
package com.wind.gifassistant.data;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Djia
 * @time 2014-8-11上午10:57:08
 * @instuction
 */
public class GifProductInfo {
	private String mGifName;
	private String mGifPath;
	private String mLastModifyTime;
	private String mFileSize;

	public GifProductInfo(String gifPath) {
		// TODO Auto-generated constructor stub
		parseFromPath(gifPath);
		mGifPath = gifPath;
	}

	public String getName() {
		return mGifName;
	}

	public String getPath() {
		return mGifPath;
	}

	public String getLastModifyTime() {
		return mLastModifyTime;
	}

	public String getFileSize() {
		return mFileSize;
	}

	private void parseFromPath(String gifPath) {
		File f = new File(gifPath);
		String name = f.getName();
		int length = name.length();
		mGifName = f.getName().substring(0, length - 4);
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		Date date = new Date(f.lastModified());
		mLastModifyTime = formatter.format(date);
		mFileSize = FormetFileSize(f.length());
	}

	private String FormetFileSize(long filesize) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (filesize < 1024) {
			fileSizeString = df.format((double) filesize) + "B";
		} else if (filesize < 1048576) {
			fileSizeString = df.format((double) filesize / 1024) + "K";
		} else if (filesize < 1073741824) {
			fileSizeString = df.format((double) filesize / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) filesize / 1073741824) + "G";
		}
		return fileSizeString;
	}

}
