package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/8/22.
 */
@ContentView(R.layout.shezhi1)
public class Fragment_Shezhi extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_back;
    @ViewInject(R.id.bar_tv_title_center)
    private TextView bar_biaoti;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @Override
    protected void init() {
        bar_back.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_biaoti.setVisibility(View.VISIBLE);
        bar_biaoti.setText("设置");
    }

    @Override
    protected void initListener() {
        bar_back.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
           case  R.id.bar_iv_left:
               getActivity().onBackPressed();
            break;
           default:
               break;
        }
    }
}
