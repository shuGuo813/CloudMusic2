package com.bw.android.cloudmusic.MusicApplication;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public class CloudMusicApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mContext = getApplicationContext();
    }
    public static Context getmContext(){
        return  mContext;
    }
}
