package com.example.foolishfan.IntelligentParking;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.foolishfan.IntelligentParking.Util.ParkingsData;
import com.example.foolishfan.IntelligentParking.Util.ParkingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParkingsDetailsActivity extends AppCompatActivity {
    private ParkingsData[] parkingsDatas = {new ParkingsData("停车场1",R.drawable.apple),new ParkingsData("停车场2",R.drawable.watermelon),new ParkingsData("停车场3",R.drawable.orange),new ParkingsData("停车场4",R.drawable.huolongguo),new ParkingsData("停车场5",R.drawable.lemon),new ParkingsData("停车场6",R.drawable.caomei),new ParkingsData("停车场7",R.drawable.shiliu),new ParkingsData("停车场8",R.drawable.pic_8),new ParkingsData("停车场9",R.drawable.pic_9),new ParkingsData("停车场10",R.drawable.pic_10)};
    private List<ParkingsData> parkingsDataList = new ArrayList<>();//数据列表
    private ParkingAdapter adapter;//数据适配器
    private SwipeRefreshLayout swipeRefresh;//实现下拉刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkings_details);

        initParking();//初始化填充数据
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);//卡片布局
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ParkingAdapter(parkingsDataList);
        recyclerView.setAdapter(adapter);//绑定适配器

        //设置toolbar导航栏，设置导航按钮
        Toolbar park_toolbar = (Toolbar) findViewById(R.id.park_toolbar);
        setSupportActionBar(park_toolbar);
        park_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);//实现下拉刷新
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置进度条颜色
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshParkings();
            }
        });
    }

    //目前实现的是本地刷新，还没有连接数据库进行查询后再刷新
    private void refreshParkings(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);//模拟耗时操作
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initParking();//本地数据刷新
                        adapter.notifyDataSetChanged();//刷新数据列表
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    //初始化数据
    private void initParking() {
        parkingsDataList.clear();
        for (int i = 0; i< parkingsDatas.length; i++) {
            parkingsDataList.add(parkingsDatas[i]);//把parkingsDatas里的所有数据写到list中
        }
    }
}
