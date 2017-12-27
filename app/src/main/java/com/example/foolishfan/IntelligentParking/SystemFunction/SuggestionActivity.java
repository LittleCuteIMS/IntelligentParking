package com.example.foolishfan.IntelligentParking.SystemFunction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 蔡创
 * 功能：意见反馈
 * 日期：2017.12.12
 */
public class SuggestionActivity extends AppCompatActivity {

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj!=null){
                Toast.makeText(SuggestionActivity.this, R.string.send_success, Toast.LENGTH_SHORT).show();
                EditText suggestionEdTxt=(EditText)findViewById(R.id.suggestionEdTxt);
                suggestionEdTxt.setText("");
            }else{
                Toast.makeText(SuggestionActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        final EditText suggestionEdTxt=(EditText)findViewById(R.id.suggestionEdTxt);
        suggestionEdTxt.addTextChangedListener(new TextWatcher() {
            String tmp = "";
            String digits = "/\\:*?<>|\"\n\t";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tmp = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suggestionEdTxt.setSelection((s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if(str.equals(tmp)){
                    return ;
                }
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i<str.length();i++){
                    if(digits.indexOf(str.charAt(i))<0){
                        sb.append(str.charAt(i));
                    }
                }
                tmp = sb.toString();
                suggestionEdTxt.setText(tmp);
            }
        });
        Button suggestionBtn=(Button)findViewById(R.id.suggestionBtn);
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefUser=getSharedPreferences("user",MODE_PRIVATE);
                JSONObject jsonObj=new JSONObject();
                try {
                    jsonObj.put("mobile",prefUser.getString("mobile",null));
                    jsonObj.put("suggestion",suggestionEdTxt.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpJson httpJson=new HttpJson("user/suggestion.php",jsonObj.toString(),mHandler);
                new Thread(httpJson.getHttpThread()).start();
            }
        });

        //设置toolbar导航栏，设置导航按钮
        Toolbar suggestion_toolbar = (Toolbar) findViewById(R.id.suggestion_toolbar);
        setSupportActionBar(suggestion_toolbar);
        suggestion_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.backout_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                suggestionEdTxt.setText("");
            }
        });
    }
}
