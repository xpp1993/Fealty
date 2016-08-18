package com.lxkj.administrator.fealty;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.fragment.LoginFragment;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * fragment跳转，返回键的控制
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends FragmentActivity {
    //声明Fragmentmanager
    private FragmentManager fm;
    //tag 容器，管理Fragment的tag
    private LinkedList<String> mFragments = new LinkedList<String>();
    public static final int LAST_CLICK_GAP = 600;
    public static final int EXIT_GAP = 2000;
    public long lastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_main);
        // 注入xutils3 的view
        x.view().inject(this);
        // EventBus 注册
        fm = getSupportFragmentManager();
        EventBus.getDefault().register(this);
      LoginFragment baseFragment;
    //  MainTabsFragemnt baseFragment;
        String tag;
       baseFragment = new LoginFragment();
       // baseFragment=new MainTabsFragemnt();
       tag = baseFragment.getMTag();
        mFragments.add(tag);

        fm.beginTransaction().replace(R.id.main_container, baseFragment, tag).addToBackStack(tag).commit();
    }
    //监听返回键
    //按返回键，Fragment要不要禁用返回键
    //假如fragment不控制返回键---》MainActivity 管理东西---->mFragments.pollLast();fm.popBackStack
//                                                      只有一个Fragment情况下，
//                                                      MainActivty.finish();
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!backCurrentFragment()) {
                goBack();
            }
            return true;//消费
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        int count = fm.getBackStackEntryCount();//
        if (count == 1) {
            if ((SystemClock.uptimeMillis() - lastClickTime) > EXIT_GAP) {
                ToastUtils.showToastInUIThread("再按一次退出程序！");
                lastClickTime = SystemClock.uptimeMillis();
            } else {
                MainActivity.this.finish();
            }
        } else {
            if (mFragments.size() > 0) {
                mFragments.pollLast();
            }
            Log.d("lanxin", "mFragments.size()" + mFragments.size());
            fm.popBackStack();
        }
    }


    //Fragment控制返回键
    public boolean backCurrentFragment() {
        // 获取当前的Fragment
        BaseFragment currentFragment = (BaseFragment) getCurrentFragment();
        if (currentFragment != null) {
            return currentFragment.onBack();
        }
        return false;
    }

    // 用EventBus 来导航,订阅者
    public void onEventMainThread(NavFragmentEvent event) {
        BaseFragment fragment = event.fragment;
        Bundle bundle = event.bundle;
        startFragment(fragment, bundle);
    }

    // 中转站：控制Fragment之间的跳转
    public void startFragment(BaseFragment fragment, Bundle bundle) {
        if (fragment == null) {
            throw new IllegalArgumentException("fragment is null");
        }
        if (lastClickTime + LAST_CLICK_GAP < SystemClock.uptimeMillis()) {
            //把tag添加到mFragments
            // 1 获取tag
            String tag = fragment.getMTag();

            //添加传过来的Fragment
            // 2 获取事务
            FragmentTransaction ft = fm.beginTransaction();
            // 3 添加Fragment
            ft.add(R.id.main_container, fragment, tag);

            // 4 添加传过bundle
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            //找到当前Fragment
            // 5 隐藏当前或者finish的Fragment
            BaseFragment currFragment = (BaseFragment) getCurrentFragment();
            //隐藏当前的Fragment
            //控制当前Fragment onFinish
            if (currFragment != null) {
                if (currFragment.finish()) {
                    mFragments.pollLast();
                    fm.popBackStack();//finish
                    //由于当前的Fragment，被弹出去，当前的Fragment已经变化角色，需要重新找到并隐藏
                    currFragment = (BaseFragment) getCurrentFragment();
                    if (currFragment != null) {
                        ft.hide(currFragment);
                    }
                } else {
                    ft.hide(currFragment);// hide
                }
            }
            // 6 把tag 添加到mFragments
            mFragments.add(tag);

            //Fragment添加到返回栈，commit
            // 7 添加到返回栈
            ft.addToBackStack(tag);
            // 8 添加事务
            ft.commit();
            lastClickTime = SystemClock.uptimeMillis();
        }

    }

    //找到当前的fragment
    public Fragment getCurrentFragment() {
        return mFragments.size() > 0 ? fm.findFragmentByTag(mFragments.peekLast()) : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
