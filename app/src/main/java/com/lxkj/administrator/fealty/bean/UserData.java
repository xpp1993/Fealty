package com.lxkj.administrator.fealty.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/16/0016.
 */
public class UserData implements Serializable {
    private String identity;
    private  SportData sportData;
 private   SleepData sleepData;
    private  int tempRate;

    public UserData() {
    }

    public UserData(String identity, SportData sportData, SleepData sleepData, int tempRate) {
        this.identity = identity;
        this.sportData = sportData;
        this.sleepData = sleepData;
        this.tempRate = tempRate;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public SportData getSportData() {
        return sportData;
    }

    public void setSportData(SportData sportData) {
        this.sportData = sportData;
    }

    public SleepData getSleepData() {
        return sleepData;
    }

    public void setSleepData(SleepData sleepData) {
        this.sleepData = sleepData;
    }

    public int getTempRate() {
        return tempRate;
    }

    public void setTempRate(int tempRate) {
        this.tempRate = tempRate;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "identity='" + identity + '\'' +
                ", sportData=" + sportData +
                ", sleepData=" + sleepData +
                ", tempRate=" + tempRate +
                '}';
    }
}
