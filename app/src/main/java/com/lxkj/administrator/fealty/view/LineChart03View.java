package com.lxkj.administrator.fealty.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.AnchorDataPoint;
import org.xclcharts.view.GraphicalView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LineChart03View extends GraphicalView {

    private String TAG = "LineChart03View";
    private LineChart chart = new LineChart();

    //标签集合
    private LinkedList<String> labels = new LinkedList<String>();
    private LinkedList<LineData> chartData = new LinkedList<LineData>();

    public LineChart03View(Context context) {
        super(context);
        chartLabels();
        chartDataSet();
        chartRender();
    }

    public LineChart03View(Context context, AttributeSet attrs) {
        super(context, attrs);
        chartLabels();
        chartDataSet();
        chartRender();
    }

    public LineChart03View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        chartLabels();
        chartDataSet();
        chartRender();
    }

    @SuppressLint("NewApi")
    private void chartRender() {
        try {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            //设定数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);

            //数据轴最大值
            chart.getDataAxis().setAxisMax(120);
            //数据轴最小值
            chart.getDataAxis().setAxisMin(50);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(20);


            //chart.getDataAxis().setAxisLineVisible(false);
            chart.getDataAxis().hide();

            chart.getCategoryAxis().getTickLabelPaint().setTextAlign(Align.LEFT);
            chart.getCategoryAxis().setTickLabelRotateAngle(90);


            chart.hideBorder();
            chart.hideDyLine();
            chart.getPlotLegend().hide();

            //忽略Java的float计算误差
            chart.disableHighPrecision();

            chart.disablePanMode(); //禁掉平移，这样线上的标注框在最左和最右时才能显示全

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }
    }

    private void chartDataSet() {
        LinkedList<Double> dataSeries2 = new LinkedList<Double>();
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        dataSeries2.add((double) 75);
        LineData lineData2 = new LineData("", dataSeries2,Color.rgb(93,205,191));
        lineData2.setDotStyle(XEnum.DotStyle.DOT);
        lineData2.getPlotLine().getDotPaint().setColor(Color.rgb(93,205,191));
        lineData2.setLabelVisible(true);
        chartData.add(lineData2);
    }

    private void chartLabels() {
        labels.clear();
        labels.add("9");
        labels.add("10");
        labels.add("11");
        labels.add("12");
        labels.add("13");
        labels.add("14");
        labels.add("15");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w, h);
    }  
	
	/*
	 * 
	 1. 右边轴view 遮住右边view视图中最左边点或线的处理办法：
	xml  FrameLayout 中 LineChart03View_left 要放到后面，放前面会盖住scrollview中的图
	HLNScrollActivity 中的horiView.setPadding()可以注释掉
	然后在 LineChart03View 中 通过设置chart.setpadding中的left来对整齐
	或通过  chart.setChartRange()中的x位置来偏移即可
	
	 2. 如果觉得左滑范围太大，可以调整 HLNScrollActivity中 的horiView.setPadding()
	 也可以调整  chart.setChartRange()中的x位置如 chart.setChartRange(60,0, ....)
	*/

    @Override
    public void render(Canvas canvas) {
        try {
            // chart.setChartRange(60,0, //设置x位置为60
//            chart.setChartRange(0, 0,
//                    this.getLayoutParams().width - 10, this.getLayoutParams().height - 10);
                        chart.setChartRange(0, 0,
                    this.getLayoutParams().width - 10, this.getLayoutParams().height - 10);
            //设置绘图区内边距
          //  chart.setPadding(70, 30, 30, 30);
            chart.setPadding(70, 28, 30,90);
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
//    public void reset(TreeMap<Integer, Integer> map) {
//        if (map != null) {
//            Iterator<Integer> valueIterator = map.values().iterator();
//            Iterator<Integer> keyIterator = map.keySet().iterator();
//            int  max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
//            LinkedList<Double> dataSeries2 = new LinkedList<Double>();
//            LineData lineData2 = new LineData("", dataSeries2, Color.rgb(93,205,191));
//            lineData2.setDotStyle(XEnum.DotStyle.DOT);
//            lineData2.getPlotLine().getDotPaint().setColor(Color.rgb(93,205,191));
//            lineData2.setLabelVisible(true);
//            while (valueIterator.hasNext()) {
//                int value = valueIterator.next();
//                max = max > value ? max : value;
//                min = min < value ? min : value;
//                dataSeries2.add((double)value);
//            }
//            chartData.clear();
//            chartData.add(lineData2);
//
//            //数据轴最大值
//            chart.getDataAxis().setAxisMax(max + 10);
//            //数据轴最小值
//            chart.getDataAxis().setAxisMin(min - 10 > 0 ? (min - 10) : 0);
//            //数据轴刻度间隔
//            chart.getDataAxis().setAxisSteps(10);
//            labels.clear();
//            while (keyIterator.hasNext()) {
//                labels.add(keyIterator.next() + "");
//            }
//        }
//    }
        public void reset(Map<String, Integer> map) {
        if (map != null) {
            Iterator<Integer> valueIterator = map.values().iterator();
            Iterator<String> keyIterator = map.keySet().iterator();
            int  max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
            LinkedList<Double> dataSeries2 = new LinkedList<Double>();
            LineData lineData2 = new LineData("", dataSeries2, Color.rgb(93,205,191));
            lineData2.setDotStyle(XEnum.DotStyle.DOT);
            lineData2.getPlotLine().getDotPaint().setColor(Color.rgb(93,205,191));
            lineData2.setLabelVisible(true);
            while (valueIterator.hasNext()) {
                int value = valueIterator.next();
                max = max > value ? max : value;
                min = min < value ? min : value;
                dataSeries2.add((double)value);
            }
            chartData.clear();
            chartData.add(lineData2);

            //数据轴最大值
            chart.getDataAxis().setAxisMax(max + 10);
            //数据轴最小值
            chart.getDataAxis().setAxisMin(min - 10 > 0 ? (min - 10) : 0);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(10);
            labels.clear();
            while (keyIterator.hasNext()) {
                labels.add(keyIterator.next() + "");
            }
        }
    }
}
