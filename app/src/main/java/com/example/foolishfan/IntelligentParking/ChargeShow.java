package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChargeShow extends AppCompatActivity {
    private String[] data = {"1","2","3","4"};

    //接收服务器查询返回的充值记录信息
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.obj != null) {
                String jsonStr = msg.obj.toString();
                parseJSONWithJSONObject(jsonStr);//解析json数据并保存
            } else{
                Toast.makeText(ChargeShow.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_show);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.finance_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button button = (Button) findViewById(R.id.start_query);//获取按钮实例
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecord();
            }
        });



        //ListView适配器:
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChargeShow.this,android.R.layout.simple_list_item_1,data);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //to do
            }
        });
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
            //把传回来的字符串转换成json数组
            JSONArray jsonArray = new JSONArray(jsondata);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象

                String id = jsonObject.getString("id");
                Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();
                String time = jsonObject.getString("datetime");
                Toast.makeText(getApplicationContext(),time,Toast.LENGTH_SHORT).show();
                String amount = jsonObject.getString("amount");
                Toast.makeText(getApplicationContext(),amount,Toast.LENGTH_SHORT).show();

                /*不应该保存，应该将数据填充
                SharedPreferences.Editor recordEditor = getSharedPreferences("chargeRecord", Context.MODE_PRIVATE).edit();
                recordEditor.putString("id",id);
                recordEditor.putString("datetime",time);
                recordEditor.putString("amount",amount);
                recordEditor.apply();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
