package com.lxkj.administrator.fealty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/19.
 */
@ContentView(R.layout.activity_baidumap)
public class LocaltionFragment extends BaseFragment {
    private BaiduMap mBaiduMap;//百度地图
    @ViewInject(R.id.bmapView)
    private MapView mapView;
    private double lat, lon;

    // private
    @Override
    protected void init() {
        //获取BaiduMap对象
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);
        lat = arguments.getDouble("lat");
        lon = arguments.getDouble("lon");
        Log.e("baidumapData", lat + "::" + lon);
        setMarker();

    }
    private void setMarker() {
        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.dingwei);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
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

    /**
     * 必须要实现
     */
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    /**
     * 必须要实现
     */
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
//    //构造纹理资源
//    BitmapDescriptor custom1 = BitmapDescriptorFactory
//            .fromResource(R.drawable.icon_road_red_arrow);
//    BitmapDescriptor custom2 = BitmapDescriptorFactory
//            .fromResource(R.drawable.icon_road_green_arrow);
//    BitmapDescriptor custom3 = BitmapDescriptorFactory
//            .fromResource(R.drawable.icon_road_blue_arrow);
//    // 定义点
//    LatLng pt1 = newLatLng(39.93923, 116.357428);
//    LatLng pt2 = newLatLng(39.91923, 116.327428);
//    LatLng pt3 = newLatLng(39.89923, 116.347428);
//    LatLng pt4 = newLatLng(39.89923, 116.367428);
//    LatLng pt5 = newLatLng(39.91923, 116.387428);
//
//    //构造纹理队列
//    List<BitmapDescriptor>customList = newArrayList<BitmapDescriptor>();
//    customList.add(custom1);
//    customList.add(custom2);
//    customList.add(custom3);
//
//    List<LatLng> points = newArrayList<LatLng>();
//    List<Integer> index = newArrayList<Integer>();
//    points.add(pt1);//点元素
//    index.add(0);//设置该点的纹理索引
//    points.add(pt2);//点元素
//    index.add(0);//设置该点的纹理索引
//    points.add(pt3);//点元素
//    index.add(1);//设置该点的纹理索引
//    points.add(pt4);//点元素
//    index.add(2);//设置该点的纹理索引
//    points.add(pt5);//点元素
////构造对象
//    OverlayOptionsooPolyline = newPolylineOptions().width(15).color(0xAAFF0000).points(points).customTextureList(customList).textureIndex(index);
////添加到地图
//    mBaiduMap.addOverlay(ooPolyline);
//    效果图如下:
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
