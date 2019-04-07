package com.bw.android.cloudmusic.MusicCloudPackage;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class MusicCloudBean {
    @Id(autoincrement = true)
    Long id;
    //歌曲的名称
    String title;
    //歌曲的专辑名
    String album;
    //歌曲的歌手名
    String artist;
    //歌曲文件的路径
    String url;
    //歌曲的总播放时长
    long duration;
    //歌曲文件的大小
    int size;
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getDuration() {
        return this.duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getAlbum() {
        return this.album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 471877705)
    public MusicCloudBean(Long id, String title, String album, String artist,
            String url, long duration, int size) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
        this.size = size;
    }
    @Generated(hash = 260231851)
    public MusicCloudBean() {
    }
   
   

}
