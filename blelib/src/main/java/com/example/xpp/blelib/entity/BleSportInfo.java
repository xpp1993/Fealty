package com.example.xpp.blelib.entity;

import java.io.Serializable;

/**
 * Created by XPP on 2016/10/7/007.
 * 封装接收到的蓝牙手环传过来的当前运动信息
 */

public class BleSportInfo implements Serializable {
    private String datatime;//当前时间
    private String steps;//当前手环计步总数
    private String distance;//当前行走的距离
    private String calories;//当前消耗的卡路里
    private  String sleeptotal;//当前睡眠时间

    public BleSportInfo() {
    }

    public BleSportInfo(String datatime, String steps, String distance, String calories, String sleeptotal) {
        this.datatime = datatime;
        this.steps = steps;
        this.distance = distance;
        this.calories = calories;
        this.sleeptotal = sleeptotal;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getSleeptotal() {
        return sleeptotal;
    }

    public void setSleeptotal(String sleeptotal) {
        this.sleeptotal = sleeptotal;
    }

    @Override
    public String toString() {
        return "BleSportInfo{" +
                "datatime='" + datatime + '\'' +
                ", steps='" + steps + '\'' +
                ", distance='" + distance + '\'' +
                ", calories='" + calories + '\'' +
                ", sleeptotal='" + sleeptotal + '\'' +
                '}';
    }
}
