package dexin.love.band.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leaking.slideswitch.SlideSwitch;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;

/**
 * Created by Administrator on 2016/8/22.
 */
@ContentView(R.layout.shezhi)
public class Fragment_Shezhi extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.shezhi_messagewarn)
    private RelativeLayout message_warn;//消息提醒设置
    @ViewInject(R.id.shezhi_xinlv)
    private RelativeLayout xinlv;//心率设置
    @ViewInject(R.id.shezhi_GPS)
    private RelativeLayout gps_shezhi;//GPS设置
    //    @ViewInject(R.id.shezhi_ziti)
//    private RelativeLayout shezhi_ziti;//字体设置
    @ViewInject(R.id.testrate)
    public SlideSwitch testRate;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void init() {
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("设置");
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
    }

    @Override
    protected void initListener() {
        message_warn.setOnClickListener(this);
//        shezhi_ziti.setOnClickListener(this);
        xinlv.setOnClickListener(this);
        gps_shezhi.setOnClickListener(this);
        bar_back.setOnClickListener(this);
        testRate.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                editor.putBoolean(ParameterManager.TEST_RATE, true);
                editor.commit();
            }
            @Override
            public void close() {
                editor.putBoolean(ParameterManager.TEST_RATE, false);
                editor.commit();
            }
        });
    }

    @Override
    protected void initData() {

    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(Bundle event) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bar_iv_left:
                EventBus.getDefault().post(new String("刷新界面"));
                getActivity().onBackPressed();
                break;
            case R.id.shezhi_GPS://跳转到GPS设置界面
                EventBus.getDefault().post(new NavFragmentEvent(new fragment_ShezhiGPS()));
                break;
            case R.id.shezhi_xinlv://跳转到心率设置界面
                EventBus.getDefault().post(new NavFragmentEvent(new fragment_xinlv()));
                break;
            case R.id.shezhi_messagewarn://跳转到消息设置界面
                EventBus.getDefault().post(new NavFragmentEvent(new fragment_messagewarn()));
                break;
//            case R.id.shezhi_ziti://调整字体大小
//                break;
            default:
                break;
        }
    }

    @Override
    public boolean onBack() {
        EventBus.getDefault().post(new String("刷新界面"));
        return super.onBack();
    }
}
