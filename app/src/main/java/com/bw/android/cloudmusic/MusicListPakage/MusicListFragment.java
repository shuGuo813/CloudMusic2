package com.bw.android.cloudmusic.MusicListPakage;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bw.android.cloudmusic.EventValuesBean.EventMusicListBean;
import com.bw.android.cloudmusic.EventValuesBean.EventPlayMusicBean;
import com.bw.android.cloudmusic.EventValuesBean.EventSendMainActivityBean;
import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicFragment;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;
import com.example.anonymous.greendao.PlayMusicBeanDao;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment implements MusicListAdapter.MusicListGetItemClick {

    Unbinder unbinder;
    @BindView(R.id.rev_musiclist)
    RecyclerView revMusiclist;
    ArrayList<MusicListBean> list = new ArrayList<>();
    boolean flag = false;
    PlayMusicBeanDao playMusicBeanDao;
    ImageView ivmusicImg;
    ImageView ivcontrolMusic;
    TextView tvmusicName;
    TextView  tvmusicMakerName;
    MediaPlayer mediaPlayer;
    SeekBar sbmusicplan;
    boolean isStop;
    TextView tvcurration;
    TextView tvduration;
    public MusicListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initMediaPlayer();
        initSeekBar();
        initview();
        return view;
    }

    private void initSeekBar() {
        sbmusicplan = getActivity().findViewById(R.id.sb_musicPlan);
        sbmusicplan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    private void initMediaPlayer() {
        mediaPlayer = MusicService.getInstanceMedis();
        //判断歌曲有没有结束
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivcontrolMusic.setImageResource(R.drawable.ic_startmusic);
                sbmusicplan.setProgress(0);
                mediaPlayer.seekTo(0);
            }
        });
    }

    private void initview() {
        ivmusicImg = getActivity().findViewById(R.id.iv_musicImg);
        ivcontrolMusic = getActivity().findViewById(R.id.iv_controlMusic);
        tvmusicName = getActivity().findViewById(R.id.tv_musicName);
        tvmusicMakerName = getActivity().findViewById(R.id.tv_musicMakerName);
        tvcurration = getActivity().findViewById(R.id.tv_curration);
        tvduration = getActivity().findViewById(R.id.tv_duration);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        revMusiclist.setLayoutManager(manager);
        revMusiclist.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        revMusiclist.setItemAnimator(new DefaultItemAnimator());
        MusicListBeanDao beanDao = EntityManager.getInstance().getUserListDao();
        playMusicBeanDao = EntityManager.getInstance().getPlayMusicDao();
        list = (ArrayList<MusicListBean>) beanDao.loadAll();
        MusicListAdapter adapter = new MusicListAdapter();
        Log.v("LSG","MusicListFragment =" + list.size());
        adapter.refresh(list);
        adapter.setGetItemClick(this);
        revMusiclist.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStop = true;
    }

    @Override
    public void getdata(int index) {
        EventMainActivityBean mainActivityBean = new EventMainActivityBean();
        mainActivityBean.setMusicName(list.get(index).title);
        mainActivityBean.setMusicMakerName(list.get(index).artist);
        EventBus.getDefault().postSticky(mainActivityBean);
        EventMusicListBean musicListBean = MusicService.getInstanceMusicList();
        musicListBean.setIndex(index);
        musicListBean.setMusicList(list);
        musicListBean.setMusicListFlag(true);
        MusicService.getInstanceMusicCloud().setList(new ArrayList<MusicCloudBean>());
        MusicService.getInstanceMusicCloud().setMusicCloudFlag(false);
        MusicService.getInstancePlayMusic().setList(new ArrayList<PlayMusicBean>());
        MusicService.getInstancePlayMusic().setPlayMusicFlag(false);
        EventBus.getDefault().postSticky(musicListBean);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(list.get(index).url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            //开启线程
            sbmusicplan.setMax(mediaPlayer.getDuration());
            tvduration.setText(formatTime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ArrayList<PlayMusicBean>  playMusicBeanArrayList  = (ArrayList<PlayMusicBean>) playMusicBeanDao.loadAll();
        PlayMusicBean bean = new PlayMusicBean(null,list.get(index).title, list.get(index).album,list.get(index).artist,
                list.get(index).url, list.get(index).duration,list.get(index).size);
        for(int i = 0; i < playMusicBeanArrayList.size(); ++i){
            if(playMusicBeanArrayList.get(i).getTitle().equals(bean.getTitle())){
                flag = true;
                break;
            }else{
                flag = false;
            }
        }
        //更新最下方的控件
        tvmusicName.setText(list.get(index).title);
        tvmusicMakerName.setText(list.get(index).artist);
        ivcontrolMusic.setImageResource(R.drawable.ic_pausemusic);
        if(flag == false){
            playMusicBeanDao.insert(bean);
            Toast.makeText(getContext(), list.get(index).title + "添加至最近播放列表", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), list.get(index).title + "已存在 添加失败", Toast.LENGTH_SHORT).show();
        }
    }
    //获取歌曲时长
    private String formatTime(int length){
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String TotalTime = simpleDateFormat.format(date);
        return TotalTime;
    }
}
