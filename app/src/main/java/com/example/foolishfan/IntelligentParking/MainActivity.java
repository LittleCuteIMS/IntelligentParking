package com.example.foolishfan.IntelligentParking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static private boolean isLogin;//全局获取当前软件的登录状态
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取登录状态
        SharedPreferences statusPreferences=getSharedPreferences("status",Context.MODE_PRIVATE);
        isLogin=statusPreferences.getBoolean("isLogin",false);

        //获取相关按钮
        Button addCar=(Button)findViewById(R.id.addCar);
        Button parkNearby=(Button)findViewById(R.id.parkNearby);
        Button wallet=(Button)findViewById(R.id.wallet);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);   //创建导航试图对象
        ImageView mImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//导航栏
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //实现抽屉效果左滑拉出菜单栏

        //注册监听事件
        addCar.setOnClickListener(mainOnClick);
        parkNearby.setOnClickListener(mainOnClick);
        wallet.setOnClickListener(mainOnClick);
        mImageView.setOnClickListener(mainOnClick);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 动态加载右键菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //实现切换网络连接
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.switchLocalhost) {
            HttpJson.setWebsite("http://10.0.2.2/ParkingWeb/");
            Toast.makeText(getApplicationContext(), "已设置为10.0.2.2", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.switchServer) {
            HttpJson.setWebsite("http://120.78.173.73/ParkingWeb/");
            Toast.makeText(getApplicationContext(), "已设置为120.78.173.73", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.switchLAN) {
            HttpJson.setWebsite("http://192.168.155.1/ParkingWeb/");
            Toast.makeText(getApplicationContext(), "已设置为192.168.155.1", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    //设置点击左边菜单栏每一个选项的回应方式
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_me://停车历史
                break;
            case R.id.nav_message://我的车辆
                break;
            case R.id.nav_friend://消息中心
                break;
            case R.id.nav_notification://我的收藏
                break;
            case R.id.nav_manage://应用管理
                break;
            case R.id.nav_night://夜间模式
                break;
            case R.id.nav_suggestion://意见反馈
                break;
            case R.id.nav_setting://设置
                Intent intent=new Intent(MainActivity.this,SoftwareSet.class);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    //页面点击事件
    View.OnClickListener mainOnClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id=v.getId();
            switch (id){
                case R.id.ivAvatar://点击头像的跳转事件
                    if(isLogin){
                        Intent intent=new Intent(getApplicationContext(),User.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.addCar://点击添加车辆的监听事件
                    break;
                case R.id.parkNearby://点击附近停车场的监听事件
                    break;
                case R.id.wallet://点击我的钱包的监听事件
                    break;
            }
        }
    };
}

