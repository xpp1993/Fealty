package com.lxkj.administrator.fealty.ui.picker;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Administrator on 2016/8/29.
 */
public class CustomCenter extends CustomHeaderAndFooterPicker {
    private String text = null;

    public CustomCenter(Activity activity, String[] options, String str) {
        super(activity, options, null);
        text = str;
    }

    /**
     * 重写中间的view
     * @return
     */
    @NonNull
    @Override
    protected View makeCenterView() {
        return super.makeCenterView();
    }
}
