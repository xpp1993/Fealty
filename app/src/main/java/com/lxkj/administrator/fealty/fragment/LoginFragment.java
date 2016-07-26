package com.lxkj.administrator.fealty.fragment;

import android.view.View;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_login)
public class LoginFragment extends BaseFragment {
    @ViewInject(R.id.tv_login)
    private TextView tv_login;

    //    protected View initView(LayoutInflater inflater, ViewGroup container) {
//        View view= inflater.inflate(R.layout.fragment_login, container, false);
//        return view;
//    }
    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {
//        tv_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goToMainTabsClick();
//            }
//        });
    }

    @Event(R.id.tv_login)
    private void goToMainTabsClick(View view) {
//        switch (view.getId()){
//            case R.id.tv_login:
//                break;
//            case R.id.tv_regist:
//                break;
//            case R.id.tv_forgetpw:
//                break;
//        }
        MainTabsFragemnt mainTabsFragemnt = new MainTabsFragemnt();
        EventBus.getDefault().post(new NavFragmentEvent(mainTabsFragemnt));
    }

    @Event(R.id.tv_forgetpw)
    private void goToResetPassword(View view) {
       ResetPasswordFragment resetPasswordFragment=new ResetPasswordFragment();
        EventBus.getDefault().post(new NavFragmentEvent(resetPasswordFragment));
    }
    @Event(R.id.tv_regist)
    private void goToRegist(View view) {
        RegistFragment registFragment = new RegistFragment();
        EventBus.getDefault().post(new NavFragmentEvent(registFragment));
    }

    @Override
    protected void initData() {

    }
}
