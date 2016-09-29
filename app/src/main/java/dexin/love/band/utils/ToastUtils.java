package dexin.love.band.utils;

import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ToastUtils {
    public static void showToastInUIThread(String content) {
        Toast.makeText(AppUtils.getBaseContext(), content, Toast.LENGTH_SHORT).show();
    }
    public static void showToastInThread(final String content){
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppUtils.getBaseContext(),content,Toast.LENGTH_LONG).show();
            }
        });
    }
}
