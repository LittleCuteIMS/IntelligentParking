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

import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhanglin on 2017/11/7.
 */
public class Finance extends AppCompatActivity{
    private Handler handler1;        //接收服务器查询返回的充值记录
    TextView userbalance;
    private Handler handler = new Handler() {//接收服务器查询返回的余额信息
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                //保存当前账户余额
                String balance=msg.obj.toString();
                userbalance.setText(balance);
                SharedPreferences.Editor statusEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                statusEditor.putString("balance", balance);
                statusEditor.apply();
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        getBalance();

        userbalance = (TextView) findViewById(R.id.amountshow);//动态显示余额
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String balance = pref.getString("balance","0.00");//余额刚开始为0
        userbalance.setText(balance);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.finance_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            finish();
        }
        });

        //获取按钮，设置监听事件
        Button charge=(Button)findViewById(R.id.charge);
        Button chargeShow=(Button)findViewById(R.id.chargeshow);
        charge.setOnClickListener(setListener);
        chargeShow.setOnClickListener(setListener);

        //将返回的充值记录保存
        handler1 = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.obj != null) {//如果不为空
                    //解析json数据并保存
                    String jsonStr = msg.obj.toString();
                    parseJSONWithJSONObject(jsonStr);
                } else{
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String balance = pref.getString("balance","0.00");//余额刚开始为0
        userbalance.setText(balance);

    }

    public void getBalance() {                                           //获取账户余额
        //1.从sharedPreference里面获取当前账户手机号
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);
        //Toast.makeText(getApplicationContext(),"the mobile id " + mobile,Toast.LENGTH_SHORT).show();//测试用

        //2.将用户手机号转为json
        JSONObject json=new JSONObject();
        try {
            json.put("mobile",mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //3.把手机号发送到服务器上进行查询
        String path="financialPHP/queryUserBalance.php";
        HttpJson http=new HttpJson(path,json.toString(),handler);
        new Thread(http.getHttpThread()).start();
    }

    public void getRecord(){
        //1.从sharedPreference里面获取当前账户手机号
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);

        //2.将用户手机号转为json
        JSONObject json=new JSONObject();
        try {
            json.put("mobile",mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //3.把手机号发送到服务器上进行查询
        String path="financialPHP/queryRecord.php";
        HttpJson http=new HttpJson(path,json.toString(),handler1);
        new Thread(http.getHttpThread()).start();
    }

    //解析json数据
    private void parseJSONWithJSONObject (String jsondata) {
        try {
            //？得到json数组
            JSONArray jsonArray = new JSONArray(jsondata);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String time = jsonObject.getString("datetime");
                String amount = jsonObject.getString("amount");

                //保存当前账户充值记录
                SharedPreferences.Editor recordEditor = getSharedPreferences("chargeRecord", Context.MODE_PRIVATE).edit();
                recordEditor.putString("id",id);
                recordEditor.putString("datetime",time);
                recordEditor.putString("amount",amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置的监听事件
    View.OnClickListener setListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.charge:
                    Intent intent = new Intent(getApplicationContext(),Charge.class);
                    startActivity(intent);
                    break;
                case R.id.chargeshow:
                    getRecord();
                    Intent intent1 = new Intent(getApplicationContext(),ChargeShow.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
}
