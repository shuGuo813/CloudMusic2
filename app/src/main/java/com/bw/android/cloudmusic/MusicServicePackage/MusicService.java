package com.bw.android.cloudmusic.MusicServicePackage;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.bw.android.cloudmusic.EventValuesBean.EventMusicCloudBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicListBean;
import com.bw.android.cloudmusic.EventValuesBean.EventPlayMusicBean;

public class MusicService extends Service {
    private static MediaPlayer mediaPlayer;
    private static EventPlayMusicBean eventPlayMusicBean;
    private static EventMusicListBean eventMusicListBean;
    private static EventMusicCloudBean eventMusicCloudBean;

    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //播放器单例
    public static MediaPlayer getInstanceMedis(){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }else{
            return mediaPlayer;
        }
        return mediaPlayer;
    }
    //播放音乐单例
    public static EventPlayMusicBean getInstancePlayMusic(){
        if(eventPlayMusicBean == null){
            eventPlayMusicBean = new EventPlayMusicBean();
        }else{
            return eventPlayMusicBean;
        }
        return eventPlayMusicBean;
    }
    //音乐库单例
    public static EventMusicCloudBean getInstanceMusicCloud(){
        if(eventMusicCloudBean == null){
            eventMusicCloudBean = new EventMusicCloudBean();
        }else{
            return eventMusicCloudBean;
        }
        return eventMusicCloudBean;
    }
    //音乐列表
    public static EventMusicListBean getInstanceMusicList(){
        if(eventMusicListBean == null){
           eventMusicListBean = new EventMusicListBean();
        }else{
            return eventMusicListBean;
        }
        return eventMusicListBean;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getInstanceMedis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
