package com.lxkj.administrator.fealty.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/28/0028.
 */
public class RateListData {
    private String mobile;
   private int rate;
    private String time;

    public RateListData() {
    }

    public RateListData(String mobile, int rate, String time) {
        this.mobile = mobile;
        this.rate = rate;
        this.time = time;
    }

    public RateListData(int rate, String time) {
        this.rate = rate;
        this.time = time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RateListData{" +
                "mobile='" + mobile + '\'' +
                ", rate=" + rate +
                ", time='" + time + '\'' +
                '}';
    }

}
