package com.example.foolishfan.IntelligentParking.SystemFunction;

/**
 * Created by LiangJiacheng on 2017/11/19 0019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.example.foolishfan.IntelligentParking.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;

public class MessageCenter extends AppCompatActivity {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        TextView tv = (TextView)findViewById(R.id.textview);
        String content = getIntent().getStringExtra("content");
        if(content!= null){
            //将传回来的信息放在SharedPreferences
            SharedPreferences.Editor recordEditor = getSharedPreferences("message", Context.MODE_PRIVATE).edit();
            recordEditor.putString("message", content);
            recordEditor.apply();
        }else {
            tv.setText("");
        }
        //取出数据
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String Jmessage = pref.getString("message", null);
        tv.setText(Jmessage);

        //设置为调试模式，具体发布的时候可以直接设置为false
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //设置toolbar导航栏，设置导航按钮center
        Toolbar messagecenter_toolbar = (Toolbar) findViewById(R.id.messagecenter_toolbar);
        setSupportActionBar(messagecenter_toolbar);
        messagecenter_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}