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
    private TextView tv_gsp;

    private CallBack mCallBack;

    public MyLocationListener(TextView tv_gsp,CallBack callBack) {
        this.tv_gsp = tv_gsp;
        this.mCallBack = callBack;
    }
    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.e("sb","开始监听定位"+location);
        if (null !=location &&location.getLocType() != BDLocation.TypeServerError) {
            StringBuffer sb = new StringBuffer(256);
            //获取经纬度
            lat = location.getLatitude();
            lon = location.getLongitude();
            sb.append(location.getAddrStr());
            tv_gsp.setText(sb.toString());
            Log.e("sb", sb.toString());
            sb.append(location.getLocType());
            Log.e("sb",sb.toString());
            //把经纬度暴露出去
//            sb.append("time : ");
//            /**
//             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//             */
            sb.append(location.getTime());
            sb.append("\nlocType : ");// 定位类型
            sb.append(location.getLocType());
            sb.append("\nlocType description : ");// *****对应的定位类型说明*****
            sb.append(location.getLocTypeDescription());
            sb.append("\nlatitude : ");// 纬度
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");// 经度
            sb.append(location.getLongitude());
            sb.append("\nradius : ");// 半径
            sb.append(location.getRadius());
            sb.append("\nCountryCode : ");// 国家码
            sb.append(location.getCountryCode());
            sb.append("\nCountry : ");// 国家名称
            sb.append(location.getCountry());
            sb.append("\ncitycode : ");// 城市编码
            sb.append(location.getCityCode());
            sb.append("\ncity : ");// 城市
            sb.append(location.getCity());
            sb.append("\nDistrict : ");// 区
            sb.append(location.getDistrict());
            sb.append("\nStreet : ");// 街道
            sb.append(location.getStreet());
            Log.e("sb",sb.toString());
        //    sb.append("\naddr : ");// 地址信息
//            sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//            sb.append(location.getUserIndoorState());
//            sb.append("\nDirection(not all devices have value): ");
//            sb.append(location.getDirection());// 方向
//            sb.append("\nlocationdescribe: ");
//            sb.append(location.getLocationDescribe());// 位置语义化信息
//            sb.append("\nPoi: ");// POI信息
//            if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                for (int i = 0; i < location.getPoiList().size(); i++) {
//                    Poi poi = (Poi) location.getPoiList().get(i);
//                    sb.append(poi.getName() + ";");
//                }
//            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 速度 单位：km/h
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());// 卫星数目
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 海拔高度 单位：米
//                sb.append("\ngps status : ");
//                sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                // 运营商信息
//                if (location.hasAltitude()) {// *****如果有海拔高度*****
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 单位：米
//                }
//                sb.append("\noperationers : ");// 运营商信息
//                sb.append(location.getOperators());
            //    sb.append("\ndescribe : ");
                sb.append("网络定位成功");
               Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
             //   sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
              //  sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
              //  sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
                Log.e("sb", sb.toString());
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
              //  sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                Log.e("sb", sb.toString());
            }
//            lat = location.getLatitude();
//            lon = location.getLongitude();

//            //这个判断是为了防止每次定位都重新设置中心点和marker
//            if(isFirstLocation){
//                isFirstLocation = false;
//                setMarker();
//                setUserMapCenter();
//            }

            if(mCallBack!=null){
                mCallBack.callYou(lat, lon);
            }
        }
    }
//    /**
//     * 添加marker
//     */
//    private void setMarker() {
//        Log.v("pcw","setMarker : lat : "+ lat+" lon : " + lon);
//        //定义Maker坐标点
//        LatLng point = new LatLng(lat, lon);
//        //构建Marker图标
//        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.ccwant_back);
//        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(point)
//                .icon(bitmap);
//        //在地图上添加Marker，并显示
//       // mBaiduMap.addOverlay(option);
//    }

//    /**
//     * 设置中心点
//     */
//    private void setUserMapCenter() {
//        Log.v("pcw","setUserMapCenter : lat : "+ lat+" lon : " + lon);
//        LatLng cenpt = new LatLng(lat,lon);
//        //定义地图状态
//        MapStatus mMapStatus = new MapStatus.Builder()
//                .target(cenpt)
//                .zoom(18)
//                .build();
//        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//     //   mBaiduMap.setMapStatus(mMapStatusUpdate);
//    }

    public interface CallBack{
        void callYou(double lat, double lon);
    }
}
