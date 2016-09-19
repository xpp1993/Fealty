package dexin.love.band.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.event.NavFragmentEvent;

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
    @ViewInject(R.id.shezhi_ziti)
    private RelativeLayout shezhi_ziti;//字体设置
    @Override
    protected void init() {
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("设置");
        message_warn.setOnClickListener(this);
        shezhi_ziti.setOnClickListener(this);
        xinlv.setOnClickListener(this);
        gps_shezhi.setOnClickListener(this);
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);

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
            case R.id.shezhi_ziti://调整字体大小
                break;
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
