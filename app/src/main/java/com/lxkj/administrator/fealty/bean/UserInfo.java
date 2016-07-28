package com.lxkj.administrator.fealty.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/27.
 */
public class UserInfo implements Serializable{
    public String userName;
    public Bitmap bitmap;

    //用户类型标杆 1.USER_OLDMAN_CODE_MAN ,老人 2.USER_YUNGMAN_CODE_WOMAN子女
    public static final String USER_OLDMAN_CODE_MAN = "1", USER_YUNGMAN_CODE_WOMAN = "2";
    private String gender;
    private String userid;
    private String mobile;//掩码
    private String mobilePlain;//明码
    private String nickName;//昵称
    private String username;//真实姓名
    private String birthday;
    private String userpic;

    /**
     * 登录
     */
    protected void login() {

    }

    /**
     * 退出登录
     */
    protected void logout() {

    }

    /**
     * 注册
     */
    protected void regist() {

    }

    /**
     * 修改密码
     */

    protected void changePassword() {

    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobilePlain() {
        return mobilePlain;
    }

    public void setMobilePlain(String mobilePlain) {
        this.mobilePlain = mobilePlain;
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
}
