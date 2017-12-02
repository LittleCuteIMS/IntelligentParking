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

import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;

public class UserCar extends AppCompatActivity {          //用户车辆信息界面
    TextView plateNumber;
    TextView remarks;

    protected Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {//如果不为空
                //解析json数据并保存
                String jsonStr = msg.obj.toString();
                //解析json数据，按键名存在SharedPreference中
                JSONObject jsonObject;
                String plateNumberString=null;
                try {
                    jsonObject = new JSONObject(jsonStr);
                    plateNumberString=jsonObject.getString("plateNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //保存车牌号码到手机
                SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                recordEditor.putString("plateNumber", plateNumberString);
                //将相应消息放在对应的控件显示
                SharedPreferences userPref=getSharedPreferences("user",Context.MODE_PRIVATE);
                String remarksString=userPref.getString("remarks",null);
                remarks.setText(remarksString);
                plateNumber.setText(plateNumberString);
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };


       @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_car);


            //设置toolbar导航栏，设置导航按钮
            Toolbar usercar_toolbar = (Toolbar) findViewById(R.id.usercar_toolbar);
            setSupportActionBar(usercar_toolbar);
            usercar_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

           plateNumber = (TextView) findViewById(R.id.plateNumber);//显示用户车牌号码
           remarks = (TextView) findViewById(R.id.remarks);//显示用户车辆备注

           getUserCarInfo();
        }

    public void getUserCarInfo() {
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
    //解析Json数据
    private void parseJSONWithJSONObject(String jsonData) {
        try {

            //？得到json数组
            JSONObject jsonObject = new JSONObject(jsonData);
            String plateNumber=jsonObject.getString("plateNumber");

            //保存当前用户记录
            SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
            recordEditor.putString("plateNumber", plateNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}