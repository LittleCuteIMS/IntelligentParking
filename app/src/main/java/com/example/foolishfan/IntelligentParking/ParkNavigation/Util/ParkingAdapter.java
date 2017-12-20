package com.example.foolishfan.IntelligentParking.ParkNavigation.Util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foolishfan.IntelligentParking.R;

import java.util.List;

/**
 * Created by zhanglin on 2017/12/3.
 */

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {
    private Context mContext;
    private List<ParkingsData> mParkingsDataList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView ParkingImage;
        TextView ParkingName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            ParkingImage = (ImageView) view.findViewById(R.id.parking_image);
            ParkingName = (TextView) view.findViewById(R.id.parking_name);
        }
    }

    public ParkingAdapter(List<ParkingsData> parkingsDataList) {
        mParkingsDataList = parkingsDataList;
    }

    /*
    * 创建ViewHolder，在RecyclerView需要展示一个item的时候回调，重写该方法时，应该使ViewHolder加载item view的布局
    * 这个方法避免了不必要的findViewById操作，提高了性能
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.parking_item,parent,false);
        return new ViewHolder(view);
    }

    /*
    * 在RecyclerView在特定位置展示数据时候回调，把数据绑定、填充到相应的item view中
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParkingsData parkingsData = mParkingsDataList.get(position);
        holder.ParkingName.setText(parkingsData.getName());
        //Glide.with(mContext).load("http://120.78.173.73/ParkingWeb/"+parkingsData.getImageId()).into(holder.ParkingImage);
        Glide.with(mContext).load("http://192.168.155.1/ParkingWeb/"+parkingsData.getImageId()).into(holder.ParkingImage);
    }

    @Override
    public int getItemCount() {
        return mParkingsDataList.size();
    }
}
