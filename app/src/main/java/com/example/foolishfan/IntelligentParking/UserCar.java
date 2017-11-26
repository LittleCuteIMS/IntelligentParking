package com.example.foolishfan.IntelligentParking;

/**
 * Created by LiangJiacheng on 2017/11/19 0019.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

    public class UserCar extends AppCompatActivity {          //用户车辆信息界面

        private EditText mPlateNumber;                        //用户车牌号
        private EditText mRemark;                            //备注编辑
        private Handler handler;                   //登录接收服务器返回的信息

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_car);

            //设置toolbar导航栏，设置导航按钮
            Toolbar usercar_toolbar = (Toolbar) findViewById(R.id.usercar_toolbar);
            setSupportActionBar(usercar_toolbar);
            usercar_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

}
