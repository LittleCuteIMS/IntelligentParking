package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.ParkingAdapter;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.ParkingsData;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.RecyclerViewClickListener;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParkingsDetailsActivity extends AppCompatActivity {
    private ParkingsData parkingsDatas;
    private List<ParkingsData> parkingsDataList = new ArrayList<>();//数据列表
    private ParkingAdapter adapter;//数据适配器
    private SwipeRefreshLayout swipeRefresh;//实现下拉刷新
    private Bitmap bitmap;

    //主线程创建消息处理器
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    //把传回来的字符串转换成json数组
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        parkingsDatas = new ParkingsData();
                        parkingsDatas.setName(jsonObject.getString("name"));
                        parkingsDatas.setImageId("parkPhoto/"+jsonObject.getString("image"));
                        parkingsDataList.add(parkingsDatas);//添加到要填充的数据列表
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(ParkingsDetailsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkings_details);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);//卡片布局
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ParkingAdapter(parkingsDataList);
        recyclerView.setAdapter(adapter);//绑定适配器

        send();

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

        //调用RecyclerView#addOnItemTouchListener方法能添加一个RecyclerView.OnItemTouchListener对象
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(this,new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ParkingsDetailsActivity.this,DetailedActivity.class);
                intent.putExtra("id",position+1);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {}
        }));
    }

    public void send(){
        String path="parkPHP/parkAddressSelect.php";
        HttpJson http=new HttpJson(path,"",handler);
        new Thread(http.getHttpThread()).start();
    }

    private void refreshParkings(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String path="parkPHP/parkAddressSelect.php";
                        HttpJson http=new HttpJson(path,"",handler);
                        new Thread(http.getHttpThread()).start();//本地数据刷新
                        adapter.notifyDataSetChanged();//刷新数据列表
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
