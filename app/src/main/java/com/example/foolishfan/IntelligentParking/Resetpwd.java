package com.example.foolishfan.IntelligentParking;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Resetpwd extends AppCompatActivity {
    private EditText mMobile;                        //用户手机号编辑
    private EditText mPwd_old;                            //密码编辑
    private EditText mPwd_new;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private Handler handler;                            //控制线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);

        //设置toolbar导航栏，设置导航按钮
        Toolbar resetpwd_toolbar = (Toolbar) findViewById(R.id.resetpwd_toolbar);
        setSupportActionBar(resetpwd_toolbar);
        resetpwd_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        layout.setOrientation(RelativeLayout.VERTICAL).
        mMobile = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_resetpwd_Listener);

        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.obj!=null) {//如果不为空
                    if (msg.obj.toString().equals("SUCCEED") ) {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent_Register_to_Login = new Intent(Resetpwd.this, Login.class);    //切换Register Activity至Login Activity
                        startActivity(intent_Register_to_Login);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "网络错误",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }
    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resetpwd_btn_sure:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
                case R.id.resetpwd_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Resetpwd_to_Login = new Intent(Resetpwd.this,Login.class) ;    //切换Resetpwd Activity至Login Activity
                    startActivity(intent_Resetpwd_to_Login);
                    finish();
                    break;
            }
        }
    };
    public void resetpwd_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String mobile= mMobile.getText().toString().trim();
            String userPwd_old = mPwd_old.getText().toString().trim();
            String userPwd_new = mPwd_new.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
            if(!userPwd_new.equals(userPwdCheck)){     //新密码与新密码不一致
                Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
            } else {
                //将手机号，密码转为json
                JSONObject json=new JSONObject();
                try {

                    json.put("mobile",mobile);
                    json.put("oldpassword",userPwd_old);
                    json.put("newpassword",userPwdCheck);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //服务器上注册
                String path="user/password_revise.php";
                HttpJson http=new HttpJson(path,json.toString(),handler);
                new Thread(http.getHttpThread()).start();
            }
        }
    }
    public boolean isUserNameAndPwdValid() {
        String userName = mMobile.getText().toString().trim();
        if (mMobile.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.mobile_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_new_empty),Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

