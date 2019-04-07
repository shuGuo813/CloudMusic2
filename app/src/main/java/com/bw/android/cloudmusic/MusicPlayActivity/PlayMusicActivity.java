package com.bw.android.cloudmusic.MusicPlayActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bw.android.cloudmusic.EventValuesBean.EventMainActivityBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicCloudBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicListBean;
import com.bw.android.cloudmusic.EventValuesBean.EventPlayMusicBean;
import com.bw.android.cloudmusic.MainActivity;
import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;
import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;
import com.bw.android.cloudmusic.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayMusicActivity extends AppCompatActivity implements View.OnClickListener, PopRevAdapter.PlayMusicItemClick {
    Unbinder unbinder;
    @BindView(R.id.iv_pm_back)
    ImageView ivPmBack;
    @BindView(R.id.tv_pm_musicname)
    TextView tvPmMusicname;
    @BindView(R.id.tv_pm_maker)
    TextView tvPmMaker;
    @BindView(R.id.iv_pm_disk)
    ImageView ivPmDisk;
    @BindView(R.id._iv_pm_styli)
    ImageView IvPmStyli;
    @BindView(R.id.iv_pm_circleimg)
    ImageView ivPmCircleimg;
    @BindView(R.id.iv_pm_heart)
    ImageView ivPmHeart;
    @BindView(R.id.iv_pm_download)
    ImageView ivPmDownload;
    @BindView(R.id.iv_pm_smile)
    ImageView ivPmSmile;
    @BindView(R.id.iv_pm_comment)
    ImageView ivPmComment;
    @BindView(R.id.iv_pm_sangd)
    ImageView ivPmSangd;
    @BindView(R.id.tv_pm_curration)
    TextView tvPmCurration;
    @BindView(R.id.sb_pm_musicplan)
    SeekBar sbPmMusicplan;
    @BindView(R.id.tv_pm_duration)
    TextView tvPmDuration;
    @BindView(R.id.iv_pm_controlmusic)
    ImageView ivPmControlmusic;
    @BindView(R.id.iv_pm_previous)
    ImageView ivPmPrevious;
    @BindView(R.id.iv_pm_next)
    ImageView ivPmNext;
    @BindView(R.id.iv_pm_sort)
    ImageView ivPmSort;
    MediaPlayer mediaPlayer;
    boolean isStop;//判断是否停止 默认为false
    int playModel = 1;  //判断播放模式    1随机播放 2列表循环 3单曲循环 0没有设置
    PopupWindow popupWindow;
    ObjectAnimator diskAnimator;        //图片转动的属性动画
    ObjectAnimator styliPlayAnimator;   //播放音乐指针摆动的属性动画
    ObjectAnimator styliPauseAnimator;  //暂停音乐指针摆动的属性动画
    ObjectAnimator styliPausequickAnimator;  //暂停音乐指针摆动的属性动画(快速)
    boolean isPlay = true;
    PopRevAdapter adapter;
    //播放音乐界面的值
    ArrayList<PlayMusicBean> playMusicBeanList = new ArrayList<>();
    int playMusicBeanIndex;
    boolean playMusicFlag;
    EventPlayMusicBean eventPlayMusicBean = new EventPlayMusicBean();
    //音乐库界面的值
    ArrayList<MusicCloudBean> cloudBeanList = new ArrayList<>();
    int cloudBeanIndex;
    boolean musicCloudFlag;
    EventMusicCloudBean eventMusicCloudBean = new EventMusicCloudBean();
    //播放列表界面的值
    ArrayList<MusicListBean> listBeanMusicList = new ArrayList<>();
    int listBeanIndex;
    boolean musicListFlag;
    EventMusicListBean eventMusicListBean = new EventMusicListBean();
    @BindView(R.id.iv_pm_model)
    ImageView ivPmModel;
    int count = 1;
    boolean musicModel = false;
    boolean isPlaying = false;
    @BindView(R.id.iv_head_bg)
    ImageView ivHeadBg;
    MediaPlayer mPlayer = new MediaPlayer();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mediaPlayer = MusicService.getInstanceMedis();
        //毛玻璃
        Glide.with(this).load(R.mipmap.ry)
                .bitmapTransform(new BlurTransformation(this, 60), new CenterCrop(this))
                .into(ivHeadBg);
        initanimator();
        initseekbar();
        initview();
        judgeSongList();
    }

    //初始化动画
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initanimator() {
        //唱片打碟
        diskAnimator = ObjectAnimator.ofFloat(ivPmCircleimg, "rotation", 0f, 360.0f);
        diskAnimator.setDuration(10000);
        diskAnimator.setInterpolator(new LinearInterpolator()); //匀速
        diskAnimator.setRepeatCount(-1);    //设置动画重复次数
        diskAnimator.setRepeatMode(ValueAnimator.RESTART);  //动画重复模式
        //指针拨动 开始播放
        styliPlayAnimator = ObjectAnimator.ofFloat(IvPmStyli, "rotation", -30f, 0.0f);
        styliPlayAnimator.setDuration(600);
        styliPlayAnimator.setRepeatCount(0);
        //指针拨动 暂停播放
        styliPauseAnimator = ObjectAnimator.ofFloat(IvPmStyli, "rotation", 0f, -30.0f);
        styliPauseAnimator.setDuration(600);
        styliPauseAnimator.setRepeatCount(0);
        //指针拨动 暂停播放快速
        //指针拨动 暂停播放
        styliPausequickAnimator = ObjectAnimator.ofFloat(IvPmStyli, "rotation", 0f, -30.0f);
        styliPausequickAnimator.setDuration(0);
        styliPausequickAnimator.setRepeatCount(0);
        if (playMusicFlag || musicListFlag || musicCloudFlag) {
            if (mediaPlayer.isPlaying()) {
                styliPlayAnimator.start();
                diskAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
                tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                tvPmCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));
                sbPmMusicplan.setMax(mediaPlayer.getDuration());
                new Thread(new SeekBarThread()).start();
            } else {
                diskAnimator.pause();
                styliPausequickAnimator.start();
                tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                tvPmCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));
                sbPmMusicplan.setMax(mediaPlayer.getDuration());
                sbPmMusicplan.setProgress(mediaPlayer.getCurrentPosition());
            }
        } else {
            styliPausequickAnimator.start();
            ivPmControlmusic.setImageResource(R.drawable.ic_startmusic);
            tvPmCurration.setText("00.00");
            tvPmDuration.setText("00.00");
        }
        tvPmMaker.setText(getIntent().getStringExtra("maker"));
        tvPmMusicname.setText(getIntent().getStringExtra("name"));
    }

    //判断歌单
    private void judgeSongList() {
        if (playMusicFlag) {
            cloudBeanList = new ArrayList<>();
            musicCloudFlag = false;
            listBeanMusicList = new ArrayList<>();
            musicListFlag = false;
        } else if (musicListFlag) {
            cloudBeanList = new ArrayList<>();
            musicCloudFlag = false;
            playMusicBeanList = new ArrayList<>();
            playMusicFlag = false;
        } else if (musicCloudFlag) {
            playMusicBeanList = new ArrayList<>();
            playMusicFlag = false;
            listBeanMusicList = new ArrayList<>();
            musicListFlag = false;
        } else {
            tvPmMusicname.setText("网易云音乐");
            tvPmMaker.setText("");
        }
        isMusicStop();
    }

    //Eventbus传值
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void OnPlayMusicEvent(EventPlayMusicBean playMusicBean) {
        eventPlayMusicBean = playMusicBean;
        //播放音乐界面的值
        playMusicBeanList = eventPlayMusicBean.getList();
        playMusicBeanIndex = eventPlayMusicBean.getIndex();
        playMusicFlag = eventPlayMusicBean.isPlayMusicFlag();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void OnMusicCloudEvent(EventMusicCloudBean cloudBean) {
        eventMusicCloudBean = cloudBean;
        //音乐库界面的值
        cloudBeanList = eventMusicCloudBean.getList();
        cloudBeanIndex = eventMusicCloudBean.getIndex();
        musicCloudFlag = eventMusicCloudBean.isMusicCloudFlag();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void OnMusicListEvent(EventMusicListBean listBean) {
        //播放列表界面的值
        eventMusicListBean = listBean;
        listBeanMusicList = eventMusicListBean.getMusicList();
        listBeanIndex = eventMusicListBean.getIndex();
        musicListFlag = eventMusicListBean.isMusicListFlag();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mediaPlayer != null) {
            mediaPlayer = null;
        }
    }

    //初始化seekbar
    private void initseekbar() {
        sbPmMusicplan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Log.e("LSG", "用户拖动改变seekBar的值");
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

    //初始化控件
    private void initview() {
        //中心专辑图片的加载
        Glide.with(this).load(R.mipmap.ry).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivPmCircleimg) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(PlayMusicActivity.this.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivPmCircleimg.setImageDrawable(circularBitmapDrawable);
            }
        });
        ivPmBack.setOnClickListener(this);
        ivPmHeart.setOnClickListener(this);
        ivPmPrevious.setOnClickListener(this);
        ivPmNext.setOnClickListener(this);
        ivPmControlmusic.setOnClickListener(this);
        ivPmSort.setOnClickListener(this);
        ivPmModel.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_pm_back) {
            //back键返回
            back();
        } else if (v.getId() == R.id.iv_pm_heart) {
            //添加至我喜欢
        } else if (v.getId() == R.id.iv_pm_controlmusic) {
            controlMusic();
        } else if (v.getId() == R.id.iv_pm_previous) {
            //上一曲
            previous();
        } else if (v.getId() == R.id.iv_pm_next) {
            //下一曲
            next();
        } else if (v.getId() == R.id.iv_pm_sort) {
            //展示popWindow
            showPopwindow();
        } else if (v.getId() == R.id.iv_pm_model) {
            //设置播放模式
            setplayModel();
        }
    }

    //Handler接收Message并且改变进度条以及图标
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sbPmMusicplan.setProgress(msg.what);

            if (mediaPlayer.isPlaying()) {
                tvPmCurration.setText(formatTime(msg.what));
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
            } else {
                ivPmControlmusic.setImageResource(R.drawable.ic_startmusic);
            }
        }
    };

    //获取歌曲时长
    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String TotalTime = simpleDateFormat.format(date);
        return TotalTime;
    }

    @Override
    public void getdata(int index) {
        //flag为1代表playmusic 2代表musiclist 3代表musiccloud
        if (adapter.flag == 1) {
            mediaPlayer.reset();
            popupWindow.dismiss();
            try {
                mediaPlayer.setDataSource(playMusicBeanList.get(index).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                sbPmMusicplan.setMax(mediaPlayer.getDuration());
                new Thread(new SeekBarThread()).start();
                tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                tvPmMaker.setText(playMusicBeanList.get(index).getArtist());
                tvPmMusicname.setText(playMusicBeanList.get(index).getTitle());
                styliPlayAnimator.start();
                diskAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (adapter.flag == 2) {
            mediaPlayer.reset();
            popupWindow.dismiss();
            try {
                mediaPlayer.setDataSource(listBeanMusicList.get(index).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                sbPmMusicplan.setMax(mediaPlayer.getDuration());
                new Thread(new SeekBarThread()).start();
                tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                tvPmMaker.setText(listBeanMusicList.get(index).getArtist());
                tvPmMusicname.setText(listBeanMusicList.get(index).getTitle());
                styliPlayAnimator.start();
                diskAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (adapter.flag == 3) {
            mediaPlayer.reset();
            popupWindow.dismiss();
            try {
                mediaPlayer.setDataSource(cloudBeanList.get(index).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                sbPmMusicplan.setMax(mediaPlayer.getDuration());
                new Thread(new SeekBarThread()).start();
                tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                tvPmMaker.setText(cloudBeanList.get(index).getArtist());
                tvPmMusicname.setText(cloudBeanList.get(index).getTitle());
                styliPlayAnimator.start();
                diskAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //更新进度条的线程
    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer != null && isStop == false) {
                //将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                try {
                    //每100毫秒更新一次位置
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //返回
    public void back() {
        EventMainActivityBean bean = new EventMainActivityBean();
        bean.setMusicMakerName(tvPmMaker.getText().toString());
        bean.setMusicName(tvPmMusicname.getText().toString());
        bean.setFlag(true);
        EventBus.getDefault().postSticky(bean);
        Intent intent = new Intent(PlayMusicActivity.this, MainActivity.class);
        intent.putExtra("flag","back");
        startActivity(intent);
    }

    //控制音乐播放
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void controlMusic() {
        if (playMusicFlag || musicListFlag || musicCloudFlag) {
            //点了控制播放的按钮
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                diskAnimator.pause();
                styliPauseAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_startmusic);
            } else {
                mediaPlayer.start();
                new Thread(new SeekBarThread()).start();
                diskAnimator.resume();
                styliPlayAnimator.start();
                ivPmControlmusic.setImageResource(R.drawable.ic_pausemusic);
                tvPmCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));

            }
        }
    }
    //上一曲
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void previous() {
        isPlaying = true;
        if (playMusicFlag || musicListFlag || musicCloudFlag) {
            if (playMusicFlag) {
                playMusicBeanIndex--;
                if (playMusicBeanIndex == -1) {
                    playMusicBeanIndex = playMusicBeanList.size() - 1;
                }
                try {
                    mediaPlayer.reset();
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(playMusicBeanList.get(playMusicBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(playMusicBeanList.get(playMusicBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(playMusicBeanList.get(playMusicBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(playMusicBeanList.get(playMusicBeanIndex).getTitle());
                    tvPmMaker.setText(playMusicBeanList.get(playMusicBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (musicListFlag) {
                listBeanIndex--;
                if (listBeanIndex == -1) {
                    listBeanIndex = listBeanMusicList.size() - 1;
                }
                try {
                    mediaPlayer.reset();
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(listBeanMusicList.get(listBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(listBeanMusicList.get(listBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(listBeanMusicList.get(listBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(listBeanMusicList.get(listBeanIndex).getTitle());
                    tvPmMaker.setText(listBeanMusicList.get(listBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (musicCloudFlag) {
                cloudBeanIndex--;
                if (cloudBeanIndex == -1) {
                    cloudBeanIndex = cloudBeanList.size() - 1;
                }
                try {
                    mediaPlayer.reset();
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(cloudBeanList.get(cloudBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(cloudBeanList.get(cloudBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(cloudBeanList.get(cloudBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(cloudBeanList.get(cloudBeanIndex).getTitle());
                    tvPmMaker.setText(cloudBeanList.get(cloudBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //下一曲
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void next() {
        isPlaying = true;
        if (playMusicFlag || musicListFlag || musicCloudFlag) {
            if (playMusicFlag) {
                playMusicBeanIndex++;
                if (playMusicBeanIndex == playMusicBeanList.size()) {
                    playMusicBeanIndex = 0;
                }
                mediaPlayer.reset();
                try {
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(playMusicBeanList.get(playMusicBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(playMusicBeanList.get(playMusicBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(playMusicBeanList.get(playMusicBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(playMusicBeanList.get(playMusicBeanIndex).getTitle());
                    tvPmMaker.setText(playMusicBeanList.get(playMusicBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (musicCloudFlag) {
                cloudBeanIndex++;
                if (cloudBeanIndex == cloudBeanList.size()) {
                    cloudBeanIndex = 0;
                }
                try {
                    mediaPlayer.reset();
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(cloudBeanList.get(cloudBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(cloudBeanList.get(cloudBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(cloudBeanList.get(cloudBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(cloudBeanList.get(cloudBeanIndex).getTitle());
                    tvPmMaker.setText(cloudBeanList.get(cloudBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (musicListFlag) {
                listBeanIndex++;
                if (listBeanIndex == listBeanMusicList.size()) {
                    listBeanIndex = 0;
                }
                try {
                    mediaPlayer.reset();
                    EventMainActivityBean mainActivityBean = new EventMainActivityBean();
                    mainActivityBean.setMusicName(listBeanMusicList.get(listBeanIndex).getTitle());
                    mainActivityBean.setMusicMakerName(listBeanMusicList.get(listBeanIndex).getArtist());
                    EventBus.getDefault().postSticky(mainActivityBean);
                    mediaPlayer.setDataSource(listBeanMusicList.get(listBeanIndex).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    diskAnimator.start();
                    styliPlayAnimator.start();
                    sbPmMusicplan.setMax(mediaPlayer.getDuration());
                    tvPmDuration.setText(formatTime(mediaPlayer.getDuration()));
                    tvPmMusicname.setText(listBeanMusicList.get(listBeanIndex).getTitle());
                    tvPmMaker.setText(listBeanMusicList.get(listBeanIndex).getArtist());
                    new Thread(new SeekBarThread()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //弹出popwindow
    public void showPopwindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.playmusic_pop_layout, null);
        //弹出popowindow
        popupWindow = new PopupWindow(this);
        popupWindow.setHeight(800);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
        RecyclerView popRev = view.findViewById(R.id.poprev);
        popRev.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        popRev.setLayoutManager(manager);
        popRev.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new PopRevAdapter();
        if (playMusicFlag) {
            adapter.refresh(new ArrayList<MusicListBean>(), 1, new ArrayList<MusicCloudBean>(), playMusicBeanList);
        } else if (musicCloudFlag) {
            adapter.refresh(new ArrayList<MusicListBean>(), 3, cloudBeanList, new ArrayList<PlayMusicBean>());
        } else if (musicListFlag) {
            adapter.refresh(listBeanMusicList, 2, new ArrayList<MusicCloudBean>(), new ArrayList<PlayMusicBean>());
        } else {
            adapter.refresh(new ArrayList<MusicListBean>(), 0, new ArrayList<MusicCloudBean>(), new ArrayList<PlayMusicBean>());
        }
        adapter.setGetItemClick(this);
        popRev.setAdapter(adapter);
    }

    //判断播放模式
    public void setplayModel() {
        count++;
        //判断播放模式    1随机播放 2列表循环 3单曲循环 0没有设置
        if (count == 1) {
            playModel = 1;
            ivPmModel.setImageResource(R.mipmap.sjbf);
            Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
        } else if (count == 2) {
            playModel = 2;
            ivPmModel.setImageResource(R.mipmap.xh);
            Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
        } else if (count == 3) {
            playModel = 3;
            ivPmModel.setImageResource(R.mipmap.dqxh);
            Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
        }
        if (count >= 3) {
            count = 0;
        }
    }

    //getDuration   获取文件的持续时间
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    //seekTo 寻找指定的时间位置
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    //获取当前的进度值
    public int getCurrentProgress() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //mediaPlayer不为空 且为播放状态
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    //获取当前是否为播放状态 提供给MyMusicListFragment的播放暂停按钮点击事件判断状态时调用
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void isMusicStop() {
        //判断歌曲有没有结束
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("LSG", "判定成功5 歌曲结束");
                Log.e("LSG", "歌曲结束 playmodel =" + playModel);
                switch (playModel){
                    case 1:

                        break;
                    case 2:
                        //列表循环
                    case 3:
                        //单曲循环
                        break;
                    default:
                        break;
                }

//                ivPmControlmusic.setImageResource(R.drawable.ic_startmusic);
//                sbPmMusicplan.setProgress(0);
//                mediaPlayer.seekTo(0);
//                if (diskAnimator.isRunning()) {
//                    diskAnimator.end();
//                }
//                styliPauseAnimator.start();
//                styliPlayAnimator.cancel();
//                tvPmDuration.setText("00.00");
//                tvPmCurration.setText("00.00");
            }
        });
    }

    //默认开始播放的方法
    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            //判断当前歌曲
            // 空并且没有在播放的音乐
            mediaPlayer.start();
        }
    }

    /**
     * //播放音乐界面的值
     * ArrayList<PlayMusicBean> playMusicBeanList = new ArrayList<>();
     * <p>
     * //音乐库界面的值
     * ArrayList<MusicCloudBean> cloudBeanList = new ArrayList<>();
     * <p>
     * //播放列表界面的值
     * ArrayList<MusicListBean> listBeanMusicList = new ArrayList<>();
     *
     * @param position
     */
    //播放
    public void playMusic(int position) {
        if (position >= 0 && position < playMusicBeanList.size()) {
            PlayMusicBean playMusicBean = playMusicBeanList.get(position);
            //进行播放 播放前判断
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(playMusicBean.getUrl()));
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

