package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/22.
 */
@ContentView(R.layout.meloction_fragment)
public class Melucheng_fragment extends BaseFragment {
    @ViewInject(R.id.melucheng_iv_right)
    private TextView tv;
    private BaiduMap mBaiduMap;//百度地图
    @ViewInject(R.id.mapView)
    private MapView mapView;
    private double lat;
    private double lon;
    @Override
    protected void init() {
        //获取BaiduMap对象
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置普通地图
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
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
    @Override
    protected void initListener() {
           tv.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   getActivity().onBackPressed();
               }
           });
    }
    @Override
    protected void initData() {

    }
    /**
     * 必须要实现
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();

    }
    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        lat=arguments.getDouble("lat");
        lon=arguments.getDouble("lon");
        LatLng point = new LatLng(lat, lon);
        setMapStatus(point);
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
// 构造折线点坐标
//List<LatLng> points = new ArrayList<LatLng>();
//    points.add(new LatLng(39.965,116.404));
//    points.add(new LatLng(39.925,116.454));
//    points.add(new LatLng(39.955,116.494));
//    points.add(new LatLng(39.905,116.554));
//    points.add(new LatLng(39.965,116.604));
//
//    //构建分段颜色索引数组
//    List<Integer> colors = new ArrayList<>();
//    colors.add(Integer.valueOf(Color.BLUE));
//    colors.add(Integer.valueOf(Color.RED));
//    colors.add(Integer.valueOf(Color.YELLOW));
//    colors.add(Integer.valueOf(Color.GREEN));
//
//    OverlayOptions ooPolyline = new PolylineOptions().width(10)
//            .colorsValues(colors).points(points);
//    添加在地图中
//    Polyline  mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
}
