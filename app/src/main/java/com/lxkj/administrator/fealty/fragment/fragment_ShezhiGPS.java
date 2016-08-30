package com.lxkj.administrator.fealty.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SPManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.ui.picker.CustomHeaderAndFooterPicker;
import com.lxkj.administrator.fealty.ui.picker.HeaderAndFooterPicker;
import com.lxkj.administrator.fealty.ui.picker.OptionPicker;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.shezhi_gps)
public class fragment_ShezhiGPS extends BaseFragment implements View.OnClickListener {
    //jiancegps,relative_sleep_gps,sport_gps
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.jiancegps)
    private RelativeLayout jianceGps;//间测定位
    @ViewInject(R.id.relative_sleep_gps)
    private RelativeLayout sleep_gps;//睡眠时定位
    @ViewInject(R.id.sport_gps)
    private RelativeLayout sport_gps;//运动定位
    @ViewInject(R.id.sleep_show)
    private TextView sleep_show;
    @ViewInject(R.id.sport_show)
    private TextView sport_show;
    @ViewInject(R.id.jiance_show)
    private TextView jiance_show;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("GPS");
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
        //设置点击监听
        bar_back.setOnClickListener(this);
        jianceGps.setOnClickListener(this);
        sport_gps.setOnClickListener(this);
        sleep_gps.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.relative_sleep_gps://点击设置睡眠时多少小时上传一次GPS定位信息，弹出时间数字选择框
                ArrayList list = new ArrayList();
                for (int i = 1; i <= 24; i++) {
                    list.add(i+"");
                }
//                CustomHeaderAndFooterPicker picker = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
//                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
//                }, "请选择睡眠时定位时间：");
                HeaderAndFooterPicker picker = new HeaderAndFooterPicker(getActivity(), list,"请选择睡眠时定位时间：");
                showhourNumber(picker, "小时/次", new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        editor.putString(ParameterManager.SHEZHI_SLEEP_GPS, option);
                        editor.commit();
                        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                        sleep_show.setText(option);
                    }
                });
                break;
            case R.id.sport_gps://点击设置运动时多少分钟上传一次GPS定位信息，弹出时间数字选择框
//                CustomHeaderAndFooterPicker picker1 = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
//                        "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
//                }, "请选择运动时定位时间：");
                ArrayList list1 = new ArrayList();
                for (int i = 1; i <= 59; i++) {
                    list1.add(i+"");
                }
                HeaderAndFooterPicker picker1 = new HeaderAndFooterPicker(getActivity(), list1,"请选择运动时定位时间：");
                showhourNumber(picker1, "分钟/次", new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        editor.putString(ParameterManager.SHEZHI_SPORT_GPS, option);
                        editor.commit();
                        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                        sport_show.setText(option);
                    }
                });
                break;
            case R.id.jiancegps:
//                CustomHeaderAndFooterPicker picker2 = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
//                        "3", "5", "7", "9", "15", "25", "30", "35", "40", "45", "50", "55"
//                }, "请选择间测定位时间：");
                ArrayList list2 = new ArrayList();
                for (int i = 1; i <= 59; i++) {
                    list2.add(i+"");
                }
                HeaderAndFooterPicker picker2 = new HeaderAndFooterPicker(getActivity(), list2,"请选择间测定位时间：");
                showhourNumber(picker2, "分钟/次", new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        editor.putString(ParameterManager.SHEZHI_JIANCEGPS, option);
                        editor.commit();
                        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                        jiance_show.setText(option);
                    }
                });
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
