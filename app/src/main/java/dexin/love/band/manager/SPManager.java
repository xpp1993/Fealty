package dexin.love.band.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.alibaba.fastjson.JSONObject;
/**
 * Created by Carl on 2016/4/14/014.
 */
public class SPManager {
//    private final String SESSION_HOLDER = "SessionHolder";
   private static Context mContext;
//    private static SPManager spManager=null;
    private static SharedPreferences mSharedPreferences=null;
    private SPManager() {
    }

    /**
     * 懒汉式单例模式
     * @param context
     * @return
     */
//    public static synchronized SPManager getSPManager(Context context) {
//        mContext = context;
//        mSharedPreferences = mContext.getSharedPreferences(ParameterManager.SESSION_CACHE_SP_NAME, mContext.MODE_PRIVATE);
//        if (spManager == null)
//            spManager = new SPManager();
//        return spManager;
//    }
    /**
     * 懒汉式单例模式
     * @return
     */
    public static synchronized SharedPreferences getSharedPreferences(Context context) {
        mContext = context;
        if (mSharedPreferences== null)
            // 第一个参数：文件名,执行完这个语句后，会在data/data/项目包名的目录下创建一个shared_prefs目录。在此目录下创建一个key的xml文件。
            // 第二参数：操作的默认。MODE_PRIVATE:私有模式，只有自己的应用程序才能访问到
            mSharedPreferences = mContext.getSharedPreferences(ParameterManager.SESSION_CACHE_SP_NAME, mContext.MODE_PRIVATE);
        return mSharedPreferences;
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
