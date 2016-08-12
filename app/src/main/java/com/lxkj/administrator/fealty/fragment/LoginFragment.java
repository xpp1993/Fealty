package com.lxkj.administrator.fealty.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.bean.UserInfo;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.ui.ActionProcessButton;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.json.JSONException;
import org.xutils.view.annotation.ContentView;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_login)
public class LoginFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
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
    @ViewInject(R.id.tv_login)
    private ActionProcessButton loginButton;
    @ViewInject(R.id.login_iv_password_see_or_hidden)
    private ImageView seePasswordImageView;
    private boolean showPassword;
    public static final int REQUEST_CODE_LOGIN_COMMIT = 0X12;//登录提交
    public static final int REQUEST_CODE_UPLOAD_CONTACTS = 0X20;//上传通讯录
    private long beforeTime;
    private long delayTime;//最短睡眠时间
    private long afterTime;
    public static final int MESSAGE_WHAT_LOGIN_LOGINING = 1;
    public static final int MESSAGE_WHAT_LOGIN_LOGINFAIL = 3;
    private static long DEFAULTTIME = 500;//登录过程最少持续时间

    private Handler handler;


    @Override
    protected void init() {
      //  EventBus.getDefault().register(this);
        showPassword = false;
        handler = new MyHandler();

    }

//    // 用EventBus 来导航,订阅者
//    public void onEventMainThread(NavFragmentEvent event) {
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initListener() {
        seePasswordImageView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        tv_forgerpw.setOnClickListener(this);
        tv_regist.setOnClickListener(this);
    }

    /**
     * 登录
     */
    private void goToMainTabsClick() {
        phone = login_phone_edittext.getText().toString().trim();
        password = login_password_edittext.getText().toString().trim();
        doLogin(phone, password);
    }

    /**
     * 处理登录的方法
     *
     * @param phone
     * @param password
     */
    private void doLogin(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToastInUIThread("输入手机号不能为空！");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.showToastInUIThread("输入密码不能为空！");
        } else {
            handler.sendEmptyMessage(MESSAGE_WHAT_LOGIN_LOGINING);//通知UI ，正在执行登录联网操作
            beforeTime = System.currentTimeMillis();
            Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "password"}, phone, password);
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SIGN_UP_COMMIT, params, null, REQUEST_CODE_LOGIN_COMMIT, this);
        }
    }

    /**
     * 跳转到重置密码页面
     */
    private void goToResetPassword() {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        EventBus.getDefault().post(new NavFragmentEvent(resetPasswordFragment));
        // this.isHidden();
    }

    /**
     * 跳转到注册页面
     */
    private void gotoRegist() {
        RegistFragment registFragment = new RegistFragment();
        EventBus.getDefault().post(new NavFragmentEvent(registFragment));
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录
            case R.id.tv_login:
                goToMainTabsClick();
                break;
            //注册
            case R.id.tv_regist:
                gotoRegist();
                break;
            //重置密码
            case R.id.tv_forgetpw:
                goToResetPassword();
                break;
            case R.id.login_iv_password_see_or_hidden:
                if (showPassword) {
                    login_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_normal);
                } else {
                    login_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_active);
                }
                showPassword = !showPassword;
                break;
        }

    }

    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(org.json.JSONObject jsonObject, int requestCode) {
        if (requestCode == REQUEST_CODE_LOGIN_COMMIT) {
            try {
                DecodeManager.decodeLogin(jsonObject, requestCode, handler);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = MESSAGE_WHAT_LOGIN_LOGINFAIL;
                Bundle data = new Bundle();
                data.putString("message", "服务器返回结果错误");
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }
        if (requestCode == REQUEST_CODE_UPLOAD_CONTACTS) {
            try {
                DecodeManager.decodeFriendMessage(jsonObject, requestCode, handler);
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtils.showToastInUIThread("网络连接错误！");
            }
        }
    }
    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        switch (requestCode) {
            case REQUEST_CODE_LOGIN_COMMIT:
                Message msg = new Message();
                msg.what = MESSAGE_WHAT_LOGIN_LOGINFAIL;
                Bundle data = new Bundle();
                data.putString("message", "与服务器连接失败");
                msg.setData(data);
                handler.sendMessage(msg);
                break;
            default:
                ToastUtils.showToastInUIThread("网络连接失败");
                break;
        }

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Bundle bundle = msg.getData();
            switch (what) {
                case MESSAGE_WHAT_LOGIN_LOGINING://正在进行登录操作
                    loginButton.setProgress(50);
                    loginButton.setClickable(false);
                    break;
                case MESSAGE_WHAT_LOGIN_LOGINFAIL://登录失败
                    loginButton.setProgress(0);
                    loginButton.setClickable(true);
                    ToastUtils.showToastInUIThread(msg.getData().getString("message"));
                    break;
                case REQUEST_CODE_LOGIN_COMMIT:
                    int identity = bundle.getInt("identity");
                    int binded = bundle.getInt("binded");
                    int code = bundle.getInt("code");
                    String mobile = bundle.getString("mobile");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setMobile(phone);
                    SessionHolder.initHolder(mobile, userInfo);
                    //  SPManager.getSPManager(AppUtils.getBaseContext()).persistenceSession();
                    if (code == 1) {
                        //  if (identity == 1) {//老人,进入主页面
                        login_password_edittext.setText("");
                        login_phone_edittext.setText("");
                        loginButton.setProgress(0);
                        tv_login.setClickable(true);
                        EventBus.getDefault().post(new NavFragmentEvent(new MainTabsFragemnt()));
                        finish();
                        //   }
//                        else if (identity == 0) {//子女
//                            if (binded == 1) {//已绑定老人
//                                EventBus.getDefault().post(new NavFragmentEvent(new MainTabsFragemnt()));
//                            } else if (binded == 0) {//没有绑定老人
//                                //1.获取手机联系人
//                                ArrayList<Contacts> contacts = (ArrayList<Contacts>) getContacts(AppUtils.getBaseContext());
//                                //2.上传通讯录,返回通讯录中,注册过 ,且是老人 的 用户列表（老人头像，老人手机号）
//                                Map<String, String> params = CommonTools.getParameterMap(new String[]{"contact_list"}, uploadContacts(contacts));
//                                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPLOAD_CONTACTS_LIST, params, null, REQUEST_CODE_UPLOAD_CONTACTS, LoginFragment.this);
//                                ToastUtils.showToastInUIThread("正在获取联系人列表....");
//                                //3.选择绑定的老人
//                            }
//                        }
                    } else {
                        Message msg1 = new Message();
                        msg1.what = MESSAGE_WHAT_LOGIN_LOGINFAIL;
                        Bundle data = new Bundle();
                        data.putString("message", msg.getData().getString("desc"));
                        msg1.setData(data);
                        handler.sendMessage(msg1);
                        Log.w("retinfo error", msg.getData().getString("desc"));
                    }
                    break;
                case REQUEST_CODE_UPLOAD_CONTACTS:
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        ArrayList<UserInfo> list_user = (ArrayList<UserInfo>) msg.getData().getSerializable("old_people_list");
                        //跳转到注册过此APP的联系人列表
                        Bundle data = new Bundle();
                        data.putSerializable("list_user", list_user);
                        EventBus.getDefault().post(new NavFragmentEvent(new OlsManListFragment(), bundle));
                    }
                    break;
            }
        }

    }

}