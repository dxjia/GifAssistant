package com.wind.gifassistant.views.gifview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.wind.gifassistant.utils.AppConfigs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * GifView<br>
 * 本类可以显示一个gif动画，其使用方法和android的其它view（如imageview)一样。<br>
 * 如果要显示的gif太大，会出现OOM的问题。
 * @author liao
 *
 */
public class GifView extends View implements GifAction{
	
	private static String TAG = AppConfigs.APP_TAG + "GifView";
	private static boolean DEBUG = true;
	private static boolean DEBUG_DRAW_THREAD = true;

	/**gif解码器*/
	private GifDecoder gifDecoder = null;
	/**当前要画的帧的图*/
	private Bitmap currentImage = null;
	
	private boolean mStop = false;
	
	private boolean pause = false;
	
	private boolean mShowCover = false;

	private int mShowWidth = -1;
	private int mShowHeight = -1;
	private Rect rect = new Rect();
	
	private DrawThread drawThread = null;
	
	private GifParsingShowType animationType = GifParsingShowType.SYNC_DECODER;
	
	private GifShowGravity mShowGravity = GifShowGravity.FILL_FULL_SCREEN;
	
	private GifSourceInfos mGifSourceInfos = null;

	/**
	 * 解码过程中，Gif动画显示的方式<br>
	 * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
	 * @author liao
	 *
	 */
	public enum GifParsingShowType{
		/**
		 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
		 */
		WAIT_FINISH (0),
		/**
		 * 和解码过程同步，解码进行到哪里，图片显示到哪里
		 */
		SYNC_DECODER (1),
		/**
		 * 在解码过程中，只显示第一帧图片
		 */
		COVER (2);
		
		GifParsingShowType(int i){
			nativeInt = i;
		}
		final int nativeInt;
	}
	
	/**
	 * Gif的显示方式，居中，满屏，
	 *
	 */
	public enum GifShowGravity{
		/**
		 * 满铺
		 */
		FILL_FULL_SCREEN (0),

		/**
		 * 上下左右都居中
		 */
		CENTER_FULL (1),

		/**
		 * 只保证竖向居中
		 */
		CENTER_VERTICAL (2),

		/**
		 * 只保证横向居中
		 */
		CENTER_HORIZONTAL(3);

		GifShowGravity(int i){
			nativeInt = i;
		}
		final int nativeInt;
	}


	/**
	 * 正在显示的GIF图片的一些文件信息
	 * GifView其实并不真正需要这些信息
	 * 加在这里是为了DEBUG方便
	 */
	public enum GifImageType{
		/**
		 * 从文件显示
		 */
		GIF_TYPE_FROM_FILE (0),
		/**
		 * 从资源文件显示
		 */
		GIF_TYPE_FROM_RES (1),
		/**
		 * 从byte字节数据显示
		 */
		GIF_TYPE_FROM_DATA (2),

		/**
		 * 从byte字节数据显示
		 */
		GIF_TYPE_FROM_INPUTSTREAM (3);
		
		GifImageType(int i){
			nativeInt = i;
		}
		final int nativeInt;
	}

	private class GifSourceInfos {
		private GifImageType mGifType;
		
		private String infos;

		public GifSourceInfos(GifImageType type) {
			mGifType = type;
			infos = "";
		}
		
		public void setInfo(String info) {
			infos = info;
		}

		public String toString() {
			String res;
			switch(mGifType) {
			case GIF_TYPE_FROM_FILE:
				res = "GIF_TYPE_FROM_FILE: " + infos;
				break;
			case GIF_TYPE_FROM_RES:
				res = "GIF_TYPE_FROM_RES: " + infos;
				break;
			case GIF_TYPE_FROM_DATA:
				res = "GIF_TYPE_FROM_DATA: ";
				break;
			case GIF_TYPE_FROM_INPUTSTREAM:
				res = "GIF_TYPE_FROM_INPUTSTREAM: ";
				break;
			default: res = "GIF_TYPE ERROR ";break;			
			}
			return res;
		}

	}
	
