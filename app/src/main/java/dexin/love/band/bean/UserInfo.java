package dexin.love.band.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/27.
 */
public class UserInfo implements Serializable {
    private String mobile;//手机号
    private String gender;//性别
    private String nickName;//昵称
    private String username;//真实姓名
    private String birthday;//生日
    private String userpic;//头像
    private String identity;//身份标识
    private String password;
    private String address;//Gps定位的地址
    private int currentHeart;
    private String cuffElectricity;
    private String mobileElectricity;
    private String url;//版本下载地址
    private int versionCode;//版本号

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public UserInfo(String mobile, String gender, String nickName, String username, String birthday, String userpic, String password) {
        this.mobile = mobile;
        this.gender = gender;
        this.nickName = nickName;
        this.username = username;
        this.birthday = birthday;
        this.userpic = userpic;
        this.identity = identity;
        this.password = password;
    }

    public UserInfo(String identity, String mobile, int currentHeart, String address, String cuffElectricity, String mobileElectricity) {
        this.identity = identity;
        this.mobile = mobile;
        this.currentHeart = currentHeart;
        this.address = address;
        this.cuffElectricity = cuffElectricity;
        this.mobileElectricity = mobileElectricity;
    }

    public int getCurrentHeart() {
        return currentHeart;
    }

    public void setCurrentHeart(int currentHeart) {
        this.currentHeart = currentHeart;
    }

    public String getCuffElectricity() {
        return cuffElectricity;
    }

    public void setCuffElectricity(String cuffElectricity) {
        this.cuffElectricity = cuffElectricity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileElectricity() {
        return mobileElectricity;
    }

    public void setMobileElectricity(String mobileElectricity) {
        this.mobileElectricity = mobileElectricity;
    }

    public UserInfo() {
    }

    public UserInfo(int versionCode, String url, String password, String userpic, String username, String nickName, String mobile, String gender, String birthday, String identity) {
        this.versionCode = versionCode;
        this.url = url;
        this.password = password;
        this.userpic = userpic;
        this.username = username;
        this.nickName = nickName;
        this.mobile = mobile;
        this.gender = gender;
        this.birthday = birthday;
        this.identity = identity;
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
                ", gender='" + gender + '\'' +
                ", nickName='" + nickName + '\'' +
                ", username='" + username + '\'' +
                ", birthday='" + birthday + '\'' +
                ", userpic='" + userpic + '\'' +
                ", identity='" + identity + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", currentHeart=" + currentHeart +
                ", cuffElectricity='" + cuffElectricity + '\'' +
                ", mobileElectricity='" + mobileElectricity + '\'' +
                ", url='" + url + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
