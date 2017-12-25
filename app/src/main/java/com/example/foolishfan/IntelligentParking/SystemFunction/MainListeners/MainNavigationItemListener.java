package com.example.foolishfan.IntelligentParking.SystemFunction.MainListeners;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.MainActivity;
import com.example.foolishfan.IntelligentParking.SystemFunction.MessageCenterActivity;
import com.example.foolishfan.IntelligentParking.SystemFunction.SoftwareSetActivity;
import com.example.foolishfan.IntelligentParking.SystemFunction.SuggestionActivity;
import com.example.foolishfan.IntelligentParking.User.ParkingHistory;
import com.example.foolishfan.IntelligentParking.User.UserCar;


/**
 * Created by CaiChuang on 2017-12-25.
 */

public class MainNavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;

    public MainNavigationItemListener(Context initContext){
        context=initContext;
    }
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_history://停车历史
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, ParkingHistory.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_car://我的车辆
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, UserCar.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_message://消息中心
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, MessageCenterActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_suggestion://意见反馈
                if (MainActivity.isLogin) {
                    Intent intent = new Intent(context, SuggestionActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "未登录，请先登录！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_setting://设置
                Intent intent = new Intent(context, SoftwareSetActivity.class);
                context.startActivity(intent);
                break;

        }
        return true;

    }
}
