package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;
import com.example.foolishfan.IntelligentParking.Util.HttpJsonModified;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 蔡创 on 2017-12-13.
 * 计入停车场的计时计费页面
 */

public class HourlyBilling extends AppCompatActivity {

    final private int CARINFO=1;
    private Handler billingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case CARINFO:
                    if(msg.obj != null){
                        setCarSpinner(msg.obj.toString());
                    }
                    break;
            }
        }
    };

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
        parkInfoJsonObj = getParkInfoJson();
        park_id = showParkInfo(parkInfoJsonObj);
        getCarInfo();
        setStartPark();
    }

    //从二维码中获取停车场信息
    private JSONObject getParkInfoJson(){
        //接受二维码扫描的信息
        Intent intent = getIntent();// 收取 email
        Bundle bundle = intent.getBundleExtra("qr_code_info");// 打开 email
        String parkInfoStr = bundle.getString("parkInfoJson");//读取内容能够
        try {
            return new JSONObject(parkInfoStr);//转换为json对象
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //显示停车场的相关信息，返回停车场的编号
    private String showParkInfo(JSONObject parkInfoJsonObj){
        //获取显示停车场信息的textview
        TextView parkNameView, parkPhoneView, parkChargeView;
        parkNameView = (TextView) findViewById(R.id.billing_name);
        parkPhoneView = (TextView) findViewById(R.id.billing_phone);
        parkChargeView = (TextView) findViewById(R.id.billing_charge);
        String park_id = null;

        //将二维码扫描到的信息放入相应的textview
        String parkName = null;
        try {
            parkName = parkNameView.getText().toString() + parkInfoJsonObj.getString("park_name");
            parkNameView.setText(parkName);
            String parkPhone = parkPhoneView.getText().toString() + parkInfoJsonObj.getString("park_phone");
            parkPhoneView.setText(parkPhone);
            String parkCharge = parkChargeView.getText().toString() + parkInfoJsonObj.getString("park_charge");
            parkChargeView.setText(parkCharge);
            park_id = parkInfoJsonObj.getString("park_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return park_id;
    }

    //从服务器获取用户车辆信息
    private void getCarInfo(){
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path="user/carinfo_inquiry.php";
        HttpJsonModified http=new HttpJsonModified(path,json.toString(),billingHandler,CARINFO);
        new Thread(http.getHttpThread()).start();
    }

    //对车辆选择下拉列表设置下拉项
    private void setCarSpinner(String carInfoStr){
        JSONArray jsonArray = null;
        JSONObject jsonTmp = null;
        List<String> carList=new ArrayList<String>();
        try {
            jsonArray = new JSONArray(carInfoStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonTmp = jsonArray.getJSONObject(i);
                carList.add(jsonTmp.getString("plate_number"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Spinner carSpinner=(Spinner)findViewById(R.id.car_spinner);
        ArrayAdapter<String> carInfoAdapter=new ArrayAdapter<String>(HourlyBilling.this,android.R.layout.simple_spinner_item, carList);
        carSpinner.setAdapter(carInfoAdapter);
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
