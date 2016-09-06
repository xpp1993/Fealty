package com.lxkj.administrator.fealty.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseView;

/**
 * Created by XPP on 2016/9/4/004.
 */
public class DialogViewSleepLow extends BaseView {
    private Button button;

    public DialogViewSleepLow(Context context) {
        super(context);
    }

    public Button getButton() {
        return button;
    }

    @Override
    protected void initView() {
        View view = View.inflate(getContext(), R.layout.dialog_sleeplow, this);
        button = (Button) view.findViewById(R.id.dialogbtn);
    }
}