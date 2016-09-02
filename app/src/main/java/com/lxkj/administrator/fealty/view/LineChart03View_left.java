package com.lxkj.administrator.fealty.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.GraphicalView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

public class LineChart03View_left extends GraphicalView {

    private String TAG = "LineChart03View_left";
    private LineChart chart = new LineChart();

    //标签集合
    private LinkedList<String> labels = new LinkedList<String>();
    private LinkedList<LineData> chartData = new LinkedList<LineData>();

    public LineChart03View_left(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        chartLabels();
        chartDataSet();
        chartRender();
    }

    public LineChart03View_left(Context context, AttributeSet attrs) {
        super(context, attrs);
        chartLabels();
        chartDataSet();
        chartRender();
    }


    public LineChart03View_left(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        chartLabels();
        chartDataSet();
        chartRender();
    }


    private void chartRender() {
        try {
            //设定数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);

            //数据轴最大值
            chart.getDataAxis().setAxisMax(120);
            //数据轴最小值
            chart.getDataAxis().setAxisMin(50);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(20);


            chart.getCategoryAxis().hide();

            //忽略Java的float计算误差
            chart.disableHighPrecision();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void chartDataSet() {
        LinkedList<Double> dataSeries2 = new LinkedList<Double>();
        dataSeries2.add(-100d);
        LineData lineData2 = new LineData("", dataSeries2,Color.rgb(93,205,191));
        lineData2.setDotStyle(XEnum.DotStyle.DOT);
        lineData2.getPlotLine().getDotPaint().setColor(Color.rgb(93,205,191));
        lineData2.setLabelVisible(false);
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


    @Override
    public void render(Canvas canvas) {
        try {

            //设置图表大小
            chart.setChartRange(0, 0, //10f, 10f,
                    this.getLayoutParams().width - 10,
                    this.getLayoutParams().height - 10);

          //  chart.setPadding(70, 30, 30, 60);    //70是轴所点总宽度，在右边轴绘图时，偏移这个宽度就对好了
            chart.setPadding(70, 28, 30, 90);    //70是轴所点总宽度，在右边轴绘图时，偏移这个宽度就对好了
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
//            while (valueIterator.hasNext()) {
//                int value = valueIterator.next();
//                max = max > value ? max : value;
//                min = min < value ? min : value;
//            }
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
        while (valueIterator.hasNext()) {
            int value = valueIterator.next();
            max = max > value ? max : value;
            min = min < value ? min : value;
        }
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
