package dexin.love.band.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommonTools {
    /**
     * 使用系统当前日期，产生一个临时文件，用户缓存
     *
     * @param fileType 缓存文件的格式  如：jpg png   不要加上小数点
     * @return
     */
    public static File getTempFile(String fileType) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("'TEMP_FILE'_yyyy-MM-dd-HH-mm-ss");
            String tempFileName = dateFormat.format(new Date());
            try {
                file = File.createTempFile(tempFileName, fileType, Environment.getExternalStorageDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 构建参数map对象的工具方法
     *
     * @param keys
     * @param values
     * @return
     */
    public static Map<String, String> getParameterMap(String keys[], String... values) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVercode(Context context) {
        int vercode = -1;
        try {
            vercode = context.getPackageManager().getPackageInfo("dexin.love.band", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("vercode", e.getMessage());
        }
        return vercode;
    }
    /**
     * 获取软件版本名称
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo("dexin.love.band", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("verName", e.getMessage());
        }
        return verName;
    }

    /**
     * 联网下载文件用
     * @param path
     * @return
     */
    public static HttpURLConnection getInputStream(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setDoInput(true);
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                Log.e("xpp",connection.getContentLength()+"");
                return connection;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
           Log.d("getAndroidSDKVersion",e.toString());
        }
        return version;
    }

}
