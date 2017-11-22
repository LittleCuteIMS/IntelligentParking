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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class ParkingHistory extends AppCompatActivity {          //用户停车记录信息界面

    private EditText mPlateNumber;                            //用户车牌号
    private EditText mRemark;                                 //备注编辑
    private EditText mParkingLotName;                        //停车场名称
    private EditText mParkingLotLocation;                    //停车场地址
    private EditText mEnterTime;                            //进入停车场时间
    private EditText mLeaveTime;                            //离开停车场时间
    private EditText mParkingTime;                          //停车时长
    private EditText mParkingCost;                         //用户停车扣费
    private Handler handler;                   //登录接收服务器返回的信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkinghistory);

        //设置toolbar导航栏，设置导航按钮
        Toolbar parkinghistory_toolbar = (Toolbar) findViewById(R.id.parkinghistory_toolbar);
        setSupportActionBar(parkinghistory_toolbar);
        parkinghistory_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
