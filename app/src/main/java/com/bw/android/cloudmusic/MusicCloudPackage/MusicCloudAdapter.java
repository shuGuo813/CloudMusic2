package com.bw.android.cloudmusic.MusicCloudPackage;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bw.android.cloudmusic.MusicListPakage.MusicListBean;
import com.bw.android.cloudmusic.R;
import com.example.anonymous.greendao.EntityManager;
import com.example.anonymous.greendao.MusicListBeanDao;
import com.facebook.imagepipeline.transcoder.ImageTranscodeResult;

import java.util.ArrayList;
import java.util.List;

public class MusicCloudAdapter extends RecyclerView.Adapter<MusicCloudAdapter.viewHolder> {
    ArrayList<MusicCloudBean> list = new ArrayList<>();
    MusicGetItemClick getItemClick;
    boolean flag = false;
    ArrayList<MusicListBean> musiclist = new ArrayList<>();
    public void setGetItemClick(MusicGetItemClick getItemClick) {
        this.getItemClick = getItemClick;
    }

    public void refresh(ArrayList<MusicCloudBean> list){
        this.list = list;
        Log.v("LSG",list.size() + "MusicCloudAdapter适配器中");
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_cloud_rev_layout, null);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, final int i) {
        viewHolder.tv_music_cloud_Num.setText((i + 1)+ "");
        viewHolder.tv_music_cloud_maker.setText(list.get(i).artist + " - " + list.get(i).album);
        viewHolder.tv_music_cloud_Name.setText(list.get(i).title);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItemClick.getdata(i);
            }
        });
        final MusicListBeanDao beanDao = EntityManager.getInstance().getUserListDao();
        viewHolder.iv_music_cloud_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(viewHolder.itemView.getContext()).inflate(R.layout.cloud_pop_layout, null);
                TextView tv = view.findViewById(R.id.tv_cloud_pop_list);
                //弹出popowindow
                final PopupWindow popupWindow = new PopupWindow(viewHolder.itemView.getContext());
                popupWindow.setHeight(400);
                popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                popupWindow.setContentView(view);
                popupWindow.setOutsideTouchable(true);
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        musiclist = (ArrayList<MusicListBean>) beanDao.loadAll();
                        MusicListBean bean = new MusicListBean(null, MusicCloudAdapter.this.list.get(i).title, MusicCloudAdapter.this.list.get(i).album, MusicCloudAdapter.this.list.get(i).artist,
                                MusicCloudAdapter.this.list.get(i).url, MusicCloudAdapter.this.list.get(i).duration, MusicCloudAdapter.this.list.get(i).size);
                        for(int i = 0; i < musiclist.size(); ++i){
                            if(musiclist.get(i).getTitle().equals(bean.getTitle())){
                                flag = true;
                                break;
                            }else{
                                flag = false;
                            }
                        }
                        if(flag == false){
                            beanDao.insert(bean);
                            Toast.makeText(viewHolder.itemView.getContext(), MusicCloudAdapter.this.list.get(i).title + "添加至播放列表", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(viewHolder.itemView.getContext(), MusicCloudAdapter.this.list.get(i).title + "已存在 添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView tv_music_cloud_Num;
        TextView tv_music_cloud_Name;
        TextView tv_music_cloud_maker;
        ImageView iv_music_cloud_add;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_music_cloud_Name = itemView.findViewById(R.id.tv_music_cloud_Name);
            tv_music_cloud_maker = itemView.findViewById(R.id.tv_music_cloud_maker);
            tv_music_cloud_Num = itemView.findViewById(R.id.tv_music_cloud_Num);
            iv_music_cloud_add = itemView.findViewById(R.id.iv_music_cloud_add);
        }
    }
    public interface MusicGetItemClick{
        void getdata(int index);
    }
}
