package com.lxkj.administrator.fealty.fragment;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SPManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.utils.AppUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_splash)
public class SplashFagment extends BaseFragment {
    Timer timer;
    private long beforeTime;//程序进入的时刻
    private SharedPreferences sharedPreferences;

    @Override
    protected void init() {
        beforeTime = System.currentTimeMillis();
        sharedPreferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2秒跳到主界面
        timer = new Timer();
        // 1)检查版本版本号
        // 2)初始缓存数据
        // 3)加载广告图
        // 4)...
    }

    @Override
    public void onResume() {
        super.onResume();
        if (beforeTime - sharedPreferences.getLong(ParameterManager.LOGIN_TIME, 0) < 1000 * 60 * 60 * 8 ) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainTabsFragemnt mainTabsFragemnt = new MainTabsFragemnt();
                    EventBus.getDefault().post(new NavFragmentEvent(mainTabsFragemnt));
                }
            }, 3000);
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    goToLoginFragment();
                }
            }, 3000);
        }
    }
    @Override
    protected void initListener() {


    }


    private void goToLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        //跳转到LoginFragment
        // MainActivity mainActivity= (MainActivity) getActivity();
        //   mainActivity.startFragment(loginFragment, null);
        EventBus.getDefault().post(new NavFragmentEvent(loginFragment));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean finish() {
        return true;
    }

}
