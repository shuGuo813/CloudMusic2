package com.bw.android.cloudmusic.PlayMusicPackage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.android.cloudmusic.MusicCloudPackage.MusicCloudBean;
import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;
import com.example.anonymous.greendao.PlayMusicBeanDao;

import java.util.ArrayList;

public class PlayMusicAdapter extends RecyclerView.Adapter<PlayMusicAdapter.viewHolder> {
    ArrayList<PlayMusicBean> list = new ArrayList<>();
    PlayMusicGetItemClick getItemClick;
    boolean flag = false;

    public void setGetItemClick(PlayMusicGetItemClick getItemClick) {
        this.getItemClick = getItemClick;
    }

    public void refresh(ArrayList<PlayMusicBean> list){
        this.list = list;
        Log.v("LSG",list.size() + "PlayMusicAdapter适配器中");
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_play_rev_layout, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {
        viewHolder.tv_music_play_Num.setText((i + 1)+ "");
        viewHolder.tv_music_play_maker.setText(list.get(i).artist + " - " + list.get(i).album);
        viewHolder.tv_music_play_Name.setText(list.get(i).title);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemClick.getdata(list.size() - (i + 1));
            }
        });
        final PlayMusicBeanDao beanDao = EntityManager.getInstance().getPlayMusicDao();
        viewHolder.iv_music_play_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PlayMusicBean> listBeanList = (ArrayList<PlayMusicBean>) beanDao.loadAll();
                PlayMusicBean bean = beanDao.queryBuilder().where(PlayMusicBeanDao.Properties.Id.eq(listBeanList.get(i).id)).build().unique();
                if(bean != null){
                    beanDao.deleteByKey(bean.getId());
                    list.remove(i);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView tv_music_play_Num;
        TextView tv_music_play_Name;
        TextView tv_music_play_maker;
        ImageView iv_music_play_delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_music_play_Name = itemView.findViewById(R.id.tv_music_play_Name);
            tv_music_play_maker = itemView.findViewById(R.id.tv_music_play_maker);
            tv_music_play_Num = itemView.findViewById(R.id.tv_music_play_Num);
            iv_music_play_delete = itemView.findViewById(R.id.iv_music_play_delete);
        }
    }
    public interface PlayMusicGetItemClick{
        void getdata(int index);
    }
}
