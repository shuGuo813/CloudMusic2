package com.bw.android.cloudmusic.EventValuesBean;

import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;

import java.util.ArrayList;

public class EventMusicListBean {
    int index;
    ArrayList<MusicListBean> musicList;
    boolean musicListFlag;

    public boolean isMusicListFlag() {
        return musicListFlag;
    }

    public void setMusicListFlag(boolean musicListFlag) {
        this.musicListFlag = musicListFlag;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<MusicListBean> getMusicList() {
        return musicList;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMusicList(ArrayList<MusicListBean> musicList) {
        this.musicList = musicList;
    }
}
