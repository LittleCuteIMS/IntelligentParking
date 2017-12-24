package com.example.foolishfan.IntelligentParking.User;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.MainActivity;
import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.System.Resetpwd;
import com.example.foolishfan.IntelligentParking.Util.HttpJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class User extends AppCompatActivity {

    TextView mobile;
    TextView nickName;
    private ImageView mImage;
    private Button mAddImage;
    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    protected Handler handler0 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.obj != null) {//如果不为空
                //解析json数据并保存
                String jsonStr = msg.obj.toString();
                //解析json数据，按键名存在SharedPreference中
                JSONObject jsonObject;
                String nickNameString=null;
                try {
                    jsonObject = new JSONObject(jsonStr);
                    nickNameString=jsonObject.getString("nickname");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //保存用户昵称到手机
                SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                recordEditor.putString("nickname", nickNameString);
                recordEditor.apply();
                //将相应消息放在对应的控件显示
                SharedPreferences userPref=getSharedPreferences("user",Context.MODE_PRIVATE);
                String mobileString=userPref.getString("mobile",null);
                mobile.setText(mobileString);
                nickName.setText(nickNameString);
            } else {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        setContentView(R.layout.user);
        initUI();
        initListeners();

        //设置toolbar导航栏，设置导航按钮
        Toolbar user_toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(user_toolbar);
        user_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobile = (TextView) findViewById(R.id.mobile);//显示用户手机号码
        nickName = (TextView) findViewById(R.id.nickname);//显示用户昵称

        getUserInfo();

        //获取按钮，设置监听事件
        Button resetpwd_button = (Button) findViewById(R.id.resetpwd_button);
        resetpwd_button.setOnClickListener(setListener);
    }

    private void initUI() {
        mImage= (ImageView) findViewById(R.id.iv_image);
        mAddImage= (Button) findViewById(R.id.btn_add_image);
    }

    private void initListeners() {
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
    }
    /**
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(User.this);
        builder.setTitle("修改头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        /*Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");*/
                        Intent openAlbumIntent;
                        if (Build.VERSION.SDK_INT < 19) {
                            openAlbumIntent = new Intent(
                                    Intent.ACTION_GET_CONTENT);
                            openAlbumIntent.setType("image/*");
                        } else {
                            openAlbumIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp_image.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }

    public void getUserInfo() {
        //1.从sharedPreference里面获取当前账户手机号
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String mobile = pref.getString("mobile", null);

        //2.将用户手机号转为json
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //3.把手机号发送到服务器上进行查询
        String path = "user/userinfo_inquiry.php";
        HttpJson http = new HttpJson(path, json.toString(), handler0);
        new Thread(http.getHttpThread()).start();
    }


    //解析Json数据
    private void parseJSONWithJSONObject(String jsonData) {
        try {

            //？得到json数组
            JSONObject jsonObject = new JSONObject(jsonData);
            String nickName=jsonObject.getString("nickname");

                //保存当前用户记录
                SharedPreferences.Editor recordEditor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                recordEditor.putString("nickname", nickName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            mImage.setImageBitmap(mBitmap);//显示图片
            //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...
        }
    }

    //设置的监听事件
    View.OnClickListener setListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.resetpwd_button:
                    Intent intent = new Intent(getApplicationContext(), Resetpwd.class);
                    startActivity(intent);
                    break;

            }
        }
    };

}

