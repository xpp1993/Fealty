package dexin.love.band.fragment;

import android.content.SharedPreferences;
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
import org.json.JSONException;
import org.xutils.view.annotation.ContentView;

import org.xutils.view.annotation.ViewInject;
import java.util.Map;

import de.greenrobot.event.EventBus;
import dexin.love.band.R;
import dexin.love.band.base.BaseFragment;
import dexin.love.band.bean.UserInfo;
import dexin.love.band.event.NavFragmentEvent;
import dexin.love.band.manager.DecodeManager;
import dexin.love.band.manager.ParameterManager;
import dexin.love.band.manager.SPManager;
import dexin.love.band.manager.SessionHolder;
import dexin.love.band.ui.ActionProcessButton;
import dexin.love.band.utils.AppUtils;
import dexin.love.band.utils.CommonTools;
import dexin.love.band.utils.ContextUtils;
import dexin.love.band.utils.NetWorkAccessTools;
import dexin.love.band.utils.ToastUtils;

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
    public static final int MESSAGE_WHAT_LOGIN_LOGINING = 1;
    public static final int MESSAGE_WHAT_LOGIN_LOGINFAIL = 3;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Handler handler;


    @Override
    protected void init() {
        showPassword = false;
        handler = new MyHandler();
        //1.获得sharedPreference对象,SharedPrefences只能放基础数据类型，不能放自定义数据类型。
        preferences = SPManager.getSharedPreferences(AppUtils.getBaseContext());
        //2. 获得编辑器:当将数据存储到SharedPrefences对象中时，需要获得编辑器。如果取出则不需要。
        editor = preferences.edit();
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
        Bundle bundle = new Bundle();
        bundle.putInt("status", 1);
        EventBus.getDefault().post(new NavFragmentEvent(resetPasswordFragment, bundle));
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
        try {
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
        } catch (Throwable e) {
            e.printStackTrace();
            ToastUtils.showToastInUIThread("服务器返回错误");
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
                    int code = bundle.getInt("code");
                    String mobile = bundle.getString("mobile");
                    String password = bundle.getString("password");
                    String nickName = bundle.getString("nickName");
                    String headFile = bundle.getString("headFile");
                    String birthday = bundle.getString("birthday");
                    int sex = bundle.getInt("sex");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setMobile(phone);
                    userInfo.setGender(sex + "");
                    userInfo.setPassword(password);
                    userInfo.setNickName(nickName);
                    userInfo.setBirthday(birthday);
                    userInfo.setUserpic(headFile);
                    if (code == 1) {
//                        SessionHolder.initHolder(mobile, userInfo);
                        login_password_edittext.setText("");
                        login_phone_edittext.setText("");
                        loginButton.setProgress(0);
                        tv_login.setClickable(true);
                        Log.i("mobile", mobile);
                        ContextUtils.saveObj2SP(AppUtils.getBaseContext(), userInfo, "userInfo");
                        UserInfo user = ContextUtils.getObjFromSp(AppUtils.getBaseContext(), "userInfo");
                        SessionHolder.initHolder(mobile, user);
                        // editor.putString(ParameterManager.USERINFO, mobile);
                        EventBus.getDefault().post(new NavFragmentEvent(new MainTabsFragemnt()));

                        // finish();//2016.9.14.xpp add
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
            }
        }
    }

    @Override
    public boolean onBack() {
        return false;
    }

    //防止登录页面重叠
    @Override
    public boolean finish() {
        return true;
    }
}