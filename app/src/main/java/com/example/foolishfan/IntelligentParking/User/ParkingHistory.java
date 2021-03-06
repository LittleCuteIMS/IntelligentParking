package com.example.foolishfan.IntelligentParking.User;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Created by RongYuting on 2017/12/3.
 */
public class ParkingHistory extends AppCompatActivity{

    private ListView lv;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//设置数据源或者说数据集合


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkinghistory);
        lv = (ListView) findViewById(R.id.listview);

        getData();

        //设置toolbar导航栏，设置导航按钮
        Toolbar parking_toolbar = (Toolbar) findViewById(R.id.parking_toolbar );
        setSupportActionBar(parking_toolbar);
        parking_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //从sharedPreference中获取数据并发送到服务器进行查询
    private void getData(){
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
        String path="parkPHP/parking_record_select.php";
        HttpJson http=new HttpJson(path,json.toString(),handler);
        new Thread(http.getHttpThread()).start();
    }

    //将返回的json数据进行转化
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    //把传回来的字符串转换成json数组
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("plate_number",jsonObject.getString("plate_number"));
                        map.put("park_id",jsonObject.getString("park_id"));
                        map.put("in_datetime",jsonObject.getString("in_datetime"));
                        map.put("out_datetime",jsonObject.getString("out_datetime"));
                        list.add(map);
                    }
                    lv.setAdapter(new RecordAdapter());//传入适配器对象，和listview建立关联
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    };

    //自定义适配器RecordAdapter,继承BaseAdapter
    class RecordAdapter extends BaseAdapter {
        @Override
        public int getCount() {                 //必填，是渲染的行数
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null){
                // view = View.inflate(ParkingHistory.this, R.layout.parkinghistory_item, null);
            view = LayoutInflater.from(ParkingHistory.this).inflate(R.layout.parkinghistory_item, parent,false);

                   TextView plateNumber = (TextView) view.findViewById(R.id.lv_plate);
                    TextView parkId= (TextView) view.findViewById(R.id.lv_parkId);
                    TextView inTime = (TextView) view.findViewById(R.id.lv_intime);
                    TextView outTime = (TextView) view.findViewById(R.id.lv_outtime);
                    Map maplist = list.get(position);
                    plateNumber.setText((String)maplist.get("plate_number"));
                    parkId.setText((String)maplist.get("park_id"));
                    inTime.setText((String)maplist.get("in_datetime"));
                    outTime.setText((String)maplist.get("out_datetime"));

           }else {
                view = convertView;
            }
            return view;
        }
    }
}


