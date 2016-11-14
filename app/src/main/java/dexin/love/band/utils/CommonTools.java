package dexin.love.band.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dexin.love.band.manager.ParameterManager;

public class CommonTools {
    private static final String TAG = CommonTools.class.getSimpleName();

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
     *
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
     *
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
                Log.e("xpp", connection.getContentLength() + "");
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
    /**
     * POST请求获取数据
     */
    public static FileOutputStream postDownTTS(String requestUrl, Map<String, Object> requestParamsMap) {
        PrintWriter printWriter = null;
        FileOutputStream fileOutputStream = null;
        StringBuffer params = new StringBuffer();
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        // 组织请求参数
        Iterator it = requestParamsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            params.append(element.getKey());
            params.append("=");
            params.append(element.getValue());
            params.append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        URL realUrl = null;
        try {
            realUrl = new URL(requestUrl);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            //设置请求属性
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            printWriter.write(params.toString());
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                Log.e(TAG, " Error===" + responseCode);
            } else if (responseCode==200){
                Log.e(TAG, "Post Success!");
                inputStream = httpURLConnection.getInputStream();
                if (inputStream != null) {
                    fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "tts.audio"));
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    while ((ch = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                        fileOutputStream.flush();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "send post request error!" + e);
        } finally {
            httpURLConnection.disconnect();
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileOutputStream;
    }
}
