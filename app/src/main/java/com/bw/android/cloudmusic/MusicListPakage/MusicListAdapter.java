package com.bw.android.cloudmusic.MusicListPakage;

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
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.viewHolder> {
    ArrayList<MusicListBean> list = new ArrayList<>();
    MusicListGetItemClick getItemClick;

    public void setGetItemClick(MusicListGetItemClick getItemClick) {
        this.getItemClick = getItemClick;
    }

    public void refresh(ArrayList<MusicListBean> list){
        this.list = list;
        notifyDataSetChanged();
        Log.v("LSG",list.size() + "MusicListAdapter适配器中");
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list_rev_layout, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {
        viewHolder.tv_music_list_Num.setText((i + 1)+ "");
        viewHolder.tv_music_list_maker.setText(list.get(i).artist + " - " + list.get(i).album);
        viewHolder.tv_music_list_Name.setText(list.get(i).title);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemClick.getdata(i);
            }
        });
        final MusicListBeanDao beanDao = EntityManager.getInstance().getUserListDao();
        viewHolder.iv_music_list_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MusicListBean> listBeanList = (ArrayList<MusicListBean>) beanDao.loadAll();
                Log.v("LSG","删除前 =" + listBeanList.size());
                MusicListBean bean = beanDao.queryBuilder().where(MusicListBeanDao.Properties.Id.eq(listBeanList.get(i).id)).build().unique();
                if(bean != null){
                    beanDao.deleteByKey(bean.getId());
                    list.remove(i);
                    notifyDataSetChanged();
                }
                ArrayList<MusicListBean> listBeanList2 = (ArrayList<MusicListBean>) beanDao.loadAll();
                Log.v("LSG","删除后 =" + listBeanList2.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView tv_music_list_Num;
        TextView tv_music_list_Name;
        TextView tv_music_list_maker;
        ImageView iv_music_list_delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_music_list_Name = itemView.findViewById(R.id.tv_music_list_Name);
            tv_music_list_maker = itemView.findViewById(R.id.tv_music_list_maker);
            tv_music_list_Num = itemView.findViewById(R.id.tv_music_list_Num);
            iv_music_list_delete = itemView.findViewById(R.id.iv_music_list_delete);
        }
    }
    public interface MusicListGetItemClick{
        void getdata(int index);
    }
}
