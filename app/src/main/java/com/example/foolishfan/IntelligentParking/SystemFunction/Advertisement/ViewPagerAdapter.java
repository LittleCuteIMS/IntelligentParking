package com.example.foolishfan.IntelligentParking.SystemFunction.Advertisement;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.Listeners.AdvertisementListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaiChuang on 2017-12-6.
 * 轮询广告的适配器
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<ImageView> images;
    private int[] imageIds;
    private String[] urls;
    private AdvertisementListener advertisementListener;

    public ViewPagerAdapter(Context context){
        imageIds = new int[]{
                R.drawable.a,
                R.drawable.b,
                R.drawable.c,
                R.drawable.d,
                R.drawable.e
        };
        urls = new String[]{
                "https://www.baidu.com/",
                "http://www.zhihu.com/",
                "https://www.douban.com/",
                "https://www.bing.com/?mkt=zh-CN",
                "https://www.taobao.com/"
        };
        images = new ArrayList<ImageView>();
        for (int i = 0; i < imageIds.length; i++) {
            advertisementListener=new AdvertisementListener(context,urls[i]);
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(imageIds[i]);
            imageView.setOnClickListener(advertisementListener);
            images.add(imageView);
        }
    }

    public int getImageIdsLength(){
        return imageIds.length;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(images.get(position));
        return images.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView(images.get(position));
    }
}
