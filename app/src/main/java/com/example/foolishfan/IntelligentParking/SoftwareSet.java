package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/11/6 0006.
 */

public class SoftwareSet extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.softwareset);

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
                    logout();
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

        Intent intent = new Intent(SoftwareSet.this,MainActivity.class) ;
        startActivity(intent);
        finish();
    }
}
