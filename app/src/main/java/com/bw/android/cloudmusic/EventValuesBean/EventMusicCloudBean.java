package com.bw.android.cloudmusic.EventValuesBean;

import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;

import java.util.ArrayList;

public class EventMusicCloudBean {
    int index;
    ArrayList<MusicCloudBean> list;
    boolean musicCloudFlag;

    public boolean isMusicCloudFlag() {
        return musicCloudFlag;
    }

    public void setMusicCloudFlag(boolean musicCloudFlag) {
        this.musicCloudFlag = musicCloudFlag;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setList(ArrayList<MusicCloudBean> list) {
        this.list = list;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<MusicCloudBean> getList() {
        return list;
    }
}
