package com.bw.android.cloudmusic.MusicPlayActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;
import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.PlayMusicPackage.PlayMusicBean;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PopRevAdapter extends RecyclerView.Adapter<PopRevAdapter.viewHolder> {
    ArrayList<MusicListBean> musicListBeanArrayList = new ArrayList<>();
    ArrayList<MusicCloudBean> musicCloudBeanArrayList = new ArrayList<>();
    ArrayList<PlayMusicBean> playMusicBeanArrayList = new ArrayList<>();
    PlayMusicItemClick getItemClick;
    int flag = 0;
    //flag为1代表playmusic 2代表musiclist 3代表musiccloud
    public void setGetItemClick(PlayMusicItemClick getItemClick) {
        this.getItemClick = getItemClick;
    }

    public void refresh(ArrayList<MusicListBean> musicListBeanArrayList,int flag,
                        ArrayList<MusicCloudBean> musicCloudBeanArrayList,
                        ArrayList<PlayMusicBean> playMusicBeanArrayList){
        this.musicListBeanArrayList = musicListBeanArrayList;
        this.musicCloudBeanArrayList = musicCloudBeanArrayList;
        this.playMusicBeanArrayList = playMusicBeanArrayList;
        this.flag = flag;
        notifyDataSetChanged();
        Log.v("LSG",playMusicBeanArrayList.size() + "PopRevAdapter适配器中 playmusiclist");
        Log.v("LSG",musicListBeanArrayList.size() + "PopRevAdapter适配器中 musicListBeanArrayList");
        Log.v("LSG",musicCloudBeanArrayList.size() + "PopRevAdapter适配器中 musicCloudBeanArrayList");
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pop_rev_layout, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {
        if(flag == 0){

        }else if(flag == 1){
            //playmusic
            viewHolder.tv_pop_musicName.setText(playMusicBeanArrayList.get(i).getTitle());
            viewHolder.tv_pop_musicMakerName.setText(playMusicBeanArrayList.get(i).getArtist());
        }else if(flag == 2){
            //musiclist
            viewHolder.tv_pop_musicName.setText(musicListBeanArrayList.get(i).getTitle());
            viewHolder.tv_pop_musicMakerName.setText(musicListBeanArrayList.get(i).getArtist());
        }else if(flag == 3){
            //musiccloud
            viewHolder.tv_pop_musicName.setText(musicCloudBeanArrayList.get(i).getTitle());
            viewHolder.tv_pop_musicMakerName.setText(musicCloudBeanArrayList.get(i).getArtist());
        }
        viewHolder.rl_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemClick.getdata(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(flag == 0){
            return 0;
        }else if(flag == 1){
            //playmusic
            return playMusicBeanArrayList.size();
        }else if(flag == 2){
            //musiclist
            return musicListBeanArrayList.size();
        }else if(flag == 3){
            //musiccloud
            return musicCloudBeanArrayList.size();
        }
        return 0;
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView tv_pop_musicName;
        TextView tv_pop_musicMakerName;
        RelativeLayout rl_pop;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_pop_musicMakerName = itemView.findViewById(R.id.tv_pop_musicMakerName);
            tv_pop_musicName = itemView.findViewById(R.id.tv_pop_musicName);
            rl_pop = itemView.findViewById(R.id.rl_pop);
        }
    }
    public interface PlayMusicItemClick{
        void getdata(int index);
    }
}
