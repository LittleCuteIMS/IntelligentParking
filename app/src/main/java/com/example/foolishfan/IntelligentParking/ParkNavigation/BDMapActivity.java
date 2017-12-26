package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.ParkingsData;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BDMapActivity extends AppCompatActivity implements  View.OnClickListener{
    public LocationClient mLocationClient;//定位类
    private MapView mapView;//地图对象
    private BaiduMap baiduMap;//百度地图对象
    private boolean isFirstLocate = true;//如果是第一次定位的话要将自己的位置显示在地图中间
    private double localLatitude;//当前经度
    private double localLongitude;//当前维度

    //接收返回的停车场的经纬度
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    //把传回来的字符串转换成json数组
                    JSONArray jsonArray = new JSONArray(msg.obj.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//解析为json对象
                        final ParkingsData parkingsData = new ParkingsData();
                        //得到每一个停车场的经度、维度、名称
                        parkingsData.setLatitude(jsonObject.getDouble("latitude"));
                        parkingsData.setLongitude(jsonObject.getDouble("longitude"));
                        parkingsData.setName(jsonObject.getString("name"));
                        parkingsData.setImageId(jsonObject.getString("image"));
                        parkingsData.setcarportNumber(jsonObject.getInt("carport_sum"));
                        parkingsData.setFreeNumber(jsonObject.getInt("carport_free_num"));
                        parkingsData.setCharge(jsonObject.getDouble("charge"));
                        parkingsData.setPhone(jsonObject.getString("phone"));
                        initOverlay(parkingsData);//将每一个停车场在地图上标注出来
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(BDMapActivity.this, "没有查询到附近停车场", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());//创建实例，构造函数接收一个Context参数
        mLocationClient.registerLocationListener(new MyLocationListener());//注册一个定位监听器，获取到位置信息则回调这个定位监听器
        SDKInitializer.initialize(getApplicationContext());//初始化操作要在setContentView()之前调用
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

        String path="parkPHP/parkAddressSelect.php";
        HttpJson http=new HttpJson(path,"",handler);
        new Thread(http.getHttpThread()).start();

        Button btnAddr = (Button) findViewById(R.id.btnAddr);
        btnAddr.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddr:
                Intent intent = new Intent(BDMapActivity.this,PoiSearchActivity.class);
                intent.putExtra("localLatitude",localLatitude);
                intent.putExtra("localLongitude",localLongitude);
                startActivity(intent);
                break;
            default:
                break;
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
            localLatitude = location.getLatitude();
            localLongitude = location.getLongitude();
            LatLng ll = new LatLng(localLatitude,localLongitude);//LatLng是用于存放经纬度值的类，接收两个参数：维度，经度
            //创建一个新的MapStatus
            MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(15).build();
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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(10000);//设置更新的时间间隔
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        // 当前地点修改为自定义marker
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker, 0xAAFFFF88, 0xAA00FF00));
    }

    public void initOverlay(final ParkingsData parkingsData) {
        BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);//构建Marker图标
        final LatLng llA = new LatLng(parkingsData.getLatitude(),parkingsData.getLongitude());//设置停车场的经纬度
        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true); //构建MarkerOption，用于在地图上添加Marker
        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);// 掉下动画
        final Marker mMarkerA = (Marker) (baiduMap.addOverlay(ooA));//在地图上添加Marker，并显示
        //设置点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()  {
            @Override
            public boolean onMarkerClick (final Marker marker)  {
                AlertDialog.Builder builder = new AlertDialog.Builder(BDMapActivity.this);
                builder.setIcon(R.drawable.icons6);//    设置Title的图标
                if (marker == mMarkerA) {
                    builder.setTitle("您将要去："+parkingsData.getName());//    设置Title的内容
                    builder.setNegativeButton("查看路线", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(BDMapActivity.this,RoutePlanActivity.class);
                            intent.putExtra("latitude",parkingsData.getLatitude());
                            intent.putExtra("longitude",parkingsData.getLongitude());
                            startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("立刻前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(BDMapActivity.this,NavigationActivity.class);
                            intent.putExtra("parkLatitude",parkingsData.getLatitude());
                            intent.putExtra("parkLongitude",parkingsData.getLongitude());
                            intent.putExtra("parkName",parkingsData.getName());
                            intent.putExtra("parkImage",parkingsData.getImageId());
                            intent.putExtra("parkNumber",parkingsData.getCarportNumber());
                            intent.putExtra("parkFreeNumber",parkingsData.getFreeNumber());
                            intent.putExtra("charge",parkingsData.getCharge());
                            intent.putExtra("phone",parkingsData.getPhone());
                            intent.putExtra("localLatitude",localLatitude);
                            intent.putExtra("localLongitude",localLongitude);
                            startActivity(intent);
                        }
                    });
                    builder.show();// 显示出该对话框
                }
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


}
