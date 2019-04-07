package com.bw.android.cloudmusic.MusicCloudPackage;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.android.cloudmusic.EventValuesBean.EventMainActivityBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicCloudBean;
import com.bw.android.cloudmusic.EventValuesBean.EventSendMainActivityBean;
import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicFragment;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicCloudBeanDao;
import com.example.anonymous.greendao.PlayMusicBeanDao;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicCloudFragment extends Fragment implements MusicCloudAdapter.MusicGetItemClick{

    Unbinder unbinder;
    @BindView(R.id.rev_musicCloud)
    RecyclerView revMusicCloud;
    ArrayList<MusicCloudBean> Musiclist = new ArrayList<>();
    boolean flag = false;
    MusicCloudBeanDao musicCloudBeanDao;
    PlayMusicBeanDao playMusicBeanDao;
    ImageView ivmusicImg;
    ImageView ivcontrolMusic;
    TextView  tvmusicName;
    TextView  tvmusicMakerName;
    MediaPlayer mediaPlayer;
    SeekBar sbMusicPlan;
    boolean isStop = false;
    TextView tvcurration;
    TextView tvduration;
    public MusicCloudFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_cloud, container, false);
        unbinder = ButterKnife.bind(this, view);
        initMediaPlayer();
        initseekbar();
        initview();
        return view;
    }

    private void initMediaPlayer() {
        mediaPlayer = MusicService.getInstanceMedis();
    }

    private void initseekbar() {
        sbMusicPlan = getActivity().findViewById(R.id.sb_musicPlan);
        tvcurration = getActivity().findViewById(R.id.tv_curration);
        tvduration = getActivity().findViewById(R.id.tv_duration);
        sbMusicPlan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void initview() {
        ivmusicImg = getActivity().findViewById(R.id.iv_musicImg);
        ivcontrolMusic = getActivity().findViewById(R.id.iv_controlMusic);
        tvmusicName = getActivity().findViewById(R.id.tv_musicName);
        tvmusicMakerName = getActivity().findViewById(R.id.tv_musicMakerName);
        musicCloudBeanDao = EntityManager.getInstance().getUserDao();
        playMusicBeanDao = EntityManager.getInstance().getPlayMusicDao();
        MusicCloudAdapter adapter = new MusicCloudAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        revMusicCloud.setLayoutManager(manager);
        revMusicCloud.setItemAnimator(new DefaultItemAnimator());
        revMusicCloud.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter.setGetItemClick(this);
        ContentResolver resolver = getActivity().getContentResolver();
        MusicCloudBeanDao beanDao = EntityManager.getInstance().getUserDao();
        beanDao.deleteAll();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //歌曲的名称
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小
                int size = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//                Log.v("LSG",id + "专辑图片");
//                Log.v("LSG","" +
//                        "歌名 =" + title + " - "  + "专辑名 =" + album + " - " + "歌手名 =" + artist + " - " +
//                        "路径 =" + url  + " - " + "总播放时长 =" + duration + " - " + "大小 -" + size);
                MusicCloudBean cloudBean = new MusicCloudBean(null, title, album, artist, url, duration, size);
                beanDao.insert(cloudBean);
            }

        }
        Musiclist = (ArrayList<MusicCloudBean>) beanDao.loadAll();
        adapter.refresh(Musiclist);
        revMusicCloud.setAdapter(adapter);
        if (cursor != null) {
            cursor.close();
        }

    }
        //获取专辑图片
//    private void getImage(int id) {
//        int album_id = id;
//        String albumArt = getAlbumArt(album_id);
//        Bitmap bm = null;
//        if (albumArt == null) {
//            ivShowimgCloud.setBackgroundResource(R.mipmap.fhzm);
//        } else {
//            bm = BitmapFactory.decodeFile(albumArt);
//            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
//            ((ImageView)ivShowimgCloud).setImageDrawable(bmpDraw);
//        }
//    }

    private String getAlbumArt(int album_id)
    {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getActivity().getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStop = true;
    }


    @Override
    public void getdata(int index) {
        EventMainActivityBean mainActivityBean = new EventMainActivityBean();
        mainActivityBean.setMusicName(Musiclist.get(index).title);
        mainActivityBean.setMusicMakerName(Musiclist.get(index).artist);
        EventBus.getDefault().postSticky(mainActivityBean);
        EventMusicCloudBean musicCloudBean = MusicService.getInstanceMusicCloud();
        musicCloudBean.setIndex(index);
        musicCloudBean.setList(Musiclist);
        musicCloudBean.setMusicCloudFlag(true);
        MusicService.getInstanceMusicList().setMusicListFlag(false);
        MusicService.getInstanceMusicList().setMusicList(new ArrayList<MusicListBean>());
        MusicService.getInstancePlayMusic().setPlayMusicFlag(false);
        MusicService.getInstancePlayMusic().setList(new ArrayList<PlayMusicBean>());
        EventBus.getDefault().postSticky(musicCloudBean);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(Musiclist.get(index).url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            //开启线程
            sbMusicPlan.setMax(mediaPlayer.getDuration());
            tvduration.setText(formatTime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //点击item项的时候把歌曲添加至最近播放列表
        final ArrayList<PlayMusicBean> playMusicBeanArrayList = (ArrayList<PlayMusicBean>) playMusicBeanDao.loadAll();
        PlayMusicBean bean = new PlayMusicBean(null, Musiclist.get(index).title, Musiclist.get(index).album, Musiclist.get(index).artist,
                Musiclist.get(index).url, Musiclist.get(index).duration, Musiclist.get(index).size);
        for (int i = 0; i < playMusicBeanArrayList.size(); ++i) {
            if (playMusicBeanArrayList.get(i).getTitle().equals(bean.getTitle())) {
                flag = true;
                break;
            } else {
                flag = false;
            }
        }
        if (flag == false) {
            playMusicBeanDao.insert(bean);
            Toast.makeText(getContext(), Musiclist.get(index).title + "添加至最近播放列表", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), Musiclist.get(index).title + "已存在 添加失败", Toast.LENGTH_SHORT).show();
        }
        //更新最下方的控件
        tvmusicName.setText(Musiclist.get(index).title);
        tvmusicMakerName.setText(Musiclist.get(index).artist);
        ivcontrolMusic.setImageResource(R.drawable.ic_pausemusic);
    }
    //获取歌曲时长
    private String formatTime(int length){
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String TotalTime = simpleDateFormat.format(date);
        return TotalTime;
    }
}
