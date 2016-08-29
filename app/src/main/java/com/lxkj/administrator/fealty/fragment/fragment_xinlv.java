package com.lxkj.administrator.fealty.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SPManager;
import com.lxkj.administrator.fealty.ui.picker.CustomHeaderAndFooterPicker;
import com.lxkj.administrator.fealty.ui.picker.HeaderAndFooterPicker;
import com.lxkj.administrator.fealty.ui.picker.OptionPicker;
import com.lxkj.administrator.fealty.utils.AppUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.xinlv)
public class fragment_xinlv extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.baojinfanwei)
    private RelativeLayout xinlv_fanwei;//设置正常心率范围
    @ViewInject(R.id.jiancetime)
    private RelativeLayout jiancetime;//设置间测时间
    @ViewInject(R.id.sport_xinlv)
    private RelativeLayout sport_xinlvfanwei;//设置运动时心率范围
    @ViewInject(R.id.sleep_xinlv)
    private RelativeLayout sleep_xinlv_fanwei;//设置睡眠时心率范围
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("心率");
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();

    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }
    @Override
    protected void initListener() {
        //设置监听
        bar_back.setOnClickListener(this);
        xinlv_fanwei.setOnClickListener(this);
        sleep_xinlv_fanwei.setOnClickListener(this);
        sport_xinlvfanwei.setOnClickListener(this);
        jiancetime.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jiancetime://弹出dialog
//                CustomHeaderAndFooterPicker picker2 = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
//                        "3", "5", "7", "9", "15", "25", "30", "35", "40", "45", "50", "55"
//                }, "请选择心率间测时间：");
                ArrayList list2 = new ArrayList();
                for (int i = 1; i <= 59; i++) {
                    list2.add(i);
                }
                HeaderAndFooterPicker picker2 = new HeaderAndFooterPicker(getActivity(), list2,"请选择心率间测时间：");
                showhourNumber(picker2, "分钟/次", new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        editor.putString(ParameterManager.SHEZHI_JIANCEXINLV, option);
                        editor.commit();
                        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.sleep_xinlv:
                break;
            case  R.id.sport_xinlv:
                break;
            case R.id.baojinfanwei:
                break;
            case R.id.bar_iv_left:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }
    /**
     * 显示dialog
     *  @param picker
     * @param str
     * @param listener
     */
    private void showhourNumber(HeaderAndFooterPicker picker, String str, OptionPicker.OnOptionPickListener listener) {
        picker.setSelectedIndex(1);
        picker.setLabel(str);
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setTopLineColor(0xFF33B5E5);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setTextColor(0xFF33B5E5, 0xFF999999);
        picker.setLineColor(0xFF33B5E5);
        picker.setAnimationStyle(R.style.Animation_Popup);
        picker.setOnOptionPickListener(listener);
        picker.show();
    }

}
