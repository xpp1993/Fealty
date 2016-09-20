package dexin.love.band.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;

/**
 * Created by Administrator on 2016/8/19.
 */
@ContentView(R.layout.activity_baidumap)
public class LocaltionFragment extends BaseFragment {
    private BaiduMap mBaiduMap;//百度地图
    @ViewInject(R.id.bmapView)
    private MapView mapView;
    public static final String RATE_CHANGED = "com.lxkj.administrator.fealty.fragment.LocaltionFragment";
    private Handler handler;
    @ViewInject(R.id.xinlvshou)
    private TextView showxinlv;
    @ViewInject(R.id.iv_left)
    private ImageView iv_left;

    // private
    @Override
    protected void init() {
        //获取BaiduMap对象
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置普通地图
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        handler = new Handler();
        //    LatLng latLng = new LatLng(SessionHolder.lat, SessionHolder.lon);
        //
//        setMarker(latLng);//设置标注
//        showTextView(SessionHolder.describleAddress, latLng);
//        setMapStatus(latLng);//设置中心坐标
        registerRateChangedReceiver();
    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        if (arguments == null)
            return;
        double lat = arguments.getDouble("lat");
        double lon = arguments.getDouble("lon");
        int rate = arguments.getInt("rate");
        showxinlv.setText(rate + "");//初始化显示心率
        String desctible = arguments.getString("locationdescrible");
        LatLng latLng = new LatLng(lat, lon);
        Log.e("baidumapData", lat + "::" + lon + desctible);
        setMarker(latLng);//设置标注
        showTextView(desctible, latLng);
        setMapStatus(latLng);//设置中心坐标
    }

    @Override
    protected void initListener() {
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {

    }

    int tempRate = 80;
    private RateReceiver mReceiver;

    private void registerRateChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        try {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mReceiver = new RateReceiver();
        filter.addAction(LocaltionFragment.RATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    //接收心率
    private class RateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocaltionFragment.RATE_CHANGED)) {
                tempRate = intent.getIntExtra("tempRate", -1);
//                lat = intent.getDoubleExtra("lat", 0.0);
//                lon = intent.getDoubleExtra("lon", 0.0);
//                SessionHolder.lat=lat;
//                SessionHolder.lon=lon;
//                describle = intent.getStringExtra("describle");
//                SessionHolder.describleAddress=describle;
            }
            handler.post(runnable);
        }
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showxinlv.setText(tempRate + "");
//            if (lat != 0) {
//                LatLng point = new LatLng(lat, lon);
//                Log.e("baidumapData", lat + "::" + lon + describle);
//                setMarker(point);//设置标注
//                showTextView(describle, point);//弹出窗体覆盖物
//                setMapStatus(point);//设置中心坐标
//            }
        }
    };

    private void setMarker(LatLng point) {
//        //定义Maker坐标点
//        LatLng point = new LatLng(lat, lon);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.dingwei1gps);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    //弹出窗覆盖物
    private void showTextView(String str, LatLng point) {
        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setTextColor(getResources().getColor(R.color.gray));
        textView.setBackgroundColor(getResources().getColor(R.color.cpb_white));
        textView.setText(str);
        Drawable drawable = getResources().getDrawable(R.mipmap.dingwei);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
        //定义用于显示该InfoWindow的坐标点
        // LatLng pt = new LatLng(lat, lon);
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(textView, point, -47);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    //设置新的中心点
    private void setMapStatus(LatLng point) {
        //  LatLng center = new LatLng(lat, lon);
        //定义地图状态
        MapStatus status = new MapStatus.Builder().target(point).zoom(18).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * 必须要实现
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if (runnable != null)
            handler.removeCallbacks(runnable);
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }

    /**
     * 必须要实现
     */
    @Override
    public void onResume() {
        mapView.setVisibility(View.VISIBLE);
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
        super.onResume();
    }

    /**
     * 必须要实现
     */
    @Override
    public void onPause() {
        mapView.setVisibility(View.INVISIBLE);
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
        super.onPause();
    }

}
