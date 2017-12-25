package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.foolishfan.IntelligentParking.R;


/**
 * Created by CaiChuang on 2017-12-25.
 */

public class WebActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置toolbar导航栏，设置导航按钮
        setContentView(R.layout.activity_web);
        Toolbar web_toolbar = (Toolbar) findViewById(R.id.web_toolbar);
        setSupportActionBar(web_toolbar);
        web_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebView webView=(WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        Intent intent = getIntent();// 收取 email
        Bundle bundle = intent.getBundleExtra("urlBundle");// 打开 email
        String url = bundle.getString("url");
        webView.loadUrl(url);
    }
}
