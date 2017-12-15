package com.example.foolishfan.IntelligentParking.Finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;

public class ChargeActivity extends AppCompatActivity {
    private EditText editText = null;//获取用户充值金额
    private Handler handler;//保存服务器返回消息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.finance_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = (EditText) findViewById(R.id.charge_amount);
        //四个用来选择充值金额的按钮
        Button button1 = (Button) findViewById(R.id.amount_button1);
        Button button2 = (Button) findViewById(R.id.amount_button2);
        Button button3 = (Button) findViewById(R.id.amount_button3);
        Button button4 = (Button) findViewById(R.id.amount_button4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "10";
                editText.setText(str.toCharArray(), 0, str.length());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "50";
                editText.setText(str.toCharArray(), 0, str.length());
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "100";
                editText.setText(str.toCharArray(), 0, str.length());
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "200";
                editText.setText(str.toCharArray(), 0, str.length());
            }
        });

        //获取选择充值的方式
        //单选框的使用目前有一些问题

        //点击充值按钮
        Button chargebutton = (Button) findViewById(R.id.charge);
        chargebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ! TextUtils.isEmpty(editText.getText()) ){//判断是否有输入值
                    //从sharedPreference里面获取当前账户手机号
                    SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
                    String mobile = pref.getString("mobile", null);
                    //找到充值金额
                    String amount = editText.getText().toString();
                    //将数据转为Json格式
                    JSONObject json=new JSONObject();
                    try {
                        json.put("mobile",mobile);
                        json.put("amount",amount);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //连接服务器
                    String path="financialPHP/recharge.php";
                    HttpJson http=new HttpJson(path,json.toString(),handler);
                    new Thread(http.getHttpThread()).start();

                    AlertDialog.Builder builder  = new AlertDialog.Builder(ChargeActivity.this);
                    builder.setTitle("提示：" ) ;
                    builder.setMessage("已提交充值信息，请稍后查看余额！" ) ;
                    builder.setPositiveButton("好的" ,  null );
                    builder.show();
                }else {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(ChargeActivity.this);
                    builder.setTitle("提示：" ) ;
                    builder.setMessage("请输入充值金额！" ) ;
                    builder.setPositiveButton("好的" ,  null );
                    builder.show();
                }
            }
        });

        //处理用户返回的信息
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj != null) {//如果不为空
                    //保存当前账户余额
                    SharedPreferences.Editor statusEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                    statusEditor.putString("balance", msg.obj.toString());
                    statusEditor.apply();
                } else {
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }

}
