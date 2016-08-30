package com.lxkj.administrator.fealty.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.lxkj.administrator.fealty.R;

/**
 * Created by Carl on 2016-08-13 013.
 */
public class PPView extends View {
    //外圈颜色
    private int outerCircleColor;
    //外圈半径
    private float outerCircleRadius;
    //外圈环宽
    private float outerCircleWidth;
    //内圈颜色
    private int innerCircleColor;
    //内圈半径
    private float innerCircleRadius;
    //内圈环宽
    private float innerCircleWidth;

    private float firstTextSize;
    private float secondTextSize;
    private float thirdTextSize;
    private float fourthTextSize;

    private int firstTextColor;
    private int secondTextColor;
    private int thirdTextColor;
    private int fourthTextColor;

    private Bitmap bitmap;

    private Paint mPaint;

    private String firstText = "我";
   // private String secondText = "健康";
   private String secondText = "80";
    private String thirdText = "实时心率";
  //  private String fountText = "80";

//    public void setFountText(String fountText) {
//        this.fountText = fountText;
//    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public PPView(Context context) {
        this(context, null);
    }

    public PPView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PPView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PPView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.PPView_firstTextColor:
                    firstTextColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_firstTextSize:
                    firstTextSize = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_secondTextColor:
                    secondTextColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_secondTextSize:
                    secondTextSize = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_thirdTextColor:
                    thirdTextColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_thirdTextSize:
                    thirdTextSize = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_fourthTextColor:
                    fourthTextColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_fourthTextSize:
                    fourthTextSize = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_outerCircleColor:
                    outerCircleColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_outerCircleRadius:
                    outerCircleRadius = a.getDimension(attr, 36);
                    break;
                case R.styleable.PPView_outerCircleWidth:
                    outerCircleWidth = a.getDimension(attr, 6);
                    break;
                case R.styleable.PPView_innerCircleColor:
                    innerCircleColor = a.getColor(attr, Color.rgb(93, 205, 191));
                    break;
                case R.styleable.PPView_innerCircleRadius:
                    innerCircleRadius = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_innerCircleWidth:
                    innerCircleWidth = a.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.PPView_bitmap:
                    bitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;

            }
        }
        a.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);  //消除锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? (int) Math.max(sizeWidth, (outerCircleRadius + outerCircleWidth) * 2)
                : (int) (outerCircleRadius + outerCircleWidth) * 2, (modeHeight == MeasureSpec.EXACTLY) ? (int) Math.max(sizeHeight, (outerCircleRadius + outerCircleWidth) * 2)
                : (int) (outerCircleRadius + outerCircleWidth) * 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 画外层的大圆环
         */
        mPaint.setColor(outerCircleColor); //设置圆环的颜色
        mPaint.setStyle(Paint.Style.STROKE); //设置空心
        mPaint.setStrokeWidth((outerCircleWidth)); //设置圆环的宽度
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, outerCircleRadius, mPaint); //画出圆环

        /**
         * 画内层的大圆环
         */
        mPaint.setColor(innerCircleColor); //设置圆环的颜色
        mPaint.setStyle(Paint.Style.STROKE); //设置空心
        mPaint.setStrokeWidth((innerCircleWidth)); //设置圆环的宽度
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, innerCircleRadius, mPaint); //画出圆环

        /**
         * 画第一行文本
         */
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(firstTextColor);
        mPaint.setTextSize(firstTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        float textWidth = mPaint.measureText(firstText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(firstText, (getWidth() - textWidth) / 2, (getHeight() - secondTextSize - firstTextSize/2) / 2, mPaint); //

        /**
         * 画第二行文本
         */
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(secondTextColor);
        mPaint.setTextSize(secondTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        textWidth = mPaint.measureText(secondText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(secondText, (getWidth() - textWidth) / 2, (getHeight() + secondTextSize) / 2, mPaint); //

        /**
         * 画第三行文本
         */
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(thirdTextColor);
        mPaint.setTextSize(thirdTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        textWidth = mPaint.measureText(thirdText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        int maxHeight = (int) Math.max(thirdTextSize, bitmap.getHeight());
        canvas.drawText(thirdText, (getWidth() - textWidth + bitmap.getWidth() + 2) / 2, (getHeight() + secondTextSize + 3*maxHeight + thirdTextSize) / 2, mPaint); //画出进度百分比

        /**
         * 画图片
         */
        canvas.drawBitmap(bitmap, (getWidth() - textWidth - bitmap.getWidth() - 2) / 2, (getHeight() + secondTextSize+3*maxHeight - bitmap.getHeight()) / 2, mPaint);

//        /**
//         * 画第四行文本
//         */
//        mPaint.setStrokeWidth(0);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(fourthTextColor);
//        mPaint.setTextSize(fourthTextSize);
//        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
//        textWidth = mPaint.measureText(fountText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
//        canvas.drawText(fountText, (getWidth() - textWidth) / 2,  (getHeight() + secondTextSize+3*maxHeight + 3*fourthTextSize) / 2, mPaint); //


    }


}
