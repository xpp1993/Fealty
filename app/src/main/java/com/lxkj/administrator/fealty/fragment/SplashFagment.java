package com.lxkj.administrator.fealty.fragment;
import android.view.View;
import android.widget.TextView;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_splash)
public class SplashFagment extends BaseFragment  {
    @ViewInject(R.id.splash_tv)
    private TextView splash_tv;
//    protected View initView(LayoutInflater inflater, ViewGroup container) {
//       View view= inflater.inflate(R.layout.fragment_splash,container,false);
//        splash_tv= (TextView) view.findViewById(R.id.splash_tv);
//
//        //注册eventBus的事件
//       // EventBus.getDefault().register(this);
//        return  view;
//    }

    @Override
    protected void init() {

    }

    @Override
    protected void initListener() {

        splash_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginFragment();
            }
        });

    }
//    public void onEventMainThread(GoHomeEvent event){
//      goToLoginFragment();
//    }
    private void goToLoginFragment() {
        LoginFragment loginFragment=new LoginFragment();
        //跳转到LoginFragment
        // MainActivity mainActivity= (MainActivity) getActivity();
      //   mainActivity.startFragment(loginFragment, null);
        EventBus.getDefault().post(new NavFragmentEvent(loginFragment));
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean finish() {
        return true;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }

}
