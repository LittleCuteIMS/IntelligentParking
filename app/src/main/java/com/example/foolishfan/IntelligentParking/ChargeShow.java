package com.example.foolishfan.IntelligentParking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChargeShow extends AppCompatActivity {
    private String[] data = {"1","2","3","4"};
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

        //ListView适配器:
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChargeShow.this,android.R.layout.simple_list_item_1,data);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
}
