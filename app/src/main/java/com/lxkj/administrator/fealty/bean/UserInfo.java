package com.lxkj.administrator.fealty.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/27.
 */
public class UserInfo implements Serializable{
    private String mobile;//手机号
    private String gender;//性别
    private String nickName;//昵称
    private String username;//真实姓名
    private String birthday;//生日
    private String userpic;//头像
    private String identity;//身份标识
    private String password;
    public UserInfo(String mobile, String gender, String nickName, String username, String birthday, String userpic,String password) {
        this.mobile = mobile;
        this.gender = gender;
        this.nickName = nickName;
        this.username = username;
        this.birthday = birthday;
        this.userpic = userpic;
        this.identity=identity;
        this.password=password;
    }

    public UserInfo() {
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "mobile='" + mobile + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
