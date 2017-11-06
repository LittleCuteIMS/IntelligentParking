package com.example.foolishfan.IntelligentParking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);   //ButterKnife的绑定注解，找到参数对应的View，并自动的进行转换
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //导航控件
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //实现抽屉效果左滑拉出菜单栏
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);   //注册监听
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);   //创建导航试图对象
        navigationView.setNavigationItemSelectedListener(this);

        mImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        mImageView.setOnClickListener(this);

        //ButterKnife.bind(this);   //进阶的ButterKnife的绑定注解，目前还不会用
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 动态加载右键菜单
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toast toast1;

        //noinspection SimplifiableIfStatement
        if (id == R.id.switchLocalhost) {
            HttpJson.setWebsite("http://10.0.2.2/ParkingWeb/");
            toast1 = Toast.makeText(getApplicationContext(), "已设置为10.0.2.2", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.BOTTOM, 0, 0);
            toast1.show();
        }
        if (id == R.id.switchServer) {
            HttpJson.setWebsite("http://120.78.173.73/ParkingWeb/");
            toast1 = Toast.makeText(getApplicationContext(), "已设置为120.78.173.73", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.BOTTOM, 0, 0);
            toast1.show();
        }
        if (id == R.id.switchLAN) {
            HttpJson.setWebsite("http://192.168.155.1/ParkingWeb/");
            toast1 = Toast.makeText(getApplicationContext(), "已设置为192.168.155.1", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.BOTTOM, 0, 0);
            toast1.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    //设置点击左边菜单栏每一个选项的回应方式
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String string = null;
        switch (id){
            case R.id.nav_me:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_friend:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_message:
                break;
            case R.id.nav_night:
                break;
            case R.id.nav_notification:
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_suggestion:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivAvatar){
            Intent intent = new Intent(this,Login.class);   //若点击用户登录则切换MainActivity至Login Activity
            startActivity(intent);
        }
    }

}

