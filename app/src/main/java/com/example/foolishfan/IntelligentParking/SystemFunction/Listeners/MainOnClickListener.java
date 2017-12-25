package com.example.foolishfan.IntelligentParking.SystemFunction.Listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.ParkNavigation.BDMapActivity;
import com.example.foolishfan.IntelligentParking.ParkNavigation.FinanceActivity;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.MainActivity;
import com.example.foolishfan.IntelligentParking.SystemFunction.ScanActivity;
import com.example.foolishfan.IntelligentParking.User.AddUserCar;
import com.example.foolishfan.IntelligentParking.User.Login;
import com.example.foolishfan.IntelligentParking.User.User;
import com.example.foolishfan.IntelligentParking.Util.QRcode;

/**
 * Created by CaiChuang on 2017-12-25.
 */

public class MainOnClickListener implements View.OnClickListener {
    private Context context;
    private QRcode qr;

    public MainOnClickListener(Context initContext){
        context=initContext;
    }

    public void getQR(QRcode mQR){
        qr=mQR;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAvatar://点击头像的跳转事件
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, User.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, Login.class);
                    context.startActivity(intent);
                }
                break;
            case R.id.addCar://点击添加车辆的监听事件
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, AddUserCar.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.parkNearby://点击附近停车场的监听事件
                Intent intent1 = new Intent(context, BDMapActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.wallet://点击我的钱包的监听事件
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, FinanceActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scanImageButton:
                qr.scanQRcode((Activity) context, ScanActivity.class);
                break;
        }
    }
}
