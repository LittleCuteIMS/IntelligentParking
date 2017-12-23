package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 蔡创 on 2017-12-13.
 * 计入停车场的计时计费页面
 */

public class HourlyBilling extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_billing);

        //设置toolbar导航栏，设置导航按钮
        Toolbar park_info_toolbar = (Toolbar) findViewById(R.id.hourly_billing_toolbar);
        setSupportActionBar(park_info_toolbar);
        park_info_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        JSONObject parkInfoJsonObj = null;
        String park_id = null;
        try {
            parkInfoJsonObj = getParkInfoJson();
            park_id = showParkInfo(parkInfoJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setStartPark();
    }

    //从二维码中获取停车场信息
    private JSONObject getParkInfoJson() throws JSONException {
        //接受二维码扫描的信息
        Intent intent = getIntent();// 收取 email
        Bundle bundle = intent.getBundleExtra("qr_code_info");// 打开 email
        String parkInfoStr = bundle.getString("parkInfoJson");//读取内容能够
        return new JSONObject(parkInfoStr);//转换为json对象
    }

    //显示停车场的相关信息，返回停车场的编号
    private String showParkInfo(JSONObject parkInfoJsonObj) throws JSONException {
        //获取显示停车场信息的textview
        TextView parkNameView, parkPhoneView, parkChargeView;
        parkNameView = (TextView) findViewById(R.id.billing_name);
        parkPhoneView = (TextView) findViewById(R.id.billing_phone);
        parkChargeView = (TextView) findViewById(R.id.billing_charge);
        String park_id = null;

        //将二维码扫描到的信息放入相应的textview
        String parkName = parkNameView.getText().toString() + parkInfoJsonObj.getString("park_name");
        parkNameView.setText(parkName);
        String parkPhone = parkPhoneView.getText().toString() + parkInfoJsonObj.getString("park_phone");
        parkPhoneView.setText(parkPhone);
        String parkCharge = parkChargeView.getText().toString() + parkInfoJsonObj.getString("park_charge");
        parkChargeView.setText(parkCharge);
        park_id = parkInfoJsonObj.getString("park_id");

        return park_id;
    }

    //对开始停车按钮设置监听事件
    private void setStartPark() {
        Button startParkBtn = (Button) findViewById(R.id.start_park);
        startParkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }
}
