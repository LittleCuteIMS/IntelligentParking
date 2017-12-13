package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 蔡创 on 2017-12-13.
 * 计入停车场的计时计费页面
 */

public class ParkInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);

        //设置toolbar导航栏，设置导航按钮
        Toolbar park_info_toolbar = (Toolbar) findViewById(R.id.park_info_toolbar);
        setSupportActionBar(park_info_toolbar);
        park_info_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showParkInfo();
    }

    private void showParkInfo(){
        //接受二维码扫描的信息
        Intent intent = getIntent();// 收取 email
        Bundle bundle = intent.getBundleExtra("qr_code_info");// 打开 email
        String parkInfoJson = bundle.getString("parkInfoJson");// 读取内容
        TextView parkNameView,parkAddressView,parkPhoneView,parkChargeView;
        parkNameView=(TextView)findViewById(R.id.parkNameView);
        parkAddressView=(TextView)findViewById(R.id.parkAddressView);
        parkPhoneView=(TextView)findViewById(R.id.parkPhoneView);
        parkChargeView=(TextView)findViewById(R.id.parkChargeView);

        try {
            JSONObject jsonObj=new JSONObject(parkInfoJson);
            String parkName=parkNameView.getText().toString()+jsonObj.getString("name");
            parkNameView.setText(parkName);
            String parkAddress=parkAddressView.getText().toString()+jsonObj.getString("address");
            parkAddressView.setText(parkAddress);
            String parkPhone=parkPhoneView.getText().toString()+jsonObj.getString("phone");
            parkPhoneView.setText(parkPhone);
            String parkCharge=parkChargeView.getText().toString()+jsonObj.getString("charge");
            parkChargeView.setText(parkCharge);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
