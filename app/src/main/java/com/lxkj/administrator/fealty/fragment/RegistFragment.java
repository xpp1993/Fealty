package com.lxkj.administrator.fealty.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.ui.MyRadioGroup;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.ExampleUtil;
import com.lxkj.administrator.fealty.utils.FormatCheck;
import com.lxkj.administrator.fealty.utils.ImageUtil;
import com.lxkj.administrator.fealty.utils.MReceiver;
import com.lxkj.administrator.fealty.utils.NetWorkAccessTools;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_regist)
public class RegistFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
    private static final int REQUEST_CODE_PICK_IMAGE = 0x1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 0x2;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA_CROP = 0x3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_REGIST_GET_CHECKCODE = 0X110;
    public static final int REQUEST_CODE_REGIST_COMMIT = 0X111;
    private boolean showPassword;
    @ViewInject(R.id.activity_regist_et_password)
    private EditText regist_password_edittext;
    @ViewInject(R.id.fragment_regist_iv_head)
    private CircleImageView headImageView;//头像展示容器
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_iv_left;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_tv_title_left;
    @ViewInject(R.id.regist_iv_password_see_or_hidden)
    private ImageView seePasswordImageView;
    private String nickName;
    private String phone;
    private String checkCode;
    private String password;
    @ViewInject(R.id.activity_regist_tv_getcheckcode)
    private TextView getCheckCodeTextView;
    @ViewInject(R.id.activity_regist_et_nickname)
    private EditText nickNameEditText;
    @ViewInject(R.id.activity_regist_et_password)
    private EditText passwordEditText;
    @ViewInject(R.id.activity_regist_et_phone)
    private EditText phoneEditText;
    @ViewInject(R.id.activity_regist_et_checkcode)
    private EditText checkCodeEditText;
    @ViewInject(R.id.activity_regist_bt_commit)
    private Button commitButton;
    private int remainRockTime;//倒计时剩余时长
    private Handler handler;
    @ViewInject(R.id.myradiogroup)
    private MyRadioGroup myRadioGroup;
    private RadioButton radioButton;
    String radiobuttonString;
    private String indentity;
    private File tempImageFile;//相机拍摄图片缓存
    private File headImageFile;//剪切图片缓存
    private String check_id;//获取验证码id
    String registerID;
    private static final String TAG = "JPush";

    protected void init() {
        //   EventBus.getDefault().register(this);
        bar_iv_left.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_tv_title_left.setVisibility(View.VISIBLE);
        bar_tv_title_left.setText("账号注册");
        remainRockTime = ParameterManager.TOTAL_ROCK_TIME;
        handler = new Myhandler();
//        //极光推送
//        //  registerID = JPushInterface.getRegistrationID(AppUtils.getBaseContext());
//        String phone = phoneEditText.getText().toString().trim();
//        //  handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, registerID));
//        handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, phone));
//        Log.e("registerID", registerID);
    }

    private static final int MSG_SET_ALIAS = 1001;
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(AppUtils.getBaseContext().getApplicationContext())) {
                        handler.sendMessageDelayed(handler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            ExampleUtil.showToast(logs, AppUtils.getBaseContext().getApplicationContext());
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        // EventBus.getDefault().unregister(this);
        handler.removeCallbacks(runable);
    }

    @Override
    protected void initListener() {
        headImageView.setOnClickListener(this);
        bar_iv_left.setOnClickListener(this);
        seePasswordImageView.setOnClickListener(this);
        getCheckCodeTextView.setOnClickListener(this);
        commitButton.setOnClickListener(this);
        myRadioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                radioButton = (RadioButton) getActivity().findViewById(checkedId);
                radiobuttonString = radioButton.getText().toString();
            }
        });

    }

    /**
     * 设置头像
     */
    private void changeHeadIcon() {
        final CharSequence[] items = {"相册", "拍照"};
        AlertDialog dlg = new AlertDialog.Builder(getActivity())
                .setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // 这里item是根据选择的方式，
                        if (item == 0) {
                            getImageFromAlbum();
                        } else {
                            getImageFromCamera();
                        }
                    }
                }).create();
        dlg.show();
    }

    /**
     * 开启验证码计时器
     */
    private void startRock() {
        final Handler handler = new Handler();
        handler.post(runable);
    }

    private Runnable runable = new Runnable() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent photoZoomIntent = ImageUtil.getPhotoZoomIntent(data.getData());
                headImageFile = CommonTools.getTempFile(".jpg");
                photoZoomIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(headImageFile));//将截取器截取的图片缓存人指定的路径
                startActivityForResult(photoZoomIntent, REQUEST_CODE_CAPTURE_CAMEIA_CROP);
            } else {
                Log.v("RegistActivity", "onActivityResult:请求图片从相册没有返回");
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            if (resultCode == Activity.RESULT_OK) {
                Intent photoZoomIntent = ImageUtil.getPhotoZoomIntent(Uri.fromFile(tempImageFile));
                headImageFile = CommonTools.getTempFile(".jpg");
                photoZoomIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(headImageFile));//将截取器截取的图片缓存人指定的路径
                startActivityForResult(photoZoomIntent, REQUEST_CODE_CAPTURE_CAMEIA_CROP);
            } else {
                Log.v("RegistActivity", "onActivityResult:请求图片从相机没有返回");
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Log.v("RegistActivity", "onActivityResult:请求图片从从剪切器返回成功");
                try {
                    headImageView.setImageURI(Uri.fromFile(headImageFile));
                } catch (Exception e) {
                    Log.e("RegistActivity", "onActivityResult:" + e.getMessage());
                }
            } else {
                Log.v("RegistActivity", "onActivityResult:请求图片从剪切器没有返回");
            }
            if (tempImageFile != null && tempImageFile.exists()) {
                tempImageFile.delete();
            }
        }
    }

    /**
     * 从相册获取图片
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 从相机获取图片
     */
    private void getImageFromCamera() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImageFile = CommonTools.getTempFile(".jpg");
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImageFile));//将相机拍摄的照片缓存人指定的路径
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Log.v(getClass().getName(), "getImageFromCamera:不存在SD卡");
            Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean finish() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_regist_iv_head:
                changeHeadIcon();
                break;
            case R.id.bar_iv_left:
               // getActivity().onBackPressed();//返回
                EventBus.getDefault().post(new NavFragmentEvent(new LoginFragment()));//跳转到登录页面
                break;
            case R.id.regist_iv_password_see_or_hidden:
                if (showPassword) {
                    regist_password_edittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_normal);
                } else {
                    regist_password_edittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    seePasswordImageView.setImageResource(R.mipmap.icon_login_show_password_active);
                }
                showPassword = !showPassword;
                break;
            case R.id.activity_regist_tv_getcheckcode:
                phone = phoneEditText.getText().toString().trim();
                if (FormatCheck.isMobile(phone)) {
                    Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, phone);
                    try {
                        getCheckCodeTextView.setClickable(false);
                        getCheckCodeTextView.setText("获取中...");
                        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_CHECK_CODE, params, null, REQUEST_CODE_REGIST_GET_CHECKCODE, this);
                        System.out.print(ParameterManager.GET_CHECK_CODE + phone);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToastInUIThread("服务器错误!");
                    }
                } else {
                    ToastUtils.showToastInUIThread("手机号码格式错误,重写填写!");
                }
                break;
            case R.id.activity_regist_bt_commit:
                nickName = nickNameEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();
                checkCode = checkCodeEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(nickName)) {
                    ToastUtils.showToastInUIThread("昵称不能为空");
                    nickNameEditText.requestFocus();
                } else if (nickName.length() > ParameterManager.nickNameLength) {
                    ToastUtils.showToastInUIThread("昵称长度必须小于" + ParameterManager.nickNameLength + "位");
                    nickNameEditText.requestFocus();
                } else if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToastInUIThread("手机号不能为空");
                    phoneEditText.requestFocus();
                } else if (TextUtils.isEmpty(checkCode)) {
                    ToastUtils.showToastInUIThread("验证码不能为空");
                    checkCodeEditText.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showToastInUIThread("密码不能为空");
                    passwordEditText.requestFocus();
                } else {
                    try {
//                        if ("老人".equals(radiobuttonString)) {
//                            indentity = "1";
//                        } else if ("子女".equals(radiobuttonString)) {
//                            indentity = "0";
//                        }
                        //极光推送
                       handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, phone));
                        Log.e("registerID",phone);
                        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "password", "check_code", "nickName", "identity", "headFile", "id", "registerID"}, phone, password, checkCode, nickName, indentity, "", check_id, phone);
                        if (headImageFile != null && headImageFile.exists()) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            byte[] buffer = changeFileToByte(headImageFile);
                            byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
                            String photo = new String(encode);
                            map.put("headFile", photo);
                            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SIGN_IN_SUBMIT, params, map, REQUEST_CODE_REGIST_COMMIT, this);
                        } else {

                            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.SIGN_IN_SUBMIT, params, null, REQUEST_CODE_REGIST_COMMIT, this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToastInUIThread("服务器错误!");
                    }
                }
                break;
        }
    }

    /**
     * 将file转为数组
     *
     * @param file
     * @return
     */
    private byte[] changeFileToByte(File file) {
        byte[] buffer = null;
        try {

            if (file == null || !file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
                bos.flush();
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
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
            case REQUEST_CODE_REGIST_GET_CHECKCODE:
                try {
                    DecodeManager.decodeCheckCode(jsonObject, requestCode, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器返回结果错误");
                }
                break;
            case REQUEST_CODE_REGIST_COMMIT:
                try {
                    DecodeManager.decodeRegistConfirm(jsonObject, requestCode, handler);
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
            case REQUEST_CODE_REGIST_GET_CHECKCODE:
                ToastUtils.showToastInUIThread("网络连接错误,请检查重试");
                getCheckCodeTextView.setClickable(true);
                getCheckCodeTextView.setText("获取验证码");
                remainRockTime = ParameterManager.TOTAL_ROCK_TIME;
                break;
            case REQUEST_CODE_REGIST_COMMIT:
                ToastUtils.showToastInUIThread("网络连接错误,请检查重试");
//                if (headImageFile != null || headImageFile.exists()) {
//                    headImageFile.delete();
//                }
                break;
        }

    }

    class Myhandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_REGIST_GET_CHECKCODE:
                    if (msg.getData().getInt("code") == 1) {//如果请求成功
                        startRock();//开启倒计时
                        String check_code = msg.getData().getString("check_code");
                        check_id = msg.getData().getString("id");
                        if (TextUtils.isEmpty(check_code)) {
                            checkCodeEditText.requestFocus();
                        } else {
                            checkCodeEditText.setText(check_code);
                            getCheckCodeTextView.setText("获取验证码");
                            commitButton.setEnabled(true);
                            passwordEditText.requestFocus();
                        }
                    } else {
                        commitButton.setEnabled(false);
                        getCheckCodeTextView.setClickable(true);
                        getCheckCodeTextView.setText("获取验证码");
                        phoneEditText.requestFocus();
                    }
                    break;
                case REQUEST_CODE_REGIST_COMMIT:
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        System.out.print("注册成功！！！！！！！！！！！！！！");
                        EventBus.getDefault().post(new NavFragmentEvent(new LoginFragment()));//跳转到登录页面
                        ToastUtils.showToastInUIThread("请登录!");
                    } else if (msg.getData().getInt("code") == 2) {
                        System.out.print("注册失败！");
                    }

                    break;
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(AppUtils.getBaseContext().getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
            }
        }
    }
}
