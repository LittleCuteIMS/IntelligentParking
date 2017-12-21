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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.Data;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.LoadListView;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChargeShowActivity extends AppCompatActivity implements LoadListView.IloadListener{
    private LoadListView listView;
    private List<Data> datas = new ArrayList<Data>();//要填充的数据
    private int count = 0;//记录分页显示的记录id号
    private int lenth;//返回数据库记录的总长度

    //主线程创建消息处理器
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());//把传回来的字符串转换成json数组
                    datas.clear();
                    lenth = jsonArray.length();
                    for (int i = 0; i < lenth ; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        if ( i<count+5){
                            Data data = new Data();
                            data.setId(jsonObject.getString("id"));
                            data.setTime(jsonObject.getString("datetime"));
                            data.setAmount(jsonObject.getString("amount")+"元");//传入Data类
                            datas.add(data);//添加到要填充的数据列表
                        }
                    }
                    listView.setAdapter(new MyAdapter());//传入适配器对象，和listview建立关联
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(ChargeShowActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        };
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

        listView = (LoadListView) findViewById(R.id.list_view);
        listView.setInterface(this);//将接口传进来
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data data = datas.get(position);
                Intent intent = new Intent(ChargeShowActivity.this,RecordShowActivity.class);
                intent.putExtra("id",data.getId());
                intent.putExtra("time",data.getTime());
                intent.putExtra("amount",data.getAmount());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        select();//获取数据
    }

    private void select(){
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
        HttpJson http=new HttpJson(path,json.toString(),handler);
        new Thread(http.getHttpThread()).start();
    }

    @Override
    public void onLoad() {
        // 刷新太快 所以新启一个线程延迟两秒
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (count<lenth){
                    count = count+5;
                    select();
                }else {
                    Toast.makeText(ChargeShowActivity.this,"全部加载完毕，点击回到屏幕上方",Toast.LENGTH_SHORT).show();
                }
                listView.loadComplete();// 通知listview加载完毕
            }
        }, 2000);
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {return datas.size();}
        @Override
        public Object getItem(int position) {return datas.get(position);}
        @Override
        public long getItemId(int position) {return position;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            view = View.inflate(ChargeShowActivity.this, R.layout.charge_show_item, null);
            TextView imageId = (TextView) view.findViewById(R.id.tv_id);
            TextView time = (TextView) view.findViewById(R.id.tv_name);
            TextView amount = (TextView) view.findViewById(R.id.tv_tech);
            imageId.setText(String.valueOf(datas.get(position).getId()));
            time.setText(datas.get(position).getTime());
            amount.setText(datas.get(position).getAmount());
            return view;
        }
    }
}
