package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class User extends AppCompatActivity {
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        mReturnButton = (Button)findViewById(R.id.returnback);

    }
    public void back_to_login(View view) {
        //注销改变登录状态
        SharedPreferences.Editor statusEditor=getSharedPreferences("status", Context.MODE_PRIVATE).edit();
        statusEditor.putBoolean("isLogin",false);
        statusEditor.apply();

        //注销清除用户信息
        SharedPreferences.Editor userEditor=getSharedPreferences("user", Context.MODE_PRIVATE).edit();
        userEditor.clear();
        userEditor.apply();

        Intent intent3 = new Intent(User.this,Login.class) ;
        startActivity(intent3);
        finish();
    }
}
