package com.example.foolishfan.IntelligentParking.Util;

/**
 * Created by zhanglin on 2017/12/3.
 */

public class ParkingsData {
    private String name;
    private int imageId;

    public ParkingsData(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }
}
