package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.example.foolishfan.IntelligentParking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaiChuang on 2017-12-6.
 * 广告轮播类
 */

public class CarouselAd {
    private android.content.Context mainContext;
    private ViewPager mViewPaper;
    private List<ImageView> images;
    private List<View> dots;
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;
    //存放图片的id
    private int[] imageIds = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };
    private ViewPagerAdapter adapter;

    public CarouselAd(ViewPager view, List<View> viewList, android.content.Context context){
        mViewPaper=view;
        dots=viewList;
        mainContext=context;
    }

    /**
     * 获得ViewPaper
     * @return
     */
    public ViewPager getmViewPaper(){
        return mViewPaper;
    }

    /**
     * 实现轮播
     */
    public void carouse(){

        //显示的图片
        images = new ArrayList<ImageView>();
        for(int i = 0; i < imageIds.length; i++){
            ImageView imageView = new ImageView(mainContext);
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }
        adapter = new ViewPagerAdapter(images);
        mViewPaper.setAdapter(adapter);
        mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                oldPosition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }
}
