package com.lxkj.administrator.fealty.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.MySqliteHelper;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

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
    private MySqliteHelper helper;
    private SQLiteDatabase db;
    String[] str;//查询条件
    List<LatLng> points = new ArrayList<LatLng>();

    public void setHelper(MySqliteHelper helper) {
        Melucheng_fragment.this.helper = helper;
    }

    public MySqliteHelper getHelper() {
        return helper;
    }

    @Override
    protected void init() {
        //获取BaiduMap对象
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置普通地图
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        str = new String[]{String.valueOf(System.currentTimeMillis() - 1000*60*30)};
        helper = getHelper();
    }

    private void setMarker(LatLng point) {
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

    @Override
    protected void initListener() {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    Cursor cursor;

    @Override
    protected void initData() {
        db = helper.getReadableDatabase();
        /**
         * 从数据库中获取半个小时内的GPS信息
         *条件为:time+x>当前时间（假设x为30分钟）
         */
        //1.查询语句
        cursor = db.query("gps",new String[]{"_id,time,lat,lon"}, "time >= ?", str, null, null, null, null);
        //不断移动光标，遍历结果集
        while (cursor.moveToNext()) {
            //获取，第三列和第四列的值 ，lat  and lon
            long time = cursor.getLong(1);
            lat = Double.parseDouble(cursor.getString(2));
            lon = Double.parseDouble(cursor.getString(3));
            point = new LatLng(lat, lon);
            points.add(point);//放入List
            Log.e("info", lat + "?" + lon + "?" + time);
        }
        if (points.size() > 0) {
            setMarker(point);
        }
        if (points.size() < 2) {
            return;
        }
        List<Integer> colors = null;
        for (int i = 1; i < points.size(); i++) {
            //构建分段颜色索引数组
            colors = new ArrayList<>();
            colors.add(Integer.valueOf(Color.GREEN));
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(10).colorsValues(colors).points(points);//在走过的路上划线
        //显示在地图上
        Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
    }

    /**
     * 必须要实现
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

    LatLng point;

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
