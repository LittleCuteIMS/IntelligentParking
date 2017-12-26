package com.example.foolishfan.IntelligentParking.ParkNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
import com.example.foolishfan.IntelligentParking.ParkNavigation.Util.BNEventHandler;

public class GuideActivity extends AppCompatActivity {
    private final String TAG = GuideActivity.class.getName();
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private final static String RET_COMMON_MODULE = "module.ret";
    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;

    //对于导航模块有两种方式来实现发起导航。 1：使用通用接口来实现 2：使用传统接口来实现
    private boolean useCommonInterface = true;// 使用通用接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationActivity.activityList.add(this);
        //createHandler();
        View view = null;
        if (useCommonInterface) {
            //使用通用接口
            mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
                    NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
                    BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onCreate();
                view = mBaiduNaviCommonModule.getView();
            }

        } else {
            //使用传统接口
            view = BNRouteGuideManager.getInstance().onCreate(this,mOnNavigationListener);
        }
        if (view != null) {
            setContentView(view);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(NavigationActivity.ROUTE_PLAN_NODE);
            }
        }

        BNEventHandler.getInstance().getDialog(this);
        BNEventHandler.getInstance().showDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onResume();
            }
        } else {
            BNRouteGuideManager.getInstance().onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onPause();
            }
        } else {
            BNRouteGuideManager.getInstance().onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onDestroy();
            }
        } else {
            BNRouteGuideManager.getInstance().onDestroy();
        }
        NavigationActivity.activityList.remove(this);
        BNEventHandler.getInstance().disposeDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStop();
            }
        } else {
            BNRouteGuideManager.getInstance().onStop();
        }
    }

    @Override
    public void onBackPressed() {//此处onBackPressed传递false表示强制退出，true表示返回上一级，非强制退出
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onBackPressed(true);
            }
        } else {
            BNRouteGuideManager.getInstance().onBackPressed(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStart();
            }
        } else {
            BNRouteGuideManager.getInstance().onStart();
        }
    }

    private BNRouteGuideManager.OnNavigationListener mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {
        @Override
        public void onNaviGuideEnd() {
            //退出导航
            finish();
        }
        @Override
        public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
            if (actionType == 0) {
                Log.i(TAG, "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");//导航到达目的地 自动退出
            }
            Log.i(TAG, "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 + "obj:" + obj.toString());
        }
    };

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
            }
        } else {
            BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        }
    };

    private interface RouteGuideModuleConstants {
        final static int METHOD_TYPE_ON_KEY_DOWN = 0x01;
        final static String KEY_TYPE_KEYCODE = "keyCode";
        final static String KEY_TYPE_EVENT = "event";
    }



    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if(useCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                Bundle mBundle = new Bundle();
                mBundle.putInt(RouteGuideModuleConstants.KEY_TYPE_KEYCODE, keyCode);
                mBundle.putParcelable(RouteGuideModuleConstants.KEY_TYPE_EVENT, event);
                mBaiduNaviCommonModule.setModuleParams(RouteGuideModuleConstants.METHOD_TYPE_ON_KEY_DOWN, mBundle);
                try {
                    Boolean ret = (Boolean)mBundle.get(RET_COMMON_MODULE);
                    if(ret) {
                        return true;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }




}
