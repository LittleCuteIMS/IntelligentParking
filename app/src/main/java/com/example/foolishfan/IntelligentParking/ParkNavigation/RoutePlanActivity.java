package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.DrivingRouteOverlay;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.OverlayManager;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.TransitRouteOverlay;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.WalkingRouteOverlay;

public class RoutePlanActivity extends Activity implements BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {
    private MapView mMapView = null;//地图对象
    private BaiduMap mBaidumap = null;//百度地图对象

    //关于定位的参数
    public LocationClient mLocClient;//定位类
    private boolean isFirstLoc = true;// 是否首次定位
    public MyLocationListenner myListener = new MyLocationListenner();//定位监听器
    private double myLongitude; //纬度
    private double myLatitude;  //经度
    private double latitude;//停车场经度
    private double longitude;//停车场纬度

    //浏览路线节点相关
    Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    int nodeIndex = -1;//节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;//泡泡view

    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocClient = new LocationClient(getApplicationContext());//创建实例，构造函数接收一个Context参数
        mLocClient.registerLocationListener(myListener);//注册一个定位监听器，获取到位置信息则回调这个定位监听器
        setContentView(R.layout.activity_routeplan);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude",0);

        //初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBaidumap.setMyLocationEnabled(true);//开启移动定位功能

        // 定位初始化
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        //设置toolbar导航栏，设置导航按钮
        Toolbar map_toolbar = (Toolbar) findViewById(R.id.map1_toolbar);
        //setSupportActionBar(map_toolbar);
        map_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        //地图点击事件处理
        mBaidumap.setOnMapClickListener(this);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        Button button_drive = (Button) findViewById(R.id.drive) ;
        button_drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchButtonProcessDrive(mMapView);
            }
        });

        Button button_bus = (Button) findViewById(R.id.transit) ;
        button_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchButtonProcessBus(mMapView);
            }
        });

        Button button_walk = (Button) findViewById(R.id.walk) ;
        button_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchButtonProcessWalk(mMapView);
            }
        });
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaidumap.animateMapStatus(u);
            }
            //取经纬度
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) return;
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude()) .longitude(location.getLongitude()).build();
            mBaidumap.setMyLocationData(locData);
        }

    }

    /**
     * 发起路线规划搜索示例
     */
    public void SearchButtonProcessDrive(View v) {
        //重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        PlanNode stNode = PlanNode.withLocation(new LatLng(myLatitude, myLongitude));
        PlanNode enNode = PlanNode.withLocation(new LatLng(latitude,longitude)); //如果使用服务器传递数据将shop的经纬度替换
        DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
        mSearch.drivingSearch((drivingOption).from(stNode).to(enNode));// 发起驾车路线规划
    }
    public void SearchButtonProcessBus(View v) {
        //重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        PlanNode stNode = PlanNode.withLocation(new LatLng(myLatitude, myLongitude));
        PlanNode enNode = PlanNode.withLocation(new LatLng(latitude,longitude)); //如果使用服务器传递数据将shop的经纬度替换
        mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city("成都").to(enNode));// 发起公交路线规划
    }
    public void SearchButtonProcessWalk(View v) {
        //重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        PlanNode stNode = PlanNode.withLocation(new LatLng(myLatitude, myLongitude));
        PlanNode enNode = PlanNode.withLocation(new LatLng(latitude,longitude)); //如果使用服务器传递数据将shop的经纬度替换
        mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));// 发起公交路线规划
    }

    /**
     * 节点浏览示例
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        //设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        //获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        //移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RoutePlanActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation,  -47));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            int totalLine = result.getRouteLines().size();
            Toast.makeText(RoutePlanActivity.this, "共查询出" + totalLine + "条符合条件的线路，"+"已经为您规划最优路线。", Toast.LENGTH_SHORT).show();
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {}
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {}
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {}

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            int totalLine = result.getRouteLines().size();
            Toast.makeText(RoutePlanActivity.this, "共查询出" + totalLine + "条符合条件的线路，"+"已经为您规划最优路线。", Toast.LENGTH_SHORT).show();
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    //驾车路线结果回调 查询的结果可能包括多条驾车路线方案
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        mBaidumap.clear();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            int totalLine = result.getRouteLines().size();
            Toast.makeText(RoutePlanActivity.this, "共查询出" + totalLine + "条符合条件的线路，"+"已经为您规划最优路线。", Toast.LENGTH_SHORT).show();
            overlay.setData(result.getRouteLines().get(0));// 设置一条最近的驾车路线方案
            overlay.addToMap();
            overlay.zoomToSpan();
        }
}

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }
        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }
        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }
        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }

}
