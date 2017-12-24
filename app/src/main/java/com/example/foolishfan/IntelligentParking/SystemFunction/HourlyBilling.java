package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJsonModified;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;


/**
 * Created by 蔡创 on 2017-12-13.
 * 计入停车场的计时计费页面
 */

public class HourlyBilling extends AppCompatActivity {

    final private int CARINFO = 1;
    final private int STARTPARK=2;
    final private int STOPPARK=3;
    final private int PARKINFO=4;
    private Handler billingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PARKINFO:
                    if(msg.obj != null){
                        String message=msg.obj.toString();
                        if(!message.equals("FALSE")){
                            showParkInfo(message);
                        }else{
                            Toast.makeText(HourlyBilling.this,"系统中无此停车场",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(HourlyBilling.this,R.string.network_error,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CARINFO:
                    if (msg.obj != null) {
                        setCarSpinner(msg.obj.toString());
                    }else{
                        Toast.makeText(HourlyBilling.this,R.string.network_error,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case STARTPARK:
                    if(msg.obj!=null){
                        Toast.makeText(HourlyBilling.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(HourlyBilling.this,R.string.network_error,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case STOPPARK:
                    if(msg.obj!=null){
                        handleTradingStatus(msg.obj.toString());
                    }else{
                        Toast.makeText(HourlyBilling.this,R.string.network_error,Toast.LENGTH_SHORT).show();
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

        String park_id = getParkID();
        getParkInfoDetails(park_id);
        getCarInfo();
        setStartPark(park_id);
        setStopPark(park_id);
    }

    //从二维码中获取停车场信息
    private String getParkID() {
        //接受二维码扫描的信息
        Intent intent = getIntent();// 收取 email
        Bundle bundle = intent.getBundleExtra("qr_code_info");// 打开 email
        String parkInfoStr = bundle.getString("parkInfoJson");//读取内容能够
        String string=null;
        try {
            string=new JSONObject(parkInfoStr).getString("park_id");//获取停车场的id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private void getParkInfoDetails(String parkID){
        JSONObject json = new JSONObject();
        try {
            json.put("park_id", parkID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path = "parkPHP/getParkInfoDetails.php";
        HttpJsonModified http = new HttpJsonModified(path, json.toString(), billingHandler, PARKINFO);
        new Thread(http.getHttpThread()).start();
    }

    //显示停车场的相关信息
    private void showParkInfo(String parkInfoStr) {
        //获取显示停车场信息的textview
        TextView parkNameView, parkPhoneView, parkChargeView;
        parkNameView = (TextView) findViewById(R.id.billing_name);
        parkPhoneView = (TextView) findViewById(R.id.billing_phone);
        parkChargeView = (TextView) findViewById(R.id.billing_charge);

        //将二维码扫描到的信息放入相应的textview
        try {
            JSONArray jsonArry=new JSONArray(parkInfoStr);
            JSONObject parkInfoJsonObj=jsonArry.getJSONObject(0);
            String parkName = parkNameView.getText().toString() + parkInfoJsonObj.getString("name");
            parkNameView.setText(parkName);
            String parkPhone = parkPhoneView.getText().toString() + parkInfoJsonObj.getString("phone");
            parkPhoneView.setText(parkPhone);
            String parkCharge = parkChargeView.getText().toString() + parkInfoJsonObj.getString("charge");
            parkChargeView.setText(parkCharge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //从服务器获取用户车辆信息
    private void getCarInfo() {
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path = "user/carinfo_inquiry.php";
        HttpJsonModified http = new HttpJsonModified(path, json.toString(), billingHandler, CARINFO);
        new Thread(http.getHttpThread()).start();
    }

    //对车辆选择下拉列表设置下拉项
    private void setCarSpinner(String carInfoStr) {
        JSONArray jsonArray = null;
        JSONObject jsonTmp = null;
        List<String> carList = new ArrayList<String>();
        try {
            jsonArray = new JSONArray(carInfoStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonTmp = jsonArray.getJSONObject(i);
                carList.add(jsonTmp.getString("plate_number"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Spinner carSpinner = (Spinner) findViewById(R.id.car_spinner);
        ArrayAdapter<String> carInfoAdapter = new ArrayAdapter<String>(HourlyBilling.this, android.R.layout.simple_spinner_item, carList);
        carInfoAdapter.setDropDownViewResource(R.layout.spinner_item);
        carSpinner.setAdapter(carInfoAdapter);
    }

    //对开始停车按钮设置监听事件
    private void setStartPark(final String parkID) {
        Button startParkBtn = (Button) findViewById(R.id.start_park);
        startParkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示开始停车时间
                TextView billInTime = (TextView) findViewById(R.id.billing_in_time);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datetime = sDateFormat.format(new Date());
                String billInTimeStr = "进入时间:" + datetime;
                billInTime.setText(billInTimeStr);
                //开始计时
                Chronometer billingTimeChronometer = (Chronometer)findViewById(R.id.billing_time);
                billingTimeChronometer.start();
                //发送开始停车信息到后台服务器
                JSONObject jsonInfo = new JSONObject();
                try {
                    jsonInfo.put("park_id",parkID);
                    Spinner carSpinner = (Spinner) findViewById(R.id.car_spinner);
                    String plateNumber = carSpinner.getSelectedItem().toString();
                    jsonInfo.put("plate_number",plateNumber);
                    jsonInfo.put("in_datetime",datetime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = "parkPHP/parking_record_in.php";
                HttpJsonModified httpJson = new HttpJsonModified(path,jsonInfo.toString(),billingHandler,STARTPARK);
                new Thread(httpJson.getHttpThread()).start();
                //开始按钮不可点击，结束按钮可点击
                setButtonEnabled(false,true);
            }
        });
    }

    //对结束停车设置监听事件
    private void setStopPark(final String parkID){
        Button stopParkBtn = (Button)findViewById(R.id.stop_park);
        stopParkBtn.setEnabled(false);
        stopParkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止计时
                Chronometer billingTimeChronometer = (Chronometer)findViewById(R.id.billing_time);
                billingTimeChronometer.stop();
                //发送停车结束信息到服务器
                JSONObject jsonInfo = new JSONObject();
                try {
                    jsonInfo.put("park_id",parkID);//停车场编号
                    Spinner carSpinner = (Spinner) findViewById(R.id.car_spinner);
                    String plateNumber = carSpinner.getSelectedItem().toString();
                    jsonInfo.put("plate_number",plateNumber);//车牌号
                    TextView billInTime = (TextView) findViewById(R.id.billing_in_time);
                    String prefix = "进入时间:";
                    String inTime = billInTime.getText().toString();
                    inTime = inTime.substring(prefix.length());
                    jsonInfo.put("in_datetime",inTime);//进入停车场时间
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String outTime = sDateFormat.format(new Date());
                    jsonInfo.put("out_datetime",outTime);//离开停车场时间
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String path = "parkPHP/parking_record_out.php";
                HttpJsonModified httpJson = new HttpJsonModified(path,jsonInfo.toString(),billingHandler,STOPPARK);
                new Thread(httpJson.getHttpThread()).start();
                //开始按钮可点击，结束按钮不可点击
                setButtonEnabled(true,false);
            }
        });
    }

    //控制两个按钮的可点击性
    private void setButtonEnabled(boolean start, boolean stop){
        Button startParkBtn = (Button) findViewById(R.id.start_park);
        Button stopParkBtn = (Button)findViewById(R.id.stop_park);
        startParkBtn.setEnabled(start);
        stopParkBtn.setEnabled(stop);
    }

    //结束停车后，接受服务器反馈信息后的处理过程
    private void handleTradingStatus(String tradingStatusStr){
        char[] tradingStatus=tradingStatusStr.toCharArray();
        if(tradingStatus[0]=='0'){
            Toast.makeText(HourlyBilling.this,R.string.tradingStatus0,Toast.LENGTH_SHORT).show();
        }else if(tradingStatus[1]=='0'){
            Toast.makeText(HourlyBilling.this,R.string.tradingStatus1,Toast.LENGTH_SHORT).show();
        }else if(tradingStatus[2]=='0'){
            Toast.makeText(HourlyBilling.this,R.string.tradingStatus2,Toast.LENGTH_SHORT).show();
        }else if(tradingStatus[3]=='0'){
            Toast.makeText(HourlyBilling.this,R.string.tradingStatus3,Toast.LENGTH_SHORT).show();
        }else if(tradingStatus[4]=='0'){
            Toast.makeText(HourlyBilling.this,R.string.tradingStatus4,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(HourlyBilling.this,"停车扣费成功",Toast.LENGTH_SHORT).show();
        }
    }
}
