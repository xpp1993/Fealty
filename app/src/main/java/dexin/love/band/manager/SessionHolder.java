package dexin.love.band.manager;

import dexin.love.band.bean.UserInfo;
public final class SessionHolder {
    public static String mobile;//电话
    public static UserInfo user;//用户类
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
