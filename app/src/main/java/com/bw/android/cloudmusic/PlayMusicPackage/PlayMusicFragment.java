package com.bw.android.cloudmusic.PlayMusicPackage;


import android.content.Context;
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
import com.bw.android.cloudmusic.EventValuesBean.EventPlayMusicBean;
import com.bw.android.cloudmusic.EventValuesBean.EventSendMainActivityBean;
import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;
import com.bw.android.cloudmusic.MusicListPakage.MusicListAdapter;
import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;
import com.example.anonymous.greendao.PlayMusicBeanDao;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.sql.Types.NULL;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayMusicFragment extends Fragment implements PlayMusicAdapter.PlayMusicGetItemClick {

    Unbinder unbinder;
    @BindView(R.id.iv_showimg)
    Banner ivShowimg;
    @BindView(R.id.rv_latelyMusic)
    RecyclerView rvLatelyMusic;
    ArrayList<Integer> imgList;
    ArrayList<String> nameList;
    ArrayList<PlayMusicBean> list = new ArrayList<>();
    boolean flag = false;
    ImageView ivmusicImg;
    ImageView ivcontrolMusic;
    TextView tvmusicName;
    TextView  tvmusicMakerName;
    MediaPlayer mediaPlayer;
    SeekBar sbmusicplan;
    boolean isStop;     //停止
    TextView tvduration;
    TextView tvcurration;
    public PlayMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_music, container, false);
        unbinder = ButterKnife.bind(this, view);
        initMediaPlayer();
        initSeekBar();
        initData();
        initView();
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("LSG","Fragment 歌曲结束");
            }
        });
    }

    private void initData() {
        imgList = new ArrayList<>();
        nameList = new ArrayList<>();
        imgList.add(R.mipmap.zgr);
        imgList.add(R.mipmap.zgr2);
        imgList.add(R.mipmap.zgr3);
        imgList.add(R.mipmap.fhzm);
        imgList.add(R.mipmap.fhzm2);
        imgList.add(R.mipmap.fhzm3);
        for(int i = 0; i < imgList.size(); ++i){
            nameList.add("第" + (i + 1) + "张图片");
        }
    }

    private void initView() {
        ivmusicImg = getActivity().findViewById(R.id.iv_musicImg);
        ivcontrolMusic = getActivity().findViewById(R.id.iv_controlMusic);
        ivcontrolMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    ivcontrolMusic.setImageResource(R.drawable.ic_startmusic);
                }else {
                    mediaPlayer.start();
                    ivcontrolMusic.setImageResource(R.drawable.ic_pausemusic);
                }
            }
        });
        tvmusicName = getActivity().findViewById(R.id.tv_musicName);
        tvmusicMakerName = getActivity().findViewById(R.id.tv_musicMakerName);
        tvduration = getActivity().findViewById(R.id.tv_duration);
        tvcurration = getActivity().findViewById(R.id.tv_curration);
        //初始化Banner控件
        ivShowimg.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        ivShowimg.setImageLoader(new MyLoader());
        ivShowimg.setImages(imgList);
        ivShowimg.setBannerAnimation(Transformer.DepthPage);
        ivShowimg.setBannerTitles(nameList);
        ivShowimg.isAutoPlay(true);
        ivShowimg.setDelayTime(1500);
        ivShowimg.setIndicatorGravity(BannerConfig.CENTER);
        ivShowimg.start();
        //初始化RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLatelyMusic.setLayoutManager(manager);
        rvLatelyMusic.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rvLatelyMusic.setItemAnimator(new DefaultItemAnimator());
        PlayMusicBeanDao beanDao = EntityManager.getInstance().getPlayMusicDao();
        list = (ArrayList<PlayMusicBean>) beanDao.loadAll();
        PlayMusicAdapter adapter = new PlayMusicAdapter();
        Log.v("LSG","PlayMusicFragment =" + list.size());
        ArrayList<PlayMusicBean> playMusicBeanArrayList = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0; --i){
            playMusicBeanArrayList.add(list.get(i));
        }
        adapter.refresh(playMusicBeanArrayList);
        adapter.setGetItemClick(this);
        rvLatelyMusic.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStop = true;
    }

    @Override
    public void getdata(final int index) {
        EventMainActivityBean mainActivityBean = new EventMainActivityBean();
        mainActivityBean.setMusicName(list.get(index).title);
        mainActivityBean.setMusicMakerName(list.get(index).artist);
        EventBus.getDefault().postSticky(mainActivityBean);
        EventPlayMusicBean bean = MusicService.getInstancePlayMusic();
        bean.setIndex(index);
        bean.setList(list);
        bean.setPlayMusicFlag(true);
        MusicService.getInstanceMusicCloud().setMusicCloudFlag(false);
        MusicService.getInstanceMusicCloud().setList(new ArrayList<MusicCloudBean>());
        MusicService.getInstanceMusicList().setMusicList(new ArrayList<MusicListBean>());
        MusicService.getInstanceMusicList().setMusicListFlag(false);
        EventBus.getDefault().postSticky(bean);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(list.get(index).url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ivcontrolMusic.setImageResource(R.drawable.ic_pausemusic);
            //开启线程
            sbmusicplan.setMax(mediaPlayer.getDuration());
            //设置当前时间
            tvduration.setText(formatTime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新最下方的控件
        tvmusicName.setText(list.get(index).title);
        tvmusicMakerName.setText(list.get(index).artist);
    }
    //图片加载器
    public class MyLoader extends ImageLoader{

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Picasso.with(context).load((Integer) path).into(imageView);
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
