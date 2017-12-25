package com.example.foolishfan.IntelligentParking.SystemFunction.Listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by Administrator on 2017-12-25.
 */

public class ContactListener implements View.OnClickListener {
    private Context context;
    private String phone;

    public ContactListener(Context initContext,String initPhone){
        context=initContext;
        phone=initPhone;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
