package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.utils.ToastUtils;

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
    @ViewInject(R.id.tv_forgetpw)
    private TextView tv_forgerpw;
    @ViewInject(R.id.tv_regist)
    private TextView tv_regist;
    private String phone;
    private String password;
    @ViewInject(R.id.login_et_phone)
    private EditText login_phone_edittext;
    @ViewInject(R.id.login_et_password)
    private EditText login_password_edittext;
    public static final int REQUEST_CODE_LOGIN_COMMIT = 0X12;
    public static final int REQUEST_CODE_UPLOAD_FRIEND = 0X20;
    private final int REQUIRE_CODE_CHEEK_SESSION = 0X14;
    private final int REQUIRE_CODE_READ_CONTACT_FROM_SP = 0X13;
    private boolean showPassword;

    private long beforeTime;
    private long delayTime;//最短睡眠时间
    private long afterTime;

    public static final int MESSAGE_WHAT_LOGIN_LOGINING = 1;
    public static final int MESSAGE_WHAT_LOGIN_LOGINSUCCESS = 2;
    public static final int MESSAGE_WHAT_LOGIN_LOGINFAIL = 3;
    public static final int MESSAGE_WHAT_LOGIN_COMMIT = 0X10;
    private static long DEFAULTTIME = 500;//登录过程最少持续时间
    @ViewInject(R.id.login_iv_password_see_or_hidden)
    private ImageView seePasswordImageView;
    @Override
    protected void init() {
     EventBus.getDefault().register(this);
    }
    // 用EventBus 来导航,订阅者
    public void onEventMainThread(NavFragmentEvent event) {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void initListener() {
seePasswordImageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (showPassword) {
            login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_normal);
        } else {
            login_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_active);
        }
        showPassword = !showPassword;
    }
});
    }

    /**
     * 登录
     *
     * @param view
     */
    @Event(R.id.tv_login)
    private void goToMainTabsClick(View view) {
        phone = login_phone_edittext.getText().toString().trim();
        password = login_password_edittext.getText().toString().trim();
        doLogin(phone, password);
       this.isHidden();
    }

    /**
     * 处理登录的方法
     *
     * @param phone
     * @param password
     */
    private void doLogin(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            //  Toast.makeText(getApplicationContext(), "输入手机号不能为空！", Toast.LENGTH_SHORT).show();
            ToastUtils.showToastInUIThread("输入手机号不能为空！");
        } else if (TextUtils.isEmpty(password)) {
            //  Toast.makeText(getApplicationContext(), "输入密码不能为空！", Toast.LENGTH_SHORT).show();
            ToastUtils.showToastInUIThread("输入密码不能为空！");
        } else {
            //  handler.sendEmptyMessage(MESSAGE_WHAT_LOGIN_LOGINING);//通知UI ，正在执行登录联网操作
            // beforeTime = System.currentTimeMillis();
            // userid = CommonTools.encodeByMD5(phone);
            //RequireServiceManager.getRequireServiceManager().requireToLogin(this, userid, password, REQUEST_CODE_LOGIN_COMMIT, this);
            MainTabsFragemnt mainTabsFragemnt = new MainTabsFragemnt();
            EventBus.getDefault().post(new NavFragmentEvent(mainTabsFragemnt));
            this.isHidden();
        }
    }

    @Event(R.id.tv_forgetpw)
    private void goToResetPassword(View view) {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        EventBus.getDefault().post(new NavFragmentEvent(resetPasswordFragment));
        this.isHidden();
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
