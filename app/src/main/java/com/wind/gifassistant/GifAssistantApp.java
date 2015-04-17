package com.wind.gifassistant;

import com.wind.gifassistant.utils.AppCrashHandler;

import android.app.Application;

public class GifAssistantApp  extends Application {  
    @Override  
    public void onCreate() {  
        super.onCreate();
        AppCrashHandler crashHandler = AppCrashHandler.getInstance();  
        //crashHandler.init(getApplicationContext());  
    }  
}