package dexin.love.band;

/**
 * Created by Administrator on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import org.xutils.x;

import com.pgyersdk.update.PgyUpdateManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.fragment.SplashFagment;

/**
 * fragment跳转，返回键的控制
 */
//@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        PgyUpdateManager.register(this);//蒲公英版本更新
        fm = getSupportFragmentManager();
        //只对api19以上版本有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        //为状态栏着色
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.MainTheme);
        x.view().inject(this);
        BaseFragment baseFragment = null;
        String tag = null;
        if (savedInstanceState == null) {
            baseFragment = new SplashFagment();
            tag = baseFragment.getMTag();
        }
        if (savedInstanceState != null) {
            tag = baseFragment.getMTag();
            baseFragment = (BaseFragment) fm.findFragmentByTag(tag);
        }
        mFragments.add(tag);
        // fm.beginTransaction().add(R.id.main_container, baseFragment, tag).addToBackStack(tag).commitAllowingStateLoss();
        fm.beginTransaction().add(R.id.main_container, baseFragment, tag).addToBackStack(tag).commitAllowingStateLoss();
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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

    private void showSetBindDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        final View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_radiogroup_back, null);
        final RadioButton readRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_finish);
        RadioButton inRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_backgound);
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String newGender = readRadioButton.isChecked() ? "退出应用" : "后台运行程序";
                if (TextUtils.equals(newGender, "退出应用")) {
                    MainActivity.this.finish();
                } else if (TextUtils.equals(newGender, "后台运行程序")) {
                    moveTaskToBack(false);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setCancelable(false).create().show();
    }

    private void goBack() {
        int count = fm.getBackStackEntryCount();//
        if (count == 1) {
            showSetBindDialog();
        } else {
            if (mFragments.size() > 0) {
                mFragments.pollLast();
            }
            Log.d("lanxin", "mFragments.size()" + mFragments.size());
            //  fm.popBackStack();
            fm.popBackStackImmediate();
        }
    }

    //Fragment控制返回键
    public boolean backCurrentFragment() {
        // 获取当前的Fragment
        BaseFragment currentFragment = (BaseFragment) getCurrentFrament();
        if (currentFragment != null) {
            return currentFragment.onBack();
        }
        return false;
    }

    public void onEventMainThread(NavFragmentEvent event) {
        BaseFragment fragment = event.fragment;
        Bundle bundle = event.bundle;
        startFragment(fragment, bundle);
    }

    public void startFragment(BaseFragment fragment, Bundle bundle) {
        if (fragment == null) {
            throw new IllegalArgumentException("fragment is null");
        }
        if (lastClickTime + LAST_CLICK_GAP < SystemClock.uptimeMillis()) {
            // 1 获取tag
            String tag = fragment.getMTag();
            // 2 获取事务
            FragmentTransaction ft = fm.beginTransaction();
            // 3 控制Fragment 的动画
//            ft.setCustomAnimations(R.anim.slide_left_enter, 0, 0,R.anim.slide_right_exit);
            // 4 添加Fragment
            //   ft.add(R.id.main_container, fragment, tag);
            ft.replace(R.id.main_container, fragment, tag);
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            // 5 隐藏当前或者finish的Fragment
            BaseFragment currFragment = getCurrentFrament();
            if (currFragment != null) {
                if (currFragment.finish()) {
                    mFragments.pollLast();
                   // fm.popBackStack();//finish
                    fm.popBackStackImmediate();
//                    //由于当前的Fragment，被弹出去，需要当前的Fragment已经变化角色，需要重新隐藏
                    currFragment = (BaseFragment) getCurrentFrament();
                    if (currFragment != null) {
                        ft.hide(currFragment);
                    }
                } else {
                    ft.hide(currFragment);// hide
                }
            }
            // 6 把tag 添加到mFragments
            mFragments.add(tag);
            // 7 添加到返回栈
            ft.addToBackStack(tag);
            // 8 添加事务
            //  ft.commit();
            ft.commitAllowingStateLoss();
            lastClickTime = SystemClock.uptimeMillis();
        }
    }

    // 怎么获取当前的Fragment
    public BaseFragment getCurrentFrament() {
        return mFragments.size() > 0 ? (BaseFragment) fm.findFragmentByTag(mFragments.peekLast()) : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        System.exit(0);
        PgyUpdateManager.unregister();//蒲公英
    }
}

