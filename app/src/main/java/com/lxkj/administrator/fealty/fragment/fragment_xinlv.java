package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

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
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("心率");

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
}
