package com.bw.android.cloudmusic.EventValuesBean;

import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;

import java.util.ArrayList;

public class EventPlayMusicBean {
    int index;
    ArrayList<PlayMusicBean> playMusicList;
    boolean playMusicFlag;

    public boolean isPlayMusicFlag() {
        return playMusicFlag;
    }

    public void setPlayMusicFlag(boolean playMusicFlag) {
        this.playMusicFlag = playMusicFlag;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setList(ArrayList<PlayMusicBean> list) {
        this.playMusicList = list;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<PlayMusicBean> getList() {
        return playMusicList;
    }
}
