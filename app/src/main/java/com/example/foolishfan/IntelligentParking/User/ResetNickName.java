package com.example.foolishfan.IntelligentParking.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.MainActivity;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/12/25 .
 */

public class ResetNickName extends AppCompatActivity {
    private EditText mMobile;                        //用户手机号编辑
    private EditText mNickName;                        //用户新昵称编辑
    private Button mSaveButton;                       //确定按钮
    private Handler handler;                            //控制线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_nickname);

        //设置toolbar导航栏，设置导航按钮
        Toolbar reset_nickname_toolbar = (Toolbar) findViewById(R.id.reset_nickname_toolbar);
        setSupportActionBar(reset_nickname_toolbar);
        reset_nickname_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        layout.setOrientation(RelativeLayout.VERTICAL).
        mMobile = (EditText) findViewById(R.id.reset_nickname_mobile);
        mNickName = (EditText) findViewById(R.id.reset_edit_nickname);
        mSaveButton = (Button) findViewById(R.id.btn_save_nickname);

        mSaveButton.setOnClickListener(m_reset_nickname_Listener);      //界面保存按钮的监听事件

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj != null) {//如果不为空
                    if (msg.obj.toString().equals("SUCCEED")) {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent_ResetNickName_to_User = new Intent(ResetNickName.this, User.class);    //切换ResetNickName Activity至User Activity
                        startActivity(intent_ResetNickName_to_User);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }


    View.OnClickListener m_reset_nickname_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save_nickname:                     //保存按钮的监听事件,由修改昵称界面返回用户信息界面
                    save_nickname_check();
                    Intent intent_ResetNickName_to_MainActivity = new Intent(ResetNickName.this, MainActivity.class);    //切换ResetNickName Activity至User Activity
                    startActivity(intent_ResetNickName_to_MainActivity);
                    finish();
                    break;
            }
        }
    };

    public void save_nickname_check() {//保存按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String mobile = mMobile.getText().toString().trim();
            String userNickName_new = mNickName.getText().toString().trim();
            //将手机号，新昵称转为json
            JSONObject json = new JSONObject();
            try {

                json.put("mobile", mobile);
                json.put("newnickname", userNickName_new);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //服务器上注册
            String path = "user/reset_nickname.php";
            HttpJson http = new HttpJson(path, json.toString(), handler);
            new Thread(http.getHttpThread()).start();
        }
    }


    public boolean isUserNameAndPwdValid() {
        String userName = mMobile.getText().toString().trim();
        if (mMobile.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.mobile_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mNickName.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.newNickname_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}

