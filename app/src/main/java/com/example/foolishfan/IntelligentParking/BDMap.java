package com.example.foolishfan.IntelligentParking;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BDMap extends AppCompatActivity {
    public LocationClient mLocationClient;//定位类
    private MapView mapView;//地图对象
    private BaiduMap baiduMap;//百度地图对象
    private boolean isFirstLocate = true;

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
        //在运行时一次申请3个权限
        List<String> permissionList = new ArrayList<>();
        //判断没有被授权则添加到List集合
        if (ContextCompat.checkSelfPermission(BDMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BDMap.this, android.Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BDMap.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //将List转换成数组
        if (!permissionList.isEmpty()){
            String[] permissons = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BDMap.this,permissons,1);//一次性申请权限
        }else {
            requestLocation();
        }
    }

    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());//LatLng是用于存放经纬度值的类，接收两个参数：维度，经度

            //创建一个新的MapStatus
            MapStatus mapStatus = new MapStatus.Builder()
                    //定位到定位点
                    .target(ll)
                    //决定缩放的尺寸
                    .zoom(16)
                    .build();
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

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation ){
                navigateTo(bdLocation);
            }
        }
    }
}
