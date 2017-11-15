package com.example.foolishfan.IntelligentParking;

/**
 * Created by Administrator on 2017/11/13 0013.
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

        //通过id找到相应的控件
        mPlateNumber = (EditText) findViewById(R.id.usercar_edit_plate_number);
        mRemark = (EditText) findViewById(R.id.usercar_edit_remarks);
        Button mAddButton = (Button) findViewById(R.id.usercar_btn_add);
        Button mCancelButton = (Button) findViewById(R.id.usercar_btn_cancle);

        //设置监听事件
        mAddButton.setOnClickListener(mListener);
        mCancelButton.setOnClickListener(mListener);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj != null) {//如果不为空
                    if (msg.obj.toString().equals("SUCCEED")) {
                        //保存登录状态
                        SharedPreferences.Editor statusEditor = getSharedPreferences("status", Context.MODE_PRIVATE).edit();
                        statusEditor.putBoolean("isLogin", true);
                        statusEditor.apply();

                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();

                        //切换UserCar Activity至Main Activity
                        Intent intent_UserCar_to_Main = new Intent(UserCar.this, MainActivity.class);
                        startActivity(intent_UserCar_to_Main);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "车牌号已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    OnClickListener mListener = new OnClickListener() {//不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.usercar_btn_add:                            //用户车辆的确认新增按钮
                    Intent intent_UserCar_to_Main = new Intent(UserCar.this, MainActivity.class);    //切换UserCar Activity至MainActivity
                    startActivity(intent_UserCar_to_Main);
                    break;
                case R.id.usercar_btn_cancel:                              //用户车辆界面的取消新增按钮
                    add();
                    break;
            }
        }
    };

    /*public void add() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String mobile = mMobile.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();

            //将用户的登录信息保存在sharedPreference里面
            SharedPreferences.Editor userEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
            userEditor.putString("mobile", mobile);
            userEditor.putString("userPwd", userPwd);
            userEditor.apply();

            //将用户手机号，密码转为json
            JSONObject json = new JSONObject();
            try {
                json.put("platenumber", mobile);
                json.put("password", userPwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //把用户信息发送到服务器上
            String path = "user/carinfo_insert.php";
            HttpJson http = new HttpJson(path, json.toString(), handler);
            new Thread(http.getHttpThread()).start();
        }
    }*/
}
