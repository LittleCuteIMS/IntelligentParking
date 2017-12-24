package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.foolishfan.IntelligentParking.R;

public class RecordShowActivity extends AppCompatActivity {
    TextView chargeAmount;//充值金额
    TextView chargeTime;
    TextView chargeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_show);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String time = intent.getStringExtra("time");
        String amount = intent.getStringExtra("amount");
        chargeAmount = (TextView) findViewById(R.id.xiaofeijine) ;
        chargeAmount.setText(amount);
        chargeTime = (TextView) findViewById(R.id.xiaofei_time) ;
        chargeTime.setText(time);
        chargeId = (TextView) findViewById(R.id.xiaofei_id) ;
        chargeId.setText(id);

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.record_info_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button button = (Button) findViewById(R.id.kefu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18782003437"));
                startActivity(intent);
            }
        });
    }
}
