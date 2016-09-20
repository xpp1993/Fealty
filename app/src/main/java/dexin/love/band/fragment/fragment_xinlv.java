package dexin.love.band.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.activity.DialogActivity;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.ui.picker.HeaderAndFooterPicker;
import dexin.love.band.ui.picker.OptionPicker;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.xinlv)
public class fragment_xinlv extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
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
    @ViewInject(R.id.sleepxinlv_show)
    private TextView sleepxinlv_show;
    @ViewInject(R.id.sport_show)
    private TextView sport_show;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @ViewInject(R.id.fanwei_show)
    private TextView fanwei_show;
    @ViewInject(R.id.jaincetime_show)
    private TextView jaincetime_show;
    public static final int REQUEST_CODE_SELF_DATA_ALTER = 0X310;
    private MyHandler handler = new MyHandler();

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

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        String jaince = preferences.getString(ParameterManager.SHEZHI_JIANCEXINLV, 3 + "");
        jaincetime_show.setText(jaince);
        int sleepmax = preferences.getInt(ParameterManager.SLEEP_RATE_MAX_MINUTE, 100);
        int sleepmin = preferences.getInt(ParameterManager.SLEEP_RATE_MIN_MINUTE, 60);
        int sportmax = preferences.getInt(ParameterManager.SPORT_RATE_MAX_MINUTE, 120);
        int sportmin = preferences.getInt(ParameterManager.SPORT_RATE_MIN_MINUTE, 90);
        int normalmax = preferences.getInt(ParameterManager.NORMAL_RATE_MAX_MINUTE, 120);
        int normalmin = preferences.getInt(ParameterManager.NORMAL_RATE_MIN_MINUTE, 60);
        sport_show.setText(sportmin + "-" + sportmax);
        fanwei_show.setText(normalmin + "-" + normalmax);
        sleepxinlv_show.setText(sleepmin + "-" + sleepmax);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.jiancetime://弹出dialog
//                CustomHeaderAndFooterPicker picker2 = new CustomHeaderAndFooterPicker(getActivity(), new String[]{
//                        "3", "5", "7", "9", "15", "25", "30", "35", "40", "45", "50", "55"
//                }, "请选择心率间测时间：");
                ArrayList list2 = new ArrayList();
                for (int i = 3; i <= 30; i++) {
                    list2.add(i + "");
                }
                HeaderAndFooterPicker picker2 = new HeaderAndFooterPicker(getActivity(), list2, "请选择心率间测时间：");
                showhourNumber(picker2, "分钟/次", new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int position, String option) {
                        editor.putString(ParameterManager.SHEZHI_JIANCEXINLV, option);
                        editor.commit();
                        jaincetime_show.setText(option);
                        Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.sleep_xinlv:
                Intent intent_sleep = new Intent(getActivity(), DialogActivity.class);
                bundle = getValue(sleepxinlv_show);
                bundle.putString("title", getResources().getString(R.string.sleep_title));
                intent_sleep.putExtra("bundle", bundle);
                // startActivity(intent_sleep);
                startActivityForResult(intent_sleep, ParameterManager.REQURST_CODE_SLEEP);
                break;
            case R.id.sport_xinlv:
                Intent intent_sport = new Intent(getActivity(), DialogActivity.class);
                bundle = getValue(sport_show);
                bundle.putString("title", getResources().getString(R.string.sport_title));
                intent_sport.putExtra("bundle", bundle);
                // startActivity(intent_sport);
                startActivityForResult(intent_sport, ParameterManager.REQURST_CODE_SPORT);
                break;
            case R.id.baojinfanwei:
                Intent intent_normal = new Intent(getActivity(), DialogActivity.class);
                bundle = getValue(fanwei_show);
                bundle.putString("title", getResources().getString(R.string.normal_title));
                intent_normal.putExtra("bundle", bundle);
                // startActivity(intent_normal);
                startActivityForResult(intent_normal, ParameterManager.REQURST_CODE_NORMAL);
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
     *
     * @param picker
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

    /**
     * 获得控件上的心率范围边界
     */
    private Bundle getValue(TextView textView) {
        String xinlvfanwei = (String) textView.getText();
        String fanwei[] = xinlvfanwei.split("-");
        int max = Integer.parseInt(fanwei[1]);
        int min = Integer.parseInt(fanwei[0]);
        Log.e("fanwei", min + "-" + max);
        Bundle bundle = new Bundle();
        bundle.putInt("max", max);
        bundle.putInt("min", min);
        return bundle;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String str = data.getBundleExtra("data").getString("minmax");
        if (str == null || str.equals(""))
            return;
        if (requestCode == ParameterManager.REQURST_CODE_NORMAL) {
            if (resultCode == ParameterManager.REQURST_CODE_NORMAL) {
                fanwei_show.setText(str);
                //上传到服务器
                UserInfo userInfo = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
                Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "nickName", "headFile", "birthday", "sex", "xinLvFanWei"}, userInfo.getMobile(), "", "", "", "", str);
                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_USER_MSG, params, null, REQUEST_CODE_SELF_DATA_ALTER, fragment_xinlv.this);
            }
        } else if (requestCode == ParameterManager.REQURST_CODE_SPORT) {
            if (resultCode == ParameterManager.REQURST_CODE_SPORT) {
                sport_show.setText(str);
            }
        } else if (requestCode == ParameterManager.REQURST_CODE_SLEEP) {
            if (resultCode == ParameterManager.REQURST_CODE_SLEEP) {
                sleepxinlv_show.setText(str);
            }
        }
    }


    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_SELF_DATA_ALTER:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器返回结果错误");
                }
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_SELF_DATA_ALTER://个人信息修改
                    if (msg.getData().getInt("code") == 1) //请求成功
                        ToastUtils.showToastInUIThread("上传成功");
                    break;
            }
        }
    }
}