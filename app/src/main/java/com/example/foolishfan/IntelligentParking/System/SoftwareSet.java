package com.example.foolishfan.IntelligentParking.System;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.MainActivity;
import com.example.foolishfan.IntelligentParking.R;

/**
 * Created by Administrator on 2017/11/6 0006.
 */

public class SoftwareSet extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.softwareset);

        //设置toolbar导航栏，设置导航按钮
        Toolbar softwareset_toolbar = (Toolbar) findViewById(R.id.softwareset_toolbar);
        setSupportActionBar(softwareset_toolbar);
        softwareset_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获取按钮
        Button wipeCache=(Button)findViewById(R.id.wipeCache);
        Button checkUpdate=(Button)findViewById(R.id.checkUpdate);
        Button aboutUs=(Button)findViewById(R.id.aboutUs);
        Button logout=(Button)findViewById(R.id.logout);

        //设置监听事件
        wipeCache.setOnClickListener(setListener);
        checkUpdate.setOnClickListener(setListener);
        aboutUs.setOnClickListener(setListener);
        logout.setOnClickListener(setListener);
    }

    //设置的监听事件
    View.OnClickListener setListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.wipeCache:
                    break;
                case R.id.checkUpdate:
                    break;
                case R.id.aboutUs:
                    break;
                case R.id.logout:
                    if(MainActivity.isLogin)
                        logout();
                    else
                        Toast.makeText(getApplicationContext(),"您当前未登录",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //点击退出登录的事件
    private void logout(){
        //注销改变登录状态
        SharedPreferences.Editor statusEditor=getSharedPreferences("status", Context.MODE_PRIVATE).edit();
        statusEditor.putBoolean("isLogin",false);
        statusEditor.apply();

        //注销清除用户信息
        SharedPreferences.Editor userEditor=getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        userEditor.clear();
        userEditor.apply();

        SharedPreferences.Editor chargeRecordEditor=getSharedPreferences("chargeRecord", Context.MODE_PRIVATE).edit();
        chargeRecordEditor.clear();
        chargeRecordEditor.apply();

        Intent intent = new Intent(SoftwareSet.this,MainActivity.class) ;
        startActivity(intent);
        finish();
    }
}
