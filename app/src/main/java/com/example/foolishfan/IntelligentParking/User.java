package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);




        //重置密码按钮
        Button mResetPwdButton = (Button) findViewById(R.id.resetpwd_button);
        //设置监听事件
        mResetPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_User_to_Resetpwd = new Intent(getApplicationContext(), Resetpwd.class);    //切换User Activity至Resetpwd Activity
                startActivity(intent_User_to_Resetpwd);
                finish();
            }
        });

        //设置toolbar导航栏，设置导航按钮
        Toolbar user_toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(user_toolbar);
        user_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
