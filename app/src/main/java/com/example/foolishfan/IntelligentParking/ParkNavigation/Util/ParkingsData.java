package com.example.foolishfan.IntelligentParking.ParkNavigation.Util;

/**
 * Created by zhanglin on 2017/12/3.
 */

public class ParkingsData {
    private String name;//停车场名称
    private int carportNumber;//车位数量
    private int freeNumber;//空闲数量
    private String address;//地址
    private String phone;//电话
    private double charge;//收费标准
    private String imageId;//图片的路径
    private double latitude;
    private double longitude;

    public ParkingsData(){}

    public void setName(String name){ this.name = name; }
    public void setcarportNumber(int carportNumber){
        this.carportNumber = carportNumber;
    }
    public void setFreeNumber(int freeNumber){
        this.freeNumber = freeNumber;
    }
    public void setAddress(String address ){
        this.address = address;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setCharge(double charge){
        this.charge = charge;
    }
    public void setImageId(String imageId){
        this.imageId = imageId;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getName(){
        return name;
    }
    public int getCarportNumber(){
        return carportNumber;
    }
    public int getFreeNumber(){
        return freeNumber;
    }
    public String getAddress(){
        return address;
    }
    public String getPhone(){
        return phone;
    }
    public double getCharge(){return  charge;}
    public String getImageId(){
        return imageId;
    }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }

}
