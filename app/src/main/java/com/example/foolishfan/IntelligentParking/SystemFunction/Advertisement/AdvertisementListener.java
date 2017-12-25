package com.example.foolishfan.IntelligentParking.SystemFunction.Advertisement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.foolishfan.IntelligentParking.SystemFunction.WebActivity;

/**
 * Created by Administrator on 2017-12-25.
 */

public class AdvertisementListener implements View.OnClickListener {
    private Context context;
    private String url;

    public AdvertisementListener(Context initContext,String initUrl){
        context=initContext;
        url=initUrl;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        intent.putExtra("urlBundle",bundle);
        context.startActivity(intent);
        /*intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);*/
    }
}
