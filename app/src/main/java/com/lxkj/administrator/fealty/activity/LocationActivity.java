package com.lxkj.administrator.fealty.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.lxkj.administrator.fealty.R;

/**
 * Created by Administrator on 2016/8/18/0018.
 */
public class LocationActivity extends AppCompatActivity {
    private BaiduMap mBaiduMap;//百度地图
    private MapView mapView;
    private double lat,lon;
   // private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumap);
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.bmapView);
        //获取BaiduMap对象
        mBaiduMap =  mapView.getMap();
        Intent intent=getIntent();
        lat=  intent.getDoubleExtra("lat",22);
        lon=  intent.getDoubleExtra("lon",114);
        Log.e("baidumapData",lat+"::"+lon);
    }
    /**
     * 必须要实现
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    /**
     * 必须要实现
     */
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    /**
     * 必须要实现
     */
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
}
