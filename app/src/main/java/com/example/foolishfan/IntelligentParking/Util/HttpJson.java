package com.example.foolishfan.IntelligentParking.Util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/10/8 0008.
 * 此类为客户端向服务器发送json格式的数据并获取服务器的返回信息
 */

public class HttpJson {
    static private  String website="http://192.168.155.1/ParkingWeb/";    //设置访问IP地址值
    private String path=null;     //获取访问的php文件的URL地址
    private String json=null;       //获取要传输的json格式字符串数据
    private Handler handler=null;   //接受子线程发送的数据， 并用此数据配合主线程更新UI
    private HttpThread httpThread;  //处理通信的线程

    public HttpJson(String initPath, String initJson, Handler initHandler){//构造函数

        path=initPath;
        json=initJson;
        handler=initHandler;    //获取主线程的handler
        httpThread=new HttpThread();
    }

    public HttpThread getHttpThread(){//获取网络通信线程
        return httpThread;
    }

    static public void setWebsite(String initWebsite){
        website=initWebsite;
    }

    private class HttpThread implements Runnable{//处理通信的线程的类
        public void run() {
            URL url=null;
            String result="";   //获取服务器返回的内容
            try {
                url = new URL(website+path);//构造URL对象
                //打开连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");//设置请求为POST方式
                conn.setDoInput(true);//设置输入
                conn.setDoOutput(true);//设置输出
                conn.setUseCaches(false);//禁用缓存

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                //设置文件类型
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //设置接收类型
                conn.setRequestProperty("accept","application/json");
                //往服务器里面发送数据
                if(json!=null&&!TextUtils.isEmpty(json)){
                    //设置文件长度
                    conn.setRequestProperty("Content-Length",String.valueOf(json.getBytes().length));
                    //打开链接

                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    //组织要发送的数据
                    out.write(json.getBytes());//将数据写入POST数据流
                    out.flush();//刷新流
                    out.close();//关闭流
                }
                //判断请求是否成功
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    InputStreamReader in=null;
                    //获取服务器的响应内容
                    in = new InputStreamReader(conn.getInputStream());
                    BufferedReader buffer=new BufferedReader(in);
                    String inputLine=null;
                    while((inputLine=buffer.readLine())!=null) {//循环读取内容
                        result += inputLine;
                    }
                    in.close();//关闭读取
                    conn.disconnect();//断开连接
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            Message m = handler.obtainMessage();//创建一个Message消息
            if(result!="")
                m.obj=result;//为消息添加从服务器上获取的消息
            handler.sendMessage(m);//发送消息
        }
    }
}
