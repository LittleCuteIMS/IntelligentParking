package com.example.foolishfan.IntelligentParking;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BDMapActivity extends AppCompatActivity {
    public LocationClient mLocationClient;//定位类
    private MapView mapView;//地图对象
    private BaiduMap baiduMap;//百度地图对象
    private boolean isFirstLocate = true;//如果是第一次定位的话要将自己的位置显示在地图中间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());//创建实例，构造函数接收一个Context参数
        mLocationClient.registerLocationListener(new MyLocationListener());//注册一个定位监听器，获取到位置信息则回调这个定位监听器
        SDKInitializer.initialize(getApplicationContext());//初始化操作要在setContentView()之前调用？
        setContentView(R.layout.activity_bdmap);

        mapView = (MapView) findViewById(R.id.bmapview);//获取地图实例
        baiduMap = mapView.getMap();//获取BaiduMap的实例
        baiduMap.setMyLocationEnabled(true);//开启移动定位功能

        //设置toolbar导航栏，设置导航按钮
        Toolbar finance_toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(finance_toolbar);
        finance_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //在运行时一次申请3个权限
        List<String> permissionList = new ArrayList<>();
        //判断没有被授权则添加到List集合
        if (ContextCompat.checkSelfPermission(BDMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BDMapActivity.this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BDMapActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //将List转换成数组
        if (!permissionList.isEmpty()){
            String[] permissons = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BDMapActivity.this,permissons,1);//一次性申请权限
        }else {
            requestLocation();
        }
    }

    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation ){
                navigateTo(bdLocation);
            }
        }
    }

    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());//LatLng是用于存放经纬度值的类，接收两个参数：维度，经度

            //创建一个新的MapStatus
            MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(16).build();
            mapView.removeViewAt(1);// 不显示百度地图Logo
            mapView.showZoomControls(false);// 不显示缩放比例尺

            //利用MapStatus构建一个MapStatusUpdate对象
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            //更新BaiduMap，此时BaiduMap的界面就会从初始位置（北京），移动到定位点
            baiduMap.setMapStatus(mapStatusUpdate);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();//MyLocationData.Builder类用来封装设备当前位置
        locationBuilder.latitude(location.getLatitude());//传入经纬度
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();//信息封装完毕后调用build()方法
        baiduMap.setMyLocationData(locationData);//在地图上显示当前位置

    }

    private void  requestLocation(){
        initLocation();
        mLocationClient.start();//开始定位，定位结果返回定位监听器
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(10000);//设置更新的时间间隔
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        showMarkers();//显示停车场位置图标
    }

    private void showMarkers(){
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);//构建Marker图标
        final LatLng point1 = new LatLng(30.638251183288837,104.08398602054602);//设置停车场的经纬度
        OverlayOptions option = new MarkerOptions().position(point1).icon(bitmap); //构建MarkerOption，用于在地图上添加Marker
        baiduMap.addOverlay(option);//在地图上添加Marker，并显示
        //设置点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()  {
            @Override
            public boolean onMarkerClick(final Marker marker)  {
                TextView textView = new TextView(getApplication());//创建InfoWindow展示的view
                textView.setBackgroundResource(R.color.colorHalftransparent);//设置InfoWindow的背景半透明
                textView.setText("四川大学停车场 ");
                InfoWindow mInfoWindow = new InfoWindow(textView, point1, 80);//创建InfoWindow , 传入view，地理坐标，y轴偏移量
                baiduMap.showInfoWindow(mInfoWindow);//显示InfoWindow

                ImageView imageView = (ImageView) findViewById(R.id.photo);
                imageView.setImageResource(R.drawable.advanse1);

                TextView textView1 = (TextView) findViewById(R.id.tohere);
                textView1.setBackgroundColor(Color.WHITE);
                textView1.setText("到这里去");

                TextView textView2 = (TextView) findViewById(R.id.parking_name);
                textView2.setBackgroundColor(Color.WHITE);
                textView2.setText("名称：");
                textView2.append(textView.getText());

                TextView textView3 = (TextView) findViewById(R.id.parking_addr);
                textView3.setBackgroundColor(Color.WHITE);
                textView3.setText("地址：成都市武侯区一环路南一段");
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        baiduMap.setMyLocationEnabled(true); //开启定位
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        baiduMap.setMyLocationEnabled(false);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();//停止定位
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    //必须同意全部权限才能开始地理位置定位
                    for (int result:grantResults){
                        if (result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_parkings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.parkings:
                Intent intent = new Intent(BDMapActivity.this,ParkingsDetailsActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }
}
