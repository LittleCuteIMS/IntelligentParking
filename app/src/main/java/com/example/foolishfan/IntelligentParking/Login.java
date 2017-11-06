package com.example.foolishfan.IntelligentParking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends Activity {                 //登录界面活动

    private EditText mMobile;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Handler handler;                   //登录接收服务器返回的信息

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //通过id找到相应的控件
        mMobile = (EditText) findViewById(R.id.login_edit_mobile);
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        Button mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        Button mLoginButton = (Button) findViewById(R.id.login_btn_login);
        Button mCancelButton = (Button) findViewById(R.id.login_btn_cancle);

        //设置监听事件
        mRegisterButton.setOnClickListener(mListener);
        mLoginButton.setOnClickListener(mListener);
        mCancelButton.setOnClickListener(mListener);

        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.obj!=null) {//如果不为空
                    if (msg.obj.toString().equals("SUCCEED")) {
                        //保存登录状态
                        SharedPreferences.Editor statusEditor=getSharedPreferences("status",Context.MODE_PRIVATE).edit();
                        statusEditor.putBoolean("isLogin",true);
                        statusEditor.apply();

                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();

                        //切换Login Activity至Main Activity
                        Intent intent_Login_to_Main = new Intent(Login.this, MainActivity.class);
                        startActivity(intent_Login_to_Main);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "手机号不存在", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "网络错误",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }
    OnClickListener mListener = new OnClickListener() {//不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(Login.this,Register.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    login();
                    break;
                case R.id.login_btn_cancle:                             //登录界面的注销按钮
                    cancel();
                    break;
            }
        }
    };

    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String mobile = mMobile.getText().toString().trim();    //获取当前输入的用户名和密码信息
            String userPwd = mPwd.getText().toString().trim();

            //将用户的登录信息保存在sharedPreference里面
            SharedPreferences.Editor userEditor=getSharedPreferences("user", Context.MODE_PRIVATE).edit();
            userEditor.putString("mobile",mobile);
            userEditor.putString("userPwd",userPwd);
            userEditor.apply();

            //将用户手机号，密码转为json
            JSONObject json=new JSONObject();
            try {
                json.put("mobile",mobile);
                json.put("password",userPwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //把用户信息发送到服务器上
            String path="user/login.php";
            HttpJson http=new HttpJson(path,json.toString(),handler);
            new Thread(http.getHttpThread()).start();
        }
    }

    public void cancel() {//取消
        Intent intent_Login_to_main = new Intent(Login.this,MainActivity.class) ;    //切换Login Activity至Main Activity
        startActivity(intent_Login_to_main);
        finish();
    }

    public boolean isUserNameAndPwdValid() {
        if (mMobile.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.mobile_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
