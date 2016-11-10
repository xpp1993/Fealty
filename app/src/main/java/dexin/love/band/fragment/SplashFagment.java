package dexin.love.band.fragment;

import android.content.SharedPreferences;

import org.xutils.view.annotation.ContentView;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.utils.AppUtils;

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
        if (beforeTime - sharedPreferences.getLong(ParameterManager.LOGIN_TIME, 0) < 1000 * 60 * 60 * 8) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainTabsFragemnt mainTabsFragemnt = new MainTabsFragemnt();
                    EventBus.getDefault().post(new NavFragmentEvent(mainTabsFragemnt));
//                    finish();//2016-9-20
                }
            }, 2000);
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    goToLoginFragment();
                }
            }, 2000);
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
//        finish();//2016-9-20
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

    @Override
    public boolean onBack() {
        return false;
    }
}
