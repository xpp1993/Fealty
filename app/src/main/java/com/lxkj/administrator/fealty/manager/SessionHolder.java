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
