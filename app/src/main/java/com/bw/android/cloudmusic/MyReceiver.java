package com.bw.android.cloudmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.bw.android.cloudmusic.EventValuesBean.EventSendMainActivityBean;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;

import org.greenrobot.eventbus.EventBus;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MediaPlayer mediaPlayer = MusicService.getInstanceMedis();
        switch (action){
            case "com.cloud.controlMusic":
                EventSendMainActivityBean  bean = new EventSendMainActivityBean();
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    bean.setFlag(1);
                }else{
                    mediaPlayer.start();
                    bean.setFlag(2);
                }
                EventBus.getDefault().post(bean);
                break;
            case "com.cloud.previousMusic":
                Toast.makeText(context, "上一曲", Toast.LENGTH_SHORT).show();
                break;
            case "com.cloud.nextMusic":
                Toast.makeText(context, "下一曲", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
