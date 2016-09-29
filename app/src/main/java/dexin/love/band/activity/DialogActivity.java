package dexin.love.band.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import dexin.love.band.R;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.widget.NumberPicker;

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
    private TextView tv_fw;
    private String title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_item);// 设置布局内容
        //只对api19以上版本有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        //为状态栏着色
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.MainTheme);
        init();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        title = bundle.getString("title");
        maxRate = bundle.getInt("max");
        minRate = bundle.getInt("min");
        tv_fw.setText(title);
        numberPicker1.setValue(minRate);
        numberPicker1.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                minRate = newVal;
            }
        });
        numberPicker2.setValue(maxRate);
        numberPicker2.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                maxRate = newVal;
            }
        });
    }

    //初始化
    private void init() {
        numberPicker1 = (NumberPicker) findViewById(R.id.np1);
        numberPicker2 = (NumberPicker) findViewById(R.id.np2);
        cancel = (Button) findViewById(R.id.cancel);
        commit = (Button) findViewById(R.id.commit);
        cancel.setOnClickListener(this);
        commit.setOnClickListener(this);
        tv_fw = (TextView) findViewById(R.id.tv_fw);
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
        finishRunnable = new finishRunnable();
        handler = new Handler();
        numberPicker1.setMinValue(50);
        numberPicker1.setMaxValue(90);
        numberPicker2.setMinValue(95);
        numberPicker2.setMaxValue(140);
    }
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.commit:
                if (title.equals(getResources().getString(R.string.normal_title))) {//如果是正常心率范围
                    editor.putInt(ParameterManager.NORMAL_RATE_MAX_MINUTE, maxRate);
                    editor.putInt(ParameterManager.NORMAL_RATE_MIN_MINUTE, minRate);
                    //把选择的数值返回给设置页面的控件显示
                    showText(ParameterManager.REQURST_CODE_NORMAL);
                } else if (title.equals(getResources().getString(R.string.sleep_title))) {//如果是睡眠时心率范围
                    editor.putInt(ParameterManager.SLEEP_RATE_MAX_MINUTE, maxRate);
                    editor.putInt(ParameterManager.SLEEP_RATE_MIN_MINUTE, minRate);
                    showText(ParameterManager.REQURST_CODE_SLEEP);
                } else if (title.equals(getResources().getString(R.string.sport_title))) {//如果是运动时心率范围
                    editor.putInt(ParameterManager.SPORT_RATE_MAX_MINUTE, maxRate);
                    editor.putInt(ParameterManager.SPORT_RATE_MIN_MINUTE, minRate);
                    showText(ParameterManager.REQURST_CODE_SPORT);
                }
                editor.commit();
                Toast.makeText(this, "设置成功!心率范围为：" + minRate + "-" + maxRate, Toast.LENGTH_SHORT).show();
                handler.postDelayed(finishRunnable, 1500);
                break;
            default:
                break;
        }
    }

    /**
     * 把数据发给Fragment
     * @param resultcode
     */
    private void showText(int resultcode) {
        Intent intent = new Intent();
        Bundle data = new Bundle();
        data.putString("minmax", minRate + "-" + maxRate);
        intent.putExtra("data", data);
        setResult(resultcode, intent);
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
