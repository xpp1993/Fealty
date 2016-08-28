package com.lxkj.administrator.fealty.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.alibaba.fastjson.JSONObject;
/**
 * Created by Carl on 2016/4/14/014.
 */
public class SPManager {
    private final String SESSION_HOLDER = "SessionHolder";
    private static Context mContext;
    private static SPManager spManager;
    private static SharedPreferences mSharedPreferences;
    private SPManager() {
    }
    public static synchronized SPManager getSPManager(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(ParameterManager.SESSION_CACHE_SP_NAME, mContext.MODE_PRIVATE);
        if (spManager == null)
            spManager = new SPManager();
        return spManager;
    }

    /**
     * sessionHolder 持久化
     */
//    public void persistenceSession() {
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("user", SessionHolder.user);
//        jsonObject.put("mobile", SessionHolder.mobile);
//        jsonObject.put("password", SessionHolder.password);
//        editor.putString(SESSION_HOLDER, jsonObject.toJSONString());
//        editor.commit();
//    }

}
