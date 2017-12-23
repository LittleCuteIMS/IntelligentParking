package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.foolishfan.IntelligentParking.R.id.booking;
import static com.example.foolishfan.IntelligentParking.R.id.tothere;

public class DetailedActivity extends AppCompatActivity implements View.OnClickListener{
    private String imageID;//图片路径
    private String latitude;
    private String longitude;
    private String phone;
    ImageView imageView;
    TextView parkingName;
    TextView parkAddress;
    TextView parkMobile;
    TextView parkNumber;
    TextView freeNumber;
    TextView charge;

    //主线程创建消息处理器
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try{
                    JSONObject jsonObject = parseJSONWithJSONObject(msg.obj.toString());
                    parkingName.setText(jsonObject.getString("name"));
                    parkNumber.setText("总停车位："+jsonObject.getString("carport_sum"));
                    freeNumber.setText("空闲车位："+jsonObject.getString("carport_free_num"));
                    parkAddress.setText("地址："+jsonObject.getString("address"));
                    parkMobile.setText("联系电话："+jsonObject.getString("phone"));
                    charge.setText("收费标准："+jsonObject.getString("charge")+"元/时");
                    latitude = jsonObject.getString("latitude");
                    longitude = jsonObject.getString("longitude");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(DetailedActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //设置toolbar导航栏，设置导航按钮
        Toolbar detailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(detailToolbar);
        detailToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
       });

        imageView = (ImageView) findViewById(R.id.park_photo);
        parkingName = (TextView) findViewById(R.id.parking_name);//名称
        parkAddress = (TextView) findViewById(R.id.parking_address);//地址
        parkMobile = (TextView) findViewById(R.id.parking_phone);//电话
        parkNumber = (TextView) findViewById(R.id.parking_number); //车位
        freeNumber = (TextView) findViewById(R.id.free_number);//空闲车位
        charge = (TextView) findViewById(R.id.parking_shoufei);//收费

        Button booking = (Button) findViewById(R.id.booking);
        booking.setOnClickListener(this);
        Button tothere = (Button) findViewById(R.id.tothere);
        tothere.setOnClickListener(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        JSONObject json=new JSONObject();
        try {
            json.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String path="parkPHP/parkNameSelect.php";
        HttpJson http=new HttpJson(path,json.toString(),handler);
        new Thread(http.getHttpThread()).start();
    }

    private JSONObject parseJSONWithJSONObject(String jsonData){
        JSONObject jsonObject = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i< jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                phone = jsonObject.getString("phone");
                imageID = jsonObject.getString("image");
                Glide.with(this).load("http://192.168.155.1/ParkingWeb/parkPhoto/"+imageID).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case booking:
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:"+phone));
               startActivity(intent);
                break;
            case tothere:
                Intent intent1 = new Intent();
                intent1.setData(Uri.parse("baidumap://map/navi?location="+latitude+","+longitude));
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

}
