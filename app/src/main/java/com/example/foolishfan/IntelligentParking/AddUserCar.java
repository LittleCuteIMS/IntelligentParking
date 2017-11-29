package com.example.foolishfan.IntelligentParking;

/**
 * Created by LiangJiacheng on 2017/11/13 0013.
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

import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;



public class AddUserCar extends AppCompatActivity {          //用户添加车辆信息界面

    private EditText mPlateNumber;                        //用户车牌号编辑
    private EditText mRemark;                            //备注编辑
    private Handler handler = new Handler() {  //登录接收服务器返回的信息
        public void handleMessage(Message msg) {
            if (msg.obj != null) {//如果不为空
                if (msg.obj.toString().trim().equals("SUCCEED")) {
                    Toast.makeText(getApplicationContext(), "车牌添加成功", Toast.LENGTH_SHORT).show();
                    //切换AddUserCar Activity至Main Activity
                    Intent intent_AddUserCar_to_Main = new Intent(AddUserCar.this, MainActivity.class);
                    startActivity(intent_AddUserCar_to_Main);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_car);

        //设置toolbar导航栏，设置导航按钮
        Toolbar addusercar_toolbar = (Toolbar) findViewById(R.id.addusercar_toolbar);
        setSupportActionBar(addusercar_toolbar);
        addusercar_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //通过id找到相应的控件
        mPlateNumber = (EditText) findViewById(R.id.addusercar_edit_plate_number);
        mRemark = (EditText) findViewById(R.id.addusercar_edit_remark);
        Button mAddButton = (Button) findViewById(R.id.addusercar_btn_add);
        Button mCancelButton = (Button) findViewById(R.id.addusercar_btn_cancel);

        //设置监听事件
        mAddButton.setOnClickListener(mListener);
        mCancelButton.setOnClickListener(mListener);
    }

    OnClickListener mListener = new OnClickListener() {//不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addusercar_btn_add:
                    Add();
                    break;
                case R.id.addusercar_btn_cancel:                              //用户车辆界面的取消新增按钮
                    mPlateNumber.setText("");
                    mRemark.setText("");
                    break;
            }
        }
    };

    public void Add() {                                              //确认新增按钮监听事件
        if (isUserCarValid()) {
            //从sharedPreference里面获取当前账户手机号
            SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
            String mobile = pref.getString("mobile", null);
            String plate_number = mPlateNumber.getText().toString().trim();    //获取当前输入的车牌号和备注信息
            String remarks = mRemark.getText().toString().trim();

            //将用户的车辆信息保存在sharedPreference里面
            SharedPreferences.Editor userEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
            userEditor.putString("plateNumber", plate_number);
            userEditor.putString("Remark", remarks);
            userEditor.apply();

            //将用户手机号，密码转为json
            JSONObject json = new JSONObject();
            try {
                json.put("mobile",mobile);
                json.put("plate_number", plate_number);
                json.put("remarks", remarks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //把用户信息发送到服务器上
            String path = "user/carinfo_insert.php";
            HttpJson http = new HttpJson(path, json.toString(), handler);
            new Thread(http.getHttpThread()).start();
        }
    }

    public boolean isUserCarValid() {
       if (mPlateNumber.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.plate_number_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if (mRemark.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.remark_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
