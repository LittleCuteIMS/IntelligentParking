package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.foolishfan.IntelligentParking.R;

/**
 * Created by 蔡创 on 2017-12-13.
 * 计入停车场的计时计费页面
 */

public class ParkInfoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);

        //设置toolbar导航栏，设置导航按钮
        Toolbar park_info_toolbar = (Toolbar) findViewById(R.id.park_info_toolbar);
        setSupportActionBar(park_info_toolbar);
        park_info_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //接受二维码扫描的信息
        Intent intent =getIntent();// 收取 email
        Bundle bundle =intent.getBundleExtra("qr_code_info");// 打开 email
        String parkInfoJson=bundle.getString("parkInfoJson");// 读取内容
        Log.d("json",parkInfoJson);
    }
}
