package com.example.foolishfan.IntelligentParking.ParkNavigation;

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

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhanglin on 2017/11/7.
 */
public class FinanceActivity extends AppCompatActivity{
    TextView userbalance;

    //接收服务器查询返回的余额信息
    private Handler handler= new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                String balance=msg.obj.toString();
                userbalance.setText(balance);//显示当前账户余额
                SharedPreferences.Editor statusEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                statusEditor.putString("balance", balance);
                statusEditor.apply();//保存当前账户余额
            } else {
                Toast.makeText(FinanceActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.finance_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBalance();

        userbalance = (TextView) findViewById(R.id.amountshow);//获取余额实例
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String balance = pref.getString("balance","0.00");//余额刚开始为0
        userbalance.setText(balance);//显示余额

        //获取按钮，设置监听事件
        Button charge=(Button)findViewById(R.id.charge);
        Button chargeShow=(Button)findViewById(R.id.chargeshow);
        charge.setOnClickListener(setListener);
        chargeShow.setOnClickListener(setListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String balance = pref.getString("balance","0.00");//重新获取当前余额的值，若不存在则为0
        userbalance.setText(balance);
    }

    public void getBalance() {
        //1.从sharedPreference里面获取当前账户手机号
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);

        //2.将用户手机号转为json格式
        JSONObject json=new JSONObject();
        try {
            json.put("mobile",mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //3.把手机号发送到服务器上进行查询
        String path="financialPHP/queryUserBalance1.php";
        HttpJson http=new HttpJson(path,json.toString(),handler);
        new Thread(http.getHttpThread()).start();
    }

    //设置的监听事件
    View.OnClickListener setListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.charge:
                    Intent intent = new Intent(FinanceActivity.this,ChargeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.chargeshow:
                    Intent intent1 = new Intent(FinanceActivity.this,ChargeShowActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };
}
