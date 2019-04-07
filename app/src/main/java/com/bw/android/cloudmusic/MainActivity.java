package com.bw.android.cloudmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bw.android.cloudmusic.EventValuesBean.EventMainActivityBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicCloudBean;
import com.bw.android.cloudmusic.EventValuesBean.EventMusicListBean;
import com.bw.android.cloudmusic.EventValuesBean.EventPlayMusicBean;
import com.bw.android.cloudmusic.EventValuesBean.EventSendMainActivityBean;
import com.bw.android.cloudmusic.MinePackage.MineFragment;
import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudFragment;
import com.bw.android.cloudmusic.MusicListPakage.MusicListFragment;
import com.bw.android.cloudmusic.MusicPlayActivity.PlayMusicActivity;
import com.bw.android.cloudmusic.MusicServicePackage.MusicService;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.sb_musicPlan)
    SeekBar sbMusicPlan;
    @BindView(R.id.iv_musicImg)
    ImageView ivMusicImg;
    @BindView(R.id.tv_musicName)
    TextView tvMusicName;
    @BindView(R.id.tv_musicMakerName)
    TextView tvMusicMakerName;
    @BindView(R.id.iv_controlMusic)
    ImageView ivControlMusic;
    @BindView(R.id.nav_menu)
    NavigationView navMenu;
    @BindView(R.id.drawlayout)
    DrawerLayout drawlayout;
    @BindView(R.id.rl_foot)
    RelativeLayout rlFoot;
    MediaPlayer mediaPlayer;
    @BindView(R.id.tv_curration)
    TextView tvCurration;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    boolean isStop;
    String musicName;
    String musicMakerName;
    boolean flag;
    int numFlag = 0;
    String backIntent = "1";
    EventPlayMusicBean eventPlayMusicBean = MusicService.getInstancePlayMusic();
    EventMusicListBean eventMusicListBean = MusicService.getInstanceMusicList();
    EventMusicCloudBean eventMusicCloudBean = MusicService.getInstanceMusicCloud();
    int nameFlag = 1;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent().getStringExtra("flag") == null){

        }else{
            backIntent = getIntent().getStringExtra("flag");
        }

        EventBus.getDefault().register(this);
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        unbinder = ButterKnife.bind(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout, new PlayMusicFragment());
        mediaPlayer = MusicService.getInstanceMedis();
        transaction.commit();
        initview();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initNotifyAction() {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notifyaction);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setCustomBigContentView(remoteViews);
            builder.setSmallIcon(R.mipmap.fhzm);//设置图标
            builder.setWhen(System.currentTimeMillis());//设置模拟器上通知的时间
            builder.setShowWhen(true);//同时设置时间
            builder.setAutoCancel(true);//点击后就消失
            builder.setDefaults(Notification.DEFAULT_SOUND);//设置当前的时间
            builder.setPriority(Notification.PRIORITY_DEFAULT);//设置通知的优先级
            Notification notification = builder.build();
            if(nameFlag == 1){
                remoteViews.setImageViewResource(R.id.iv_nc_control,R.drawable.ic_pausemusic);
            }else if(nameFlag == 2){
                remoteViews.setImageViewResource(R.id.iv_nc_control,R.drawable.ic_startmusic);
            }

        remoteViews.setTextViewText(R.id.tv_nc_musicname,musicName);
        remoteViews.setTextViewText(R.id.tv_nc_maker,musicMakerName);

        //控制音乐
        Intent controlIntent = new Intent(this,MyReceiver.class);
        controlIntent.setAction("com.cloud.controlMusic");
        PendingIntent controlPendingIntent = PendingIntent.getBroadcast(this,0,controlIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.iv_nc_control,controlPendingIntent);
        //上一曲
        Intent previousIntent = new Intent(this,MyReceiver.class);
        previousIntent.setAction("com.cloud.previousMusic");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this,0,previousIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.iv_nc_previous,previousPendingIntent);
        //下一曲
        Intent nextIntent = new Intent(this,MyReceiver.class);
        nextIntent.setAction("com.cloud.nextMusic");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.iv_nc_next,nextPendingIntent);
        notification.bigContentView = remoteViews;
            manager.notify(1,notification);
        }

    //Eventbus传值
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void OnPlayMusicEvent(EventMainActivityBean bean){
        musicName = bean.getMusicName();
        musicMakerName = bean.getMusicMakerName();
        flag = bean.isFlag();
        new Thread(new SeekBarThread()).start();
        Log.e("LSG","backIntent =" + backIntent);
        if("1".equals(backIntent)){
            Log.e("LSG","come in");
            initNotifyAction();
        }else{
            Log.e("LSG","can t");
        }

    }

    //Eventbus传值
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void OnReceivcverEvent(EventSendMainActivityBean bean){
        numFlag = bean.getFlag();
    }
    private void initview() {
        ivBack.setOnClickListener(this);
        ivControlMusic.setOnClickListener(this);
        sbMusicPlan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
        rlFoot.setOnClickListener(this);
        View headview = navMenu.getHeaderView(0);
        final ImageView sdv = headview.findViewById(R.id.sdv_showimg);
        sdv.setImageResource(R.mipmap.nstx);
        Glide.with(this).load(R.mipmap.cq).asBitmap().centerCrop().into(new BitmapImageViewTarget(sdv) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                sdv.setImageDrawable(circularBitmapDrawable);
            }
        });
        //开启手势滑动打开侧滑菜单栏，如果要关闭手势滑动，将后面的UNLOCKED替换成LOCKED_CLOSED 即可
        drawlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.menu_mine:
                        transaction.replace(R.id.framelayout, new MineFragment());
                        drawlayout.closeDrawers();
                        break;
                    case R.id.menu_musicbox:
                        transaction.replace(R.id.framelayout, new MusicCloudFragment());
                        drawlayout.closeDrawers();
                        break;
                    case R.id.menu_playmusic:
                        transaction.replace(R.id.framelayout, new PlayMusicFragment());
                        drawlayout.closeDrawers();
                        break;
                    case R.id.menu_playmusiclist:
                        transaction.replace(R.id.framelayout, new MusicListFragment());
                        drawlayout.closeDrawers();
                        break;
                }
                transaction.commit();
                return true;
            }
        });
        Log.e("LSG","oncreate");
        if(flag){
            tvDuration.setText(formatTime(mediaPlayer.getDuration()));
            tvCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));
            tvMusicName.setText(musicName);
            tvMusicMakerName.setText(musicMakerName);
            sbMusicPlan.setMax(mediaPlayer.getDuration());
            sbMusicPlan.setProgress(mediaPlayer.getCurrentPosition());
            new Thread(new SeekBarThread()).start();
        }else{
//            tvDuration.setText(formatTime(mediaPlayer.getDuration()));
//            tvCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            //点击了上方的菜单按钮 弹出菜单
            if (drawlayout.isDrawerOpen(navMenu)) {
                drawlayout.closeDrawers();
            }
            drawlayout.openDrawer(navMenu);
        } else if (v.getId() == R.id.iv_controlMusic) {
            //点击了下方的暂停按钮 控制音乐播放
            if(mediaPlayer.isPlaying()){
                nameFlag = 2;
                mediaPlayer.pause();
                initNotifyAction();
            }else{
                nameFlag = 1;
                mediaPlayer.start();
                initNotifyAction();
            }
        } else if (v.getId() == R.id.rl_foot) {
            if(eventMusicCloudBean.isMusicCloudFlag() || eventMusicListBean.isMusicListFlag() || eventPlayMusicBean.isPlayMusicFlag()){
                ivControlMusic.setClickable(true);
                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                intent.putExtra("name",tvMusicName.getText().toString());
                intent.putExtra("maker",tvMusicMakerName.getText().toString());
                startActivity(intent);
            }else{
                Toast.makeText(this, "请选择音乐", Toast.LENGTH_SHORT).show();
                ivControlMusic.setClickable(false);
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MusicService.getInstanceMedis().isPlaying()) {
                sbMusicPlan.setProgress(mediaPlayer.getCurrentPosition());
                tvCurration.setText(formatTime(mediaPlayer.getCurrentPosition()));
                ivControlMusic.setImageResource(R.drawable.ic_pausemusic);
            } else {
                ivControlMusic.setImageResource(R.drawable.ic_startmusic);
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
}
