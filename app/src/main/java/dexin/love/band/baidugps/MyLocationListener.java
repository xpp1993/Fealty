package dexin.love.band.baidugps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.xpp.blelib.BroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.MySqliteHelper;
import dexin.love.band.utils.NetWorkAccessTools;

/**
 * Created by Administrator on 2016/8/18/0018.
 */
public class MyLocationListener implements BDLocationListener, NetWorkAccessTools.RequestTaskListener {
    private double lat;
    private double lon;
    private String locationdescrible;
    private String address;
    private CallBack mCallBack;
    private MySqliteHelper helper=new MySqliteHelper(AppUtils.getBaseContext());
    private SQLiteDatabase db=helper.getReadableDatabase();
    public static final int GPS_UPLOAD_CODE = 0x21;
    private static final String TAG =  MyLocationListener.class.getSimpleName();
    public MyLocationListener(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.e(TAG, "开始监听定位" + location);
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            StringBuffer sb = new StringBuffer(256);
            //获取经纬度
            lat = location.getLatitude();//维度
            lon = location.getLongitude();//经度
            address = location.getAddrStr();
            locationdescrible = location.getLocationDescribe();// 位置语义化信息
            //把数据放入数据库
            addDataToSQLite(lat, lon);
            //把数据上传服务器
//            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "locationdescrible", "address", "lat", "lon"}, SessionHolder.user.getMobile(), locationdescrible, address, String.valueOf(lat), String.valueOf(lon));
//            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GPS_UPLOAD_URL, params, null, MyLocationListener.GPS_UPLOAD_CODE, MyLocationListener.this);
            sb.append(lat + "::" + lon + "::" + address + "::" + locationdescrible);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("gps定位成功");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("网络定位成功");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("离线定位成功，离线定位结果也是有效的");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                Log.e("sb", sb.toString());
            }
            if (mCallBack != null && helper != null) {
                mCallBack.callYou(lat, lon, locationdescrible, address, helper,currentTime);
            }
        }
    }

//    /**
//     * 经纬度或者地址互相转换
//     */
//    private void latIngToAddress(LatLng latlng){
//        //设置反地理经纬度坐标，请求位置时，需要一个经纬度
//        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
//        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//            //经纬度转换成地址
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//            }
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                if (reverseGeoCodeResult == null ||  reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//                    Toast.makeText(AppUtils.getBaseContext(), "找不到该地址!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }
    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    private MyHandler myHandler = new MyHandler();

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        if (requestCode == GPS_UPLOAD_CODE) {
            try {
                DecodeManager.decodeCommon(jsonObject, requestCode, myHandler);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
         if (requestCode==GPS_UPLOAD_CODE){
            Log.e("TAG","上传GPS失败！");
         }
    }

    public interface CallBack {
        void callYou(double lat, double lon, String locationdescrible, String address, MySqliteHelper helper, long currentTime);
    }
    long currentTime;
    private void addDataToSQLite(double lat, double lon) {
        /**
         *删除数据
         * 删除数据库中条件为time<当前时间-x（假如x=30分钟) 的数据
         */
        currentTime = System.currentTimeMillis();
        //currentTime= SystemClock.uptimeMillis();
        ContentValues values = toContentValues(currentTime, String.valueOf(lat), String.valueOf(lon));
        long _id = db.insert("gps", null, values);
        Log.e("_id", _id + "  "+currentTime);
        String[] str = new String[]{String.valueOf(currentTime - 1000 * 60*30)};
        long _id1 = db.delete("gps", "time < "+(currentTime - 1000 * 60*30), null);
        Log.e("_id", _id1 + "");
    }
    // 将数据封装为ContentValues
    public ContentValues toContentValues(long time, String lat,
                                         String lon) {
        // 底部类似于Map
        ContentValues values = new ContentValues();
        // 将值跟列一起放到ConentValues里。
        values.put("time", time);
        values.put("lat", lat);
        values.put("lon", lon);
        return values;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GPS_UPLOAD_CODE:
                    if (msg.getData().getInt("code") == 1) {
                        Log.e("gpsupdate", msg.getData().getString("desc"));
                    }
                    break;
                default:
                    break;
            }

        }
    }


}