package com.example.foolishfan.IntelligentParking.SystemFunction.Advertisement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
