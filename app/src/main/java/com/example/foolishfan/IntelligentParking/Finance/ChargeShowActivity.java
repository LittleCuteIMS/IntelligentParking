package com.example.foolishfan.IntelligentParking.Finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.Data;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChargeShowActivity extends AppCompatActivity {
    private ListView listView;
    private List<Data> datas = new ArrayList<Data>();//要填充的数据

    //主线程创建消息处理器
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    //把传回来的字符串转换成json数组
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());
                    Log.d("Feedback",jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        Data data = new Data();
                        data.setImageId(jsonObject.getInt("id"));
                        data.setTime(jsonObject.getString("datetime"));
                        data.setAmount(jsonObject.getString("amount"));//传入Data类
                        datas.add(data);//添加到要填充的数据列表
                    }
                    listView.setAdapter(new MyAdapter());//传入适配器对象，和listview建立关联
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_show);
        listView = (ListView) findViewById(R.id.list_view);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.finance_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
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
            //为子项动态加载布局：若有缓存的加载好的布局则使用；否则重新加载
            if (convertView == null){
                view = View.inflate(ChargeShowActivity.this, R.layout.charge_show_item, null);
                TextView imageId = (TextView) view.findViewById(R.id.tv_id);
                TextView time = (TextView) view.findViewById(R.id.tv_name);
                TextView amount = (TextView) view.findViewById(R.id.tv_tech);
                imageId.setText(String.valueOf(datas.get(position).getImageId()));
                time.setText(datas.get(position).getTime());
                amount.setText(datas.get(position).getAmount());
            }else {
                view = convertView;
            }
            return view;
        }
    }
}
