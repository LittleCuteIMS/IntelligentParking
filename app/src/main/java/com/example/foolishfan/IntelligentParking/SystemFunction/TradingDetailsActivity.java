package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJsonModified;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CaiChuang on 2017-12-26.
 */

public class TradingDetailsActivity extends AppCompatActivity {
    private TextView trading_amout_tv,trading_car_tv,trading_park_tv,park_charge_amout_tv;
    private TextView trading_time_tv,trading_id_tv;
    private String datetime,plateNumber;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(msg.obj!=null){
                        if(!msg.obj.toString().equals("0")){
                            setTradingTextViewValue(msg.obj.toString());
                        }
                    }else{
                        Toast.makeText(TradingDetailsActivity.this,R.string.network_error,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_details);

        //设置toolbar导航栏，设置导航按钮
        Toolbar trading_details_toolbar = (Toolbar) findViewById(R.id.trading_details_toolbar);
        setSupportActionBar(trading_details_toolbar);
        trading_details_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTradingTextView();
        getDateTimeAndPlateNumber();
        getTradingInfoDetails();
    }

    public void setTradingTextView(){
        trading_amout_tv=(TextView)findViewById(R.id.trading_amout_tv);
        trading_car_tv=(TextView)findViewById(R.id.trading_car_tv);
        trading_park_tv=(TextView)findViewById(R.id.trading_park_tv);
        park_charge_amout_tv=(TextView)findViewById(R.id.park_charge_amout_tv);
        trading_time_tv=(TextView)findViewById(R.id.trading_time_tv);
        trading_id_tv=(TextView)findViewById(R.id.trading_id_tv);
    }

    public void getDateTimeAndPlateNumber(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("billingToTradingDetail");
        plateNumber = bundle.getString("plate_number");
        datetime=bundle.getString("out_datetime");
    }

    public void getTradingInfoDetails(){
        JSONObject json=new JSONObject();
        try {
            json.put("plate_number",plateNumber);
            json.put("datetime",datetime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String path="parkPHP/tradingDetailsSelect.php";
        HttpJsonModified mHttpJson=new HttpJsonModified(path,json.toString(),mHandler,1);
        new Thread(mHttpJson.getHttpThread()).start();
    }

    public void setTradingTextViewValue(String detailsStr){
        String tradingAmount=null,parkName=null,parkCharge=null,tradingID=null;
        try {
            JSONArray jsonArray=new JSONArray(detailsStr);
            JSONObject detailsJson=jsonArray.getJSONObject(0);
            tradingAmount=detailsJson.getString("amount");
            parkName=detailsJson.getString("name");
            parkCharge=detailsJson.getString("charge");
            tradingID=detailsJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        trading_amout_tv.setText(tradingAmount);
        trading_car_tv.setText(plateNumber);
        trading_park_tv.setText(parkName);
        trading_time_tv.setText(datetime);
        park_charge_amout_tv.setText(parkCharge);
        trading_id_tv.setText(tradingID);
    }
}
