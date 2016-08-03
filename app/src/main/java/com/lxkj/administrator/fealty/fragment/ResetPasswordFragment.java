package com.lxkj.administrator.fealty.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.FormatCheck;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_reset_password)
public class ResetPasswordFragment extends BaseFragment implements NetWorkAccessTools.RequestTaskListener, View.OnClickListener {
    public static final int REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE = 0X220;
    public static final int REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM = 0X221;
    @ViewInject(R.id.bar_iv_left)
    private ImageView barBackImageView;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView barLeftTitleTextView;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.activity_reset_tv_phone)
    private EditText phoneEditText;
    @ViewInject(R.id.activity_reset_tv_getcheckcode)
    private TextView getCheckCodeTextView;
    @ViewInject(R.id.activity_reset_et_checkcode)
    private EditText checkCodeEditText;
    @ViewInject(R.id.activity_reset_et_password)
    private EditText newPasswordEditText;
    @ViewInject(R.id.activity_reset_et_password_again)
    private EditText newPasswordConfirmEditText;
    @ViewInject(R.id.bar_iv_right)
    private ImageView bar_iv_right;
    @ViewInject(R.id.bar_tv_title_center)
    private TextView bar_tv_title_center;
    @ViewInject(R.id.activity_reset_bt_commit)
    private Button commitButton;
    private Handler handler;
    private String check_id;//获取验证码id
    private int remainRockTime;//倒计时剩余时长
    private String checkCode;
    private String phone;

    protected void init() {
      //  EventBus.getDefault().register(this);
        handler = new MyHandler();
        remainRockTime = ParameterManager.TOTAL_ROCK_TIME;
        barBackImageView.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        barLeftTitleTextView.setVisibility(View.VISIBLE);
        barLeftTitleTextView.setText("忘记密码");

    }

    // 用EventBus 来导航,订阅者
//    public void onEventMainThread(NavFragmentEvent event) {
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initListener() {
        barBackImageView.setOnClickListener(this);
        getCheckCodeTextView.setOnClickListener(this);
        bar_iv_right.setVisibility(View.GONE);
        bar_tv_title_center.setVisibility(View.GONE);
        commitButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    /**
     * 开启验证码计时器
     */
    private void startRock() {
        final Handler handler = new Handler();
        Runnable runable = new Runnable() {
            @Override
            public void run() {
                remainRockTime--;
                if (remainRockTime > 0) {
                    getCheckCodeTextView.setClickable(false);
                    getCheckCodeTextView.setText(remainRockTime + "秒后重新获取");
                    handler.postDelayed(this, 1000);
                } else {
                    getCheckCodeTextView.setClickable(true);
                    getCheckCodeTextView.setText("获取验证码");
                    remainRockTime = ParameterManager.TOTAL_ROCK_TIME;
                }
            }
        };
        handler.post(runable);
    }

    @Override
    public boolean finish() {
        return true;
    }

    @Override
    public void onRequestStart(int requestCode) {

    }

    @Override
    public void onRequestLoading(int requestCode, long current, long count) {

    }

    @Override
    public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE:
                try {
                    DecodeManager.decodeCheckCode(jsonObject, requestCode, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器返回结果错误");
                }
                break;
            case REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM:
                try {
                    DecodeManager.decodeCommon(jsonObject, requestCode, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器返回结果错误");
                }
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode, int errorNo) {
        switch (requestCode) {
            case REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE:
                ToastUtils.showToastInUIThread("网络连接错误,请检查重试");
                getCheckCodeTextView.setClickable(true);
                getCheckCodeTextView.setText("获取验证码");
                remainRockTime = ParameterManager.TOTAL_ROCK_TIME;
                break;
            case REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM:
                ToastUtils.showToastInUIThread("网络连接错误,请检查重试");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_iv_left:
                getActivity().onBackPressed();//返回
                break;
            case R.id.activity_reset_tv_getcheckcode:
                phone = phoneEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToastInUIThread("手机号码不能为空");
                } else if (FormatCheck.isMobile(phoneEditText.getText().toString().trim())) {
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, phone);
                    try {
                        getCheckCodeTextView.setClickable(false);
                        getCheckCodeTextView.setText("获取中...");
                        //  RequireServiceManager.getRequireServiceManager().requireCheckCode(this, CommonTools.encodeByMD5(phoneEditText.getText().toString().trim()), "1", REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE, this);
                        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_CHECK_CODE, params, null, REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE, this);
                        System.out.print(ParameterManager.GET_CHECK_CODE + phone);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToastInUIThread("程序错误");
                    }
                } else {
                    ToastUtils.showToastInUIThread("手机号码格式错误");
                }

                break;
            case R.id.activity_reset_bt_commit:
                checkCode = checkCodeEditText.getText().toString().trim();
                String newPassword1 = newPasswordEditText.getText().toString().trim();
                String newPassword2 = newPasswordConfirmEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phoneEditText.getText().toString().trim())) {
                    ToastUtils.showToastInUIThread("手机号码不能为空");
                    phoneEditText.requestFocus();
                } else if (!FormatCheck.isMobile(phoneEditText.getText().toString().trim())) {
                    ToastUtils.showToastInUIThread("手机号码格式错误");
                } else if (TextUtils.isEmpty(checkCode)) {
                    ToastUtils.showToastInUIThread("验证码不能为空");
                    checkCodeEditText.requestFocus();
                } else if (TextUtils.isEmpty(newPassword1)) {
                    ToastUtils.showToastInUIThread("密码不能为空");
                    newPasswordEditText.requestFocus();
                } else if (TextUtils.isEmpty(newPassword2)) {
                    ToastUtils.showToastInUIThread("确认密码不能为空");
                    newPasswordConfirmEditText.requestFocus();
                } else if (!TextUtils.equals(newPassword1, newPassword2)) {
                    ToastUtils.showToastInUIThread("两次密码输入不一致");
                    newPasswordEditText.requestFocus();
                } else {
                    try {
                        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "new_password", "check_code","id"}, phone, newPassword1, checkCode,check_id);
                        // RequireServiceManager.getRequireServiceManager().requireToResetPassword(this, CommonTools.encodeByMD5(phoneEditText.getText().toString().trim()), checkCode, send_id, newPassword1, REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM, this);
                        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.RESET_PASSWORD, params, null, REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToastInUIThread("程序错误");
                    }
                }
                break;
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_SELF_GET_RESET_PASSWORD_REQUIRE_CHECKCODE:
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        startRock();//开启倒计时
                        String check_code = msg.getData().getString("check_code");
                        check_id=msg.getData().getString("id");
                        if (TextUtils.isEmpty(check_code)) {
                            checkCodeEditText.requestFocus();
                        } else {
                            checkCodeEditText.setText(check_id);
                            getCheckCodeTextView.setText("获取验证码");
                            commitButton.setEnabled(true);
                            newPasswordEditText.requestFocus();
                        }

                    } else {
                        commitButton.setEnabled(false);
                        getCheckCodeTextView.setClickable(true);
                        getCheckCodeTextView.setText("获取验证码");
                    }
                    break;
                case REQUEST_CODE_SELF_GET_RESET_PASSWORD_CONFIRM://提交修改密码返回
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        ToastUtils.showToastInUIThread("密码修改成功,请重新登陆!");
                        EventBus.getDefault().post(new NavFragmentEvent(new LoginFragment()));//跳转到登录页面
                        // finish();
                    } else {//请求失败
                        ToastUtils.showToastInUIThread("请求失败！"+msg.getData().getString("desc"));
                    }
                    break;
            }
        }
    }
}
