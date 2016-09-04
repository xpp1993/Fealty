package com.lxkj.administrator.fealty.base;

import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.xutils.x;

/**
 * Created by Administrator on 2016/8/2/0002.
 */
public abstract class BaseView extends RelativeLayout {

    public BaseView(Context context) {
        super(context);
        initView();
        x.view().inject(this);
        initAnim();
        initData();
    }

    public void initData() {
    }

    public void initAnim() {

    }

    public void startAnim() {
    }

    protected abstract void initView();

    public abstract Button getButton() ;
}
