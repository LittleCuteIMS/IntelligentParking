package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends AppCompatActivity {

    TextView mobile;
    TextView nickName;

    protected Handler handler0 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {//如果不为空
                //解析json数据并保存
                String jsonStr = msg.obj.toString();
                //解析json数据，按键名存在SharedPreference中
                JSONObject jsonObject;
                String nickNameString=null;
                try {
                    jsonObject = new JSONObject(jsonStr);
                    nickNameString=jsonObject.getString("nickname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //保存用户昵称到手机
                SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                recordEditor.putString("nickname", nickNameString);
                //将相应消息放在对应的控件显示
                SharedPreferences userPref=getSharedPreferences("user",Context.MODE_PRIVATE);
                String mobileString=userPref.getString("mobile",null);
                mobile.setText(mobileString);
                nickName.setText(nickNameString);
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        //设置toolbar导航栏，设置导航按钮
        Toolbar user_toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(user_toolbar);
        user_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobile = (TextView) findViewById(R.id.mobile);//显示用户手机号码
        nickName = (TextView) findViewById(R.id.nickname);//显示用户昵称

        getUserInfo();

        //获取按钮，设置监听事件
        Button resetpwd_button = (Button) findViewById(R.id.resetpwd_button);
        resetpwd_button.setOnClickListener(setListener);
    }

    public void getUserInfo() {
        //1.从sharedPreference里面获取当前账户手机号
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);

        //2.将用户手机号转为json
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //3.把手机号发送到服务器上进行查询
        String path = "user/userinfo_inquiry.php";
        HttpJson http = new HttpJson(path, json.toString(), handler0);
        new Thread(http.getHttpThread()).start();
    }


    //解析Json数据
    private void parseJSONWithJSONObject(String jsonData) {
        try {

            //？得到json数组
            JSONObject jsonObject = new JSONObject(jsonData);
            String nickName=jsonObject.getString("nickname");

                //保存当前用户记录
                SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                recordEditor.putString("nickname", nickName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置的监听事件
    View.OnClickListener setListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.resetpwd_button:
                    Intent intent = new Intent(getApplicationContext(), Resetpwd.class);
                    startActivity(intent);
                    break;

            }
        }
    };

}

