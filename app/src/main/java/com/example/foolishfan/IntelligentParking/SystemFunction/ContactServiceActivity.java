package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.SystemFunction.Listeners.ContactListener;


/**
 * Created by CaiChuang on 2017-12-25.
 */

public class ContactServiceActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_service);
        Toolbar contact_service_toolbar = (Toolbar) findViewById(R.id.contact_service_toolbar);
        setSupportActionBar(contact_service_toolbar);
        contact_service_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String[] phones=new String[]{
                "13551374417","15520702168","18780073534","18782003437"
        };
        int[] imageViewIds=new int[]{
                R.id.contact_iv1, R.id.contact_iv2, R.id.contact_iv3, R.id.contact_iv4
        };
        for(int i=0;i<4;i++){
            ImageView mImageView=(ImageView)findViewById(imageViewIds[i]);
            ContactListener mContactListener=new ContactListener(this,phones[i]);
            mImageView.setOnClickListener(mContactListener);
        }
    }
}
