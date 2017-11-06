package com.example.foolishfan.IntelligentParking;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    private EditText mNickname;                       //用户昵称编辑
    private EditText mMobile;                        //用户手机号编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private Handler handler;                            //控制线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mNickname=(EditText)findViewById(R.id.register_edit_name);
        mMobile = (EditText) findViewById(R.id.register_edit_mobile);
        mPwd = (EditText) findViewById(R.id.register_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.register_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);

        handler = new Handler(){
            public void handleMessage(Message msg){
                Toast toast1;
                if(msg.obj!=null) {//如果不为空
                    if (msg.obj.toString().equals("SUCCEED") ) {
                        toast1 = Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.BOTTOM, 0, 0);
                        toast1.show();
                        Intent intent_Register_to_Login = new Intent(Register.this, Login.class);    //切换Register Activity至Login Activity
                        startActivity(intent_Register_to_Login);
                        finish();
                    } else {
                        toast1 = Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.BOTTOM, 0, 0);
                        toast1.show();
                    }
                }else{
                    toast1=Toast.makeText(getApplicationContext(), "网络错误",Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.BOTTOM, 0, 0);
                    toast1.show();
                }
                super.handleMessage(msg);
            }
        };

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this,Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };
    
    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String nickname=mNickname.getText().toString().trim();
            String mobile= mMobile.getText().toString().trim();
            String userPwd = mPwd.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
            if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
                Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
                return ;
            } else {
                //将用户昵称，手机号，密码转为json
                JSONObject json=new JSONObject();
                try {
                    json.put("nick_name",nickname);
                    json.put("mobile",mobile);
                    json.put("password",userPwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //服务器上注册
                String path="user/register.php";
                HttpJson http=new HttpJson(path,json.toString(),handler);
                new Thread(http.getHttpThread()).start();
            }
        }
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
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
