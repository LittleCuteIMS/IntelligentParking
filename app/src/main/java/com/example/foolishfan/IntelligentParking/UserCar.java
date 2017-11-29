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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserCar extends AppCompatActivity {          //用户车辆信息界面
    private Handler handler = new Handler() {//接收服务器查询返回的用户车辆信息
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                //保存当前用户车辆信息
                SharedPreferences.Editor statusEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                statusEditor.putString("carinfo", msg.obj.toString());
                statusEditor.apply();
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
            private Handler handler1;                   //登录接收服务器返回的信息
            TextView usercarinfo;

       @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_car);

            getUserCarInfo();

            usercarinfo = (TextView) findViewById(R.id.carinfo);//显示用户车辆信息
            SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
            String carinfo = pref.getString("carinfo",null);
            usercarinfo.setText(carinfo);

            //设置toolbar导航栏，设置导航按钮
            Toolbar usercar_toolbar = (Toolbar) findViewById(R.id.usercar_toolbar);
            setSupportActionBar(usercar_toolbar);
            usercar_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //将返回的用户车辆信息保存
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

        public void getUserCarInfo(){
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
            String path="user/carinfo_inquiry.php";
            HttpJson http=new HttpJson(path,json.toString(),handler);
            new Thread(http.getHttpThread()).start();
        }

             //解析Json数据
        private void parseJSONWithJSONObject (String jsondata) {
            try {

                //？得到json数组
                JSONArray jsonArray = new JSONArray(jsondata);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String plateNumber = jsonObject.getString("plate_number");

                    //保存当前用户车辆记录
                    SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                    recordEditor.putString("plate_number",plateNumber);

                }
            } catch (Exception e) {
                     e.printStackTrace();
            }
        }


}