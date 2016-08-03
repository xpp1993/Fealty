package com.lxkj.administrator.fealty.bean;

/**
 * 手机联系人实体类
 * Created by Administrator on 2016/8/2/0002.
 */
public class Contacts {
    private String id;
    private String name;
    private String phone;

    public Contacts() {
    }

    public Contacts(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
