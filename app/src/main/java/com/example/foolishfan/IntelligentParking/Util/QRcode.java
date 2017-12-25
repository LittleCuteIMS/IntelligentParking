package com.example.foolishfan.IntelligentParking.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.foolishfan.IntelligentParking.SystemFunction.HourlyBillingActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CaiChuang on 2017/11/26 0026.
 * 二维码扫描类
 */

public class QRcode {
    private IntentResult result=null;       //扫描二维码返回的结果

    //扫描二维码
    public void scanQRcode(android.app.Activity activity, Class<?> captureActivity){
        IntentIntegrator integrator = new IntentIntegrator(activity);
// 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCaptureActivity(captureActivity);
        integrator.setPrompt("将二维码放入框内，即可自动扫描"); //底部的提示文字，设为""可以置空
        integrator.setCameraId(0); //前置或者后置摄像头
        integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    //生成二维码图片
    public Bitmap createQRcode(String words){
        Bitmap bitmap;
        BitMatrix matrix;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            matrix = writer.encode(words, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    //设置扫描二维码获得的结果
    public void setResult(int requestCode, int resultCode, android.content.Intent data){
        result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    }

    //获取结果
    public IntentResult getResult(){
        return result;
    }

    //扫描到停车场信息的二维码，跳转到计时计费页面
    public void startBilling(Context context){
        String jsonRs=result.getContents();
        try {
            JSONObject jsonObj=new JSONObject(jsonRs);
            if(jsonObj.getString("mode").equals("park")){
                Intent intent_Login_to_Register = new Intent(context, HourlyBillingActivity.class);
                Bundle bundle=new Bundle();//创建email内容
                bundle.putString("parkInfoJson",jsonRs);
                intent_Login_to_Register.putExtra("qr_code_info",bundle);
                context.startActivity(intent_Login_to_Register);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
