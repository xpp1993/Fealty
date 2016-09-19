package dexin.love.band.utils;

import android.os.Handler;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ThreadPoolUtils {
    public static void runTaskOnThread(Runnable runnable) {
        ThreadPoolFactory.getCommonThreadPool().execute(runnable);
    }

    //UI的handler
    private static Handler handler = new Handler();

    public static void runTaskOnUIThread(Runnable runnable) {
        handler.post(runnable);
    }
}
