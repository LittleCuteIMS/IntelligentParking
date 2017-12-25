package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.ParkNavigation.FinanceActivity;
import com.example.foolishfan.IntelligentParking.ParkNavigation.BDMapActivity;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.Advertisement.ViewPagerAdapter;
import com.example.foolishfan.IntelligentParking.SystemFunction.MainListeners.MainNavigationItemListener;
import com.example.foolishfan.IntelligentParking.User.Login;
import com.example.foolishfan.IntelligentParking.User.AddUserCar;
import com.example.foolishfan.IntelligentParking.User.ParkingHistory;
import com.example.foolishfan.IntelligentParking.User.User;
import com.example.foolishfan.IntelligentParking.User.UserCar;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;
import com.example.foolishfan.IntelligentParking.Util.HttpJsonModified;
import com.example.foolishfan.IntelligentParking.Util.QRcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    static public boolean isLogin;//全局获取当前软件的登录状态
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private QRcode qr;
    private TextView tvNavMobile, tvNavNickname;
    //用于实现广告轮播
    private ViewPager mViewPaper;
    private List<View> dots;
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;
    //存放图片的id

    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //顶部toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //二维码初始化
        qr = new QRcode();

        //获取相关按钮
        Button addCar = (Button) findViewById(R.id.addCar);
        Button parkNearby = (Button) findViewById(R.id.parkNearby);
        Button wallet = (Button) findViewById(R.id.wallet);
        ImageButton scanImageButton = (ImageButton) findViewById(R.id.scanImageButton);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);   //创建导航试图对象
        ImageView mImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivAvatar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //实现抽屉效果左滑拉出菜单栏
        tvNavMobile = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavMobile);
        tvNavNickname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvNavNickname);

        //注册监听事件
        addCar.setOnClickListener(mainOnClick);
        parkNearby.setOnClickListener(mainOnClick);
        wallet.setOnClickListener(mainOnClick);
        mImageView.setOnClickListener(mainOnClick);
        scanImageButton.setOnClickListener(mainOnClick);
        MainNavigationItemListener navigationItemListener=new MainNavigationItemListener(this);
        navigationView.setNavigationItemSelectedListener(navigationItemListener);

        //实现侧边栏滑入滑出
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //实现广告轮播
        mViewPaper = (ViewPager) findViewById(R.id.vp);

        //显示的小点
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));

        adapter = new ViewPagerAdapter(this);
        mViewPaper.setAdapter(adapter);

        mViewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                oldPosition = position;
                currentItem = position;
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //获取登录状态
        SharedPreferences statusPreferences = getSharedPreferences("status", Context.MODE_PRIVATE);
        isLogin = statusPreferences.getBoolean("isLogin", false);

        //更改用户名显示
        if (isLogin) {
            String nickname;
            String mobile;
            SharedPreferences userPref = getSharedPreferences("user", Context.MODE_PRIVATE);
            nickname = userPref.getString("nickname", null);
            mobile = userPref.getString("mobile", null);
            tvNavNickname.setText(nickname);
            tvNavMobile.setText(mobile);
        } else {
            tvNavNickname.setText("请登录");
            tvNavMobile.setText("");
        }

        //实现广告自动轮播
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                2,
                2,
                TimeUnit.SECONDS);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawers();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
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
        switch (item.getItemId()) {
            case R.id.switchLocalhost:
                HttpJson.setWebsite("http://10.0.2.2/ParkingWeb/");
                HttpJsonModified.setWebsite("http://10.0.2.2/ParkingWeb/");
                Toast.makeText(MainActivity.this, "已设置为10.0.2.2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.switchServer:
                HttpJson.setWebsite("http://120.78.173.73/ParkingWeb/");
                HttpJsonModified.setWebsite("http://120.78.173.73/ParkingWeb/");
                Toast.makeText(MainActivity.this, "已设置为120.78.173.73", Toast.LENGTH_SHORT).show();
                break;
            case R.id.switchLAN:
                HttpJson.setWebsite("http://192.168.155.1/ParkingWeb/");
                HttpJsonModified.setWebsite("http:1//92.168.155.1/ParkingWeb/");
                Toast.makeText(MainActivity.this, "已设置为192.168.155.1", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        qr.setResult(requestCode, resultCode, data);
        if (qr.getResult() != null) {
            if (qr.getResult().getContents() == null) {
                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show();
            } else {
                qr.startBilling(MainActivity.this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //页面点击事件
    View.OnClickListener mainOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainOnClick(v);
        }
    };

    //页面按钮点击事件的方法
    private void mainOnClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivAvatar://点击头像的跳转事件
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, User.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
                break;
            case R.id.addCar://点击添加车辆的监听事件
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, AddUserCar.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.parkNearby://点击附近停车场的监听事件
                Intent intent1 = new Intent(MainActivity.this, BDMapActivity.class);
                startActivity(intent1);
                break;
            case R.id.wallet://点击我的钱包的监听事件
                if (isLogin) {
                    Intent intent = new Intent(MainActivity.this, FinanceActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scanImageButton:
                qr.scanQRcode(MainActivity.this, ScanActivity.class);
                break;
        }
    }

    //图片轮播任务
    private class ViewPageTask implements Runnable {

        @Override
        public void run() {
            currentItem = (currentItem + 1) % adapter.getImageIdsLength();
            mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mViewPaper.setCurrentItem(currentItem);
        }

        ;
    };
}

