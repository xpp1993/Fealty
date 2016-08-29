package com.lxkj.administrator.fealty.manager;

import android.os.Handler;
import android.util.Log;

import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.ExampleUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public final class SessionHolder {
    public static String mobile;//电话
    public static String password;//密码
    public static UserInfo user;//用户类
//    public static double lat;
//    public  static  double lon;
//    public static String describleAddress;

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
