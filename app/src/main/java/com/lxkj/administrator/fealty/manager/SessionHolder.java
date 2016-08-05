package com.lxkj.administrator.fealty.manager;

import com.lxkj.administrator.fealty.bean.UserInfo;
public final class SessionHolder {

    public static String mobile;
    public static String password;
    public static UserInfo user;
    /**
     * sessionHolder 初始化，并且持久化sessionHolder
     *
     *
     */
    public static void initHolder(String mobile, UserInfo userMySelf) {
        SessionHolder.mobile=mobile;
        SessionHolder.user = userMySelf;
    }


}