	public GifView(Context context) {
        super(context);
        
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
    }
    
    /**
     * 设置图片，并开始解码
     * @param gif 要设置的图片
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     * 
     */
    private void showGifDecoderImage(byte[] gif, boolean justShowCover){
    	if(gifDecoder != null){  		
    		gifDecoder.free();
    		gifDecoder = null;
    	}
    	mShowCover = justShowCover;
    	gifDecoder = new GifDecoder(gif,this, mShowCover);
    	gifDecoder.start();
    }
    
    /**
     * 设置图片，开始解码
     * @param is 要设置的图片
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     */
    private void showGifDecoderImage(InputStream is, boolean justShowCover){
    	if(gifDecoder != null){
    		gifDecoder.free();
    		gifDecoder= null;
    	}
    	mShowCover = justShowCover;
    	gifDecoder = new GifDecoder(is,this, mShowCover);
    	gifDecoder.start();
    }
    
    /**
     * 以字节数据形式设置gif图片
     * @param gif 图片
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     */
    public void showGifImage(byte[] gif, boolean justShowCover){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_DATA);
    	
    	showGifDecoderImage(gif, justShowCover);
    }
    
    /**
     * 以字节流形式设置gif图片
     * @param is 图片
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     */
    public void showGifImage(InputStream is, boolean justShowCover){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_INPUTSTREAM);

    	showGifDecoderImage(is, justShowCover);
    }
    
    /**
     * 以资源形式设置gif图片
     * @param resId gif图片的资源ID
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     */
    public void showGifImage(int resId, boolean justShowCover){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_RES);
        mGifSourceInfos.setInfo("resId [" + resId + "]");

        Resources r = this.getResources();
    	InputStream is = r.openRawResource(resId);
    	showGifDecoderImage(is, justShowCover);
    }

    /**
     * 以文件路径形式设置gif图片
     * @param path gif图片的文件路径
     * @param justShowCover 是否只显示第一帧，当该参数为true时，
     *        decoder只解析得到一张图片就为停止工作
     */
    public void showGifImage(String path, boolean justShowCover){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_FILE);
    	mGifSourceInfos.setInfo("path [" + path + "]");

    	File f = new File(path);
    	InputStream is;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	showGifDecoderImage(is, justShowCover);
    }

    /**
     * 以字节数据形式设置gif图片
     * @param gif 图片
     */
    public void showGifImage(byte[] gif){
    	showGifDecoderImage(gif, false);
    }
    
    /**
     * 以字节流形式设置gif图片
     * @param is 图片
     */
    public void showGifImage(InputStream is){
    	showGifDecoderImage(is, false);
    }
    
    /**
     * 以资源形式设置gif图片
     * @param resId gif图片的资源ID

     */
    public void showGifImage(int resId){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_RES);
        mGifSourceInfos.setInfo("resId [" + resId + "]");
    	Resources r = this.getResources();
    	InputStream is = r.openRawResource(resId);
    	showGifDecoderImage(is, false);
    }

    /**
     * 以文件路径形式设置gif图片
     * @param path gif图片的文件路径
     */
    public void showGifImage(String path){
    	// 记录gif图片信息，方便debug
    	mGifSourceInfos = new GifSourceInfos(GifImageType.GIF_TYPE_FROM_FILE);
    	mGifSourceInfos.setInfo("path [" + path + "]");
    	File f = new File(path);
    	InputStream is;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	showGifDecoderImage(is, false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(gifDecoder == null)
        	return;
        if(currentImage == null){
        	currentImage = gifDecoder.getImage();
        }
        if(currentImage == null){
        	return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        
        calculateDrawRect();
        canvas.drawBitmap(currentImage, null, rect, null);

        canvas.restoreToCount(saveCount);
    }
    
    private void calculateDrawRect() {
    	
    	// 先假设最大化
    	int viewWidth = getWidth();
    	int viewHeight = getHeight();
    	int imageWidth;
    	int imageHeight;

    	switch(mShowGravity) {
    	case FILL_FULL_SCREEN:
    		// 满铺会自动拉伸长宽，会忽略 setShowDimension 的设置
        	rect.left = 0;
        	rect.top = 0;
        	rect.right = viewWidth;
        	rect.bottom = viewHeight;
    		break;
    	case CENTER_FULL:
    		imageWidth = currentImage.getWidth();
    		imageHeight = currentImage.getHeight();
    		//logd("imageWidth = " + imageWidth +  ",  imageHeight = " + imageHeight);
    		//logd("viewWidth = " + viewWidth +  ",  viewHeight = " + viewHeight);
    		// 总以其中一个边长全部填充
            boolean calculateDone = false;
            // 先尝试填充满高
   			int centerH = (int) Math.floor(viewWidth/2);
   			int newHeight = viewHeight;
   			int halfNewWidth = (int) Math.floor(((double)imageWidth*(double)newHeight/(double)imageHeight)/2);
   			if (2*halfNewWidth > viewWidth) {
   				calculateDone = false;
   			} else {
    			rect.left = centerH - halfNewWidth;
    			rect.top = 0;
    			rect.right = centerH + halfNewWidth;
    			rect.bottom = newHeight;
    			calculateDone = true;
   			}
   			// 如果上面的可以使gif图片显示在view内，则不会进行下面的计算
   			// 否则以宽为填充重新计算
   			if (!calculateDone) {
    			int centerV = (int) Math.floor(viewHeight/2);
    			int newWidth = viewWidth;
    			int halfNewHeight = (int) Math.floor(((double)imageHeight*(double)newWidth/(double)imageWidth)/2);
    			rect.left = 0;
    			rect.top = centerV - halfNewHeight;
    			rect.right = newWidth;
    			rect.bottom =  centerV + halfNewHeight;	
   			}
   			//logd("rect[(" + rect.left + ", " + rect.top + ") (" + rect.right + ", " + rect.bottom + ")]");
    		break;
    	case CENTER_VERTICAL:
    		break;
    	case CENTER_HORIZONTAL:
    		break;
    	default: break;

    	}
    	// user have set the show size
    	if (mShowWidth != -1 && mShowHeight != -1) {

    	}
    }

    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        int widthSize;
        int heightSize;
        
        int w;
        int h;
        
        if(gifDecoder == null){
        	w = 1;
        	h = 1;
        }else{
        	w = gifDecoder.width;
        	h = gifDecoder.height;
        }
        
        w += pleft + pright;
        h += ptop + pbottom;
            
        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        widthSize = resolveSize(w, widthMeasureSpec);
        heightSize = resolveSize(h, heightMeasureSpec);
        
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation(){
    	if(pause){
    		pause = false;
    	}
    }
    
    /**
     * 设置gif在解码过程中的显示方式<br>
     * <strong>本方法只能在showGifImage方法之前设置，否则设置无效</strong>
     * @param type 显示方式
     */
    public void setGifParsingShowType(GifParsingShowType type){
    	if(gifDecoder == null) {
    		animationType = type;
    	}
    }
    
    /**
     * 设置gif的显示方式:居中、满铺等<br>
     * <strong>本方法只能在showGifImage方法之前设置，否则设置无效</strong>
     * @param showGravity 显示方式
     */
    public void setGifShowGravity(GifShowGravity showGravity) {
    	if(gifDecoder == null) {
    		mShowGravity = showGravity;
    	}
    }
    
    /**
     * 设置要显示的图片的大小<br>
     * 当设置了图片大小 之后，会按照设置的大小来显示gif（按设置后的大小来进行拉伸或压缩）
     * @param width 要显示的图片宽
     * @param height 要显示的图片高
     */
    public void setShowDimension(int width,int height){
    	if(width > 0 && height > 0){
	    	mShowWidth = width;
	    	mShowHeight = height;
			rect.left = 0;
			rect.top = 0;
			rect.right = width;
			rect.bottom = height;
    	}
    }

    public void parseOk(boolean parseStatus,int frameIndex){
    	if(parseStatus){
    		if(gifDecoder != null){
    			logd("parseOk, frameIndex = " + frameIndex);
    			switch(animationType){
    			case WAIT_FINISH:
    				if(frameIndex == -1){
    					if(gifDecoder.getFrameCount() > 1){     //当帧数大于1时，启动动画线程
    						if (!mShowCover) {
    	    				    DrawThread dt = new DrawThread(mGifSourceInfos.toString());
    	    	    		    dt.start();
    						} else {
    							reDraw();
    						}
    	    			}else{
    	    				reDraw();
    	    			}
    				}
    				break;
    			case COVER:
    				if(frameIndex == 1) {
    					currentImage = gifDecoder.getImage();
    					reDraw();
    				}else if(frameIndex == -1){
    					if(gifDecoder.getFrameCount() > 1){
    						if (!mShowCover) {
    						    if(drawThread == null){
        						    drawThread = new DrawThread(mGifSourceInfos.toString());
        						    drawThread.start();
        					    }
    						} else {
    							reDraw();
    						}
    					}else{
    						reDraw();
    					}
    				}
    				break;
    			case SYNC_DECODER:
    				if(frameIndex == 1){
    					currentImage = gifDecoder.getImage();
    					reDraw();
    				}else if(frameIndex == -1){
    					reDraw();
    				}else{
    					if (!mShowCover) {
    					    if(drawThread == null){
    						    drawThread = new DrawThread(mGifSourceInfos.toString());
    						    drawThread.start();
    					    }
    					} else {
    						reDraw();
    					}
    				}
    				break;
    			}
 
    		}else{
    			Log.e("gif","parse error");
    		}
    		
    	}
    }
    
    private void reDraw(){
    	if(redrawHandler != null){
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
    	}
    }
    
    private Handler redrawHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		invalidate();
    	}
    };
    
    /**
     * 动画线程
     * @author liao
     *
     */
    private class DrawThread extends Thread{
    	private final String mGifInfos;
    	/* 构造
    	 * @param gifinfos 要显示的gif图片的一些文件信息
    	 * 用于调试用的字串，如文件路径等
    	 */
    	public DrawThread(String gifinfos) {
    		mGifInfos = gifinfos;    		
    	}

    	public void run(){
    		if(DEBUG_DRAW_THREAD) {
    			Log.d(TAG, "DrawThread[" + getId() + "]" + " started, " + mGifInfos);
    		}
    		if(gifDecoder == null){
    			if(DEBUG_DRAW_THREAD) {
    				Log.d(TAG, "DrawThread[" + getId() + "]" + " exit for (gifDecoder == null)");
    			}
    			return;
    		}

    		while(!mStop){
    			if(pause == false){
	    			//if(gifDecoder.parseOk()){
	    				GifFrame frame = gifDecoder.next();
	    				currentImage = frame.image;
	    				long sp = frame.delay;	    				
	    				if(redrawHandler != null){
	    					Message msg = redrawHandler.obtainMessage();
	    					redrawHandler.sendMessage(msg);
	    					SystemClock.sleep(sp); 
	    				}else{
	    					break;
	    				}
//	    			}else{
//	    				currentImage = gifDecoder.getImage();
//	    				break;
//	    			}
    			}else{
    				SystemClock.sleep(10);
    			}
    		}
    		if(DEBUG_DRAW_THREAD) {
    		    Log.d(TAG, "DrawThread[" + getId() + "]" + " exit");
    		}
    	}
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setDrawThreadOut();
        if (currentImage != null && !currentImage.isRecycled()) {
        	currentImage.recycle();
        	currentImage = null;
        }
    	if(gifDecoder != null) {  		
    		gifDecoder.free();
    		gifDecoder = null;
    	}
    }

    public void setDrawThreadOut() {
    	mStop = true;
    }

    private void logd(String message) {
    	if(DEBUG) {
    		Log.d(TAG, message);    		
    	}
    }
    
}
