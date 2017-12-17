package com.example.foolishfan.IntelligentParking.System;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.foolishfan.IntelligentParking.R;
import com.example.foolishfan.IntelligentParking.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Iterator;



import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private SqlDao sqldao;
	private static final String TAG = "JPush";
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				//自定义消息通知栏
				receivingNotification(context, bundle);

			}else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				String content = bundle.getString(JPushInterface.EXTRA_ALERT);//此处取出的是通知的内容。还可以取出别的信息
				Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");
				//打开自定义的Activity
				//由于是在广播接收器里面启动Activity，所以要添加Flag：FLAG_ACTIVITY_NEW_TASK，表示新创建一个task来存放启动的Activity
				Intent i = new Intent(context, MessageCenter.class);
				i.putExtra("content",content);//Intent传递数据将获取到的消息信息传递给MessageCenter
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(i);//启动MessageCenter

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

		}


	//打印所有的 intent extra 数据
	@NonNull
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Logger.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Logger.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}



	//自定义通知栏
	private void receivingNotification(Context context, Bundle bundle) {
	    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//获取系统的通知栏的Service
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		// 使用notification
		// 使用广播或者通知进行内容的显示
		//新建状态栏通知
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentText(message).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(JPushInterface.EXTRA_TITLE);
	    builder.setDefaults(Notification.DEFAULT_SOUND);
	    manager.notify(1,builder.build());
	}
}
