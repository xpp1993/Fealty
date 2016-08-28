package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leaking.slideswitch.SlideSwitch;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/27/0027.
 */
@ContentView(R.layout.shezhi_baojin)
public class fragment_messagewarn extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.voidbaojing)
    private SlideSwitch voice;//设置声音提醒
    @ViewInject(R.id.zdbaojing)
    private SlideSwitch shark;//设置振动提醒
    @ViewInject(R.id.yuyinbaojing)
    private SlideSwitch yuyin;//设置语音提醒
    @ViewInject(R.id.tancbaojing)
    private SlideSwitch dialog;//设置弹窗提醒
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("消息提醒");
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);
        voice.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {//如果声音提醒是打开的，把其他的设为关闭。把状态写入sp 文件(写一个静态类表示各个开关状态)

            }
            @Override
            public void close() {

            }
        });
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
        if (v.getId()==R.id.bar_iv_left){
                getActivity().onBackPressed();
        }
    }
}
