package com.lxkj.administrator.fealty.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SPManager;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.widget.NumberPicker;

/**
 * Created by Administrator on 2016/8/30.
 */
public class DialogActivity extends Activity implements View.OnClickListener {
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private Button cancel;
    private Button commit;
    int minRate = 60;
    int maxRate = 100;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private finishRunnable finishRunnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_item);// 设置布局内容
        numberPicker1 = (NumberPicker) findViewById(R.id.np1);
        numberPicker2 = (NumberPicker) findViewById(R.id.np2);
        cancel = (Button) findViewById(R.id.cancel);
        commit = (Button) findViewById(R.id.commit);
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
        finishRunnable = new finishRunnable();
        handler = new Handler();
        numberPicker1.setMinValue(50);
        numberPicker1.setMaxValue(80);
        numberPicker1.setValue(minRate);
        numberPicker1.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                minRate = newVal;
            }
        });
        numberPicker2.setMinValue(90);
        numberPicker2.setMaxValue(140);
        numberPicker2.setValue(maxRate);
        numberPicker2.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                maxRate = newVal;
            }
        });
        cancel.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.commit:
                editor.putInt(ParameterManager.SLEEP_RATE_MAX_MINUTE, maxRate);
                editor.putInt(ParameterManager.SLEEP_RATE_MIN_MINUTE, minRate);
                editor.commit();
                Toast.makeText(this, "设置成功,睡眠心率范围为：" + minRate + "-" + maxRate, Toast.LENGTH_SHORT).show();
                handler.postDelayed(finishRunnable, 1500);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (finishRunnable != null) {
            handler.removeCallbacks(finishRunnable);
        }
    }

    private class finishRunnable implements Runnable {

        @Override
        public void run() {
            finish();
        }
    }
}
