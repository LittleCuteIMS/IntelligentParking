package com.example.foolishfan.IntelligentParking;

/**
 * Created by LiangJiacheng on 2017/11/19 0019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.Util.CarRecord;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserCar extends AppCompatActivity {          //用户车辆信息界面
    private ListView listView;
    private List<CarRecord> datas = new ArrayList<CarRecord>();//要填充的数据

    //主线程创建消息处理器
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    //把传回来的字符串转换成json数组
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        CarRecord carrecord = new CarRecord();
                        carrecord.setPlateNumber(jsonObject.getString("plateNumber"));
                        carrecord.setRemarks(jsonObject.getString("remarks"));//传入CarRecord类
                        datas.add(carrecord);//添加到要填充的数据列表
                    }
                    listView.setAdapter(new MyAdapter());//传入适配器对象，和listview建立关联
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car);
        listView = (ListView) findViewById(R.id.list_view);
        select();//获取数据

        //设置toolbar导航栏，设置导航按钮
        Toolbar usercar_toolbar = (Toolbar) findViewById(R.id.usercar_toolbar);
        setSupportActionBar(usercar_toolbar);
        usercar_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void select() {
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
        String path = "user/carinfo_inquiry.php";
        HttpJson http = new HttpJson(path, json.toString(), handler);
        new Thread(http.getHttpThread()).start();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            //为子项动态加载布局：若有缓存的加载好的布局则使用；否则重新加载
            if (convertView == null) {
                view = View.inflate(UserCar.this, R.layout.activity_user_car_item, null);
                TextView plateNumber = (TextView) view.findViewById(R.id.tv_plateNumber);
                TextView remarks = (TextView) view.findViewById(R.id.tv_remarks);
                plateNumber.setText(datas.get(position).getPlateNumber());
                remarks.setText(datas.get(position).getRemarks());
            } else {
                view = convertView;
            }
            return view;
        }
    }
}
