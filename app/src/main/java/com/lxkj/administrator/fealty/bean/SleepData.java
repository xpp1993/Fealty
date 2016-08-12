package com.lxkj.administrator.fealty.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/12.
 */
public class SleepData implements Serializable {
   private int deep_hour,deep_minute , light_hour , light_minute , active_count ;
    private String total_hour_str;

    public SleepData(int deep_hour, int deep_minute, int light_hour, int light_minute, int active_count, String total_hour_str) {
        this.deep_hour = deep_hour;
        this.deep_minute = deep_minute;
        this.light_hour = light_hour;
        this.light_minute = light_minute;
        this.active_count = active_count;
        this.total_hour_str = total_hour_str;
    }

    public SleepData() {
    }

    public int getDeep_hour() {
        return deep_hour;
    }

    public void setDeep_hour(int deep_hour) {
        this.deep_hour = deep_hour;
    }

    public int getDeep_minute() {
        return deep_minute;
    }

    public void setDeep_minute(int deep_minute) {
        this.deep_minute = deep_minute;
    }

    public int getLight_hour() {
        return light_hour;
    }

    public void setLight_hour(int light_hour) {
        this.light_hour = light_hour;
    }

    public int getActive_count() {
        return active_count;
    }

    public void setActive_count(int active_count) {
        this.active_count = active_count;
    }

    public int getLight_minute() {
        return light_minute;
    }

    public void setLight_minute(int light_minute) {
        this.light_minute = light_minute;
    }

    public String getTotal_hour_str() {
        return total_hour_str;
    }

    public void setTotal_hour_str(String total_hour_str) {
        this.total_hour_str = total_hour_str;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "deep_hour=" + deep_hour +
                ", deep_minute=" + deep_minute +
                ", light_hour=" + light_hour +
                ", light_minute=" + light_minute +
                ", active_count=" + active_count +
                ", total_hour_str='" + total_hour_str + '\'' +
                '}';
    }
}
