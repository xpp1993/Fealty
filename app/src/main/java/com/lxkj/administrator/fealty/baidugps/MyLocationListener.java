package com.lxkj.administrator.fealty.baidugps;

import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * Created by Administrator on 2016/8/18/0018.
 */
public class MyLocationListener implements BDLocationListener {
    private double lat;
    private double lon;
    private String locationdescrible;
    private String address;
    private CallBack mCallBack;

    public MyLocationListener(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.e("sb", "开始监听定位" + location);
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            StringBuffer sb = new StringBuffer(256);
            //获取经纬度
            lat = location.getLatitude();//维度
            lon = location.getLongitude();//经度
            address = location.getAddrStr();
            locationdescrible = location.getLocationDescribe();// 位置语义化信息
            sb.append(lat+"::"+lon+"::"+address+"::"+locationdescrible);
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
            if (mCallBack != null) {
                mCallBack.callYou(lat, lon, locationdescrible,address);
            }
        }
    }
    public interface CallBack {
        void callYou(double lat, double lon, String locationdescrible, String address);
    }
}
