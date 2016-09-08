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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxkj.administrator.fealty.MainActivity;
import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.manager.DecodeManager;
import com.lxkj.administrator.fealty.manager.ParameterManager;
import com.lxkj.administrator.fealty.manager.SessionHolder;
import com.lxkj.administrator.fealty.ui.wheel.ScreenInfo;
import com.lxkj.administrator.fealty.ui.wheel.WheelMain;
import com.lxkj.administrator.fealty.utils.AppUtils;
import com.lxkj.administrator.fealty.utils.CommonTools;
import com.lxkj.administrator.fealty.utils.ImageUtil;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/8/4.
 */
@ContentView(R.layout.fragment_mineinfo)
public class MeSettingFragment extends BaseFragment implements View.OnClickListener, NetWorkAccessTools.RequestTaskListener {
    @ViewInject(R.id.fragment_mine_iv_head)
    private CircleImageView circleImageView;
    @ViewInject(R.id.fragment_mine_tv_nickname_smile)
    private TextView tv_simle;
    @ViewInject(R.id.fragment_mine_tv_nickname)
    private TextView tv_nickName;
    @ViewInject(R.id.fragment_mine_tv_phone)
    private TextView tv_phone;
    @ViewInject(R.id.fragment_mine_tv_birthday)
    private TextView birthDayTextView;
    @ViewInject(R.id.fragment_mine_iv_birthday)
    private ImageView tv_birth;
    @ViewInject(R.id.fragment_mine_iv_callCamera)
    private ImageView callCamera;
    @ViewInject(R.id.fragment_mine_iv_callAlbum)
    private ImageView callCame;
    @ViewInject(R.id.fragment_mine_iv_nickname)
    private ImageView iv_nickname;
    @ViewInject(R.id.fragment_mine_iv_sex)
    private ImageView iv_sex;
    @ViewInject(R.id.fragment_mine_tv_sex)
    private TextView tv_sex;
    @ViewInject(R.id.fragment_mine_iv_reset)
    private ImageView iv_reset;
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_iv_left;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_tv_title_left;
    private File tempImageFile;//相机拍摄图片缓存
    private File headImageFile;//剪切图片缓存
    private static final int REQUEST_CODE_PICK_IMAGE = 0x1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 0x2;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA_CROP = 0x3;
    public static final int REQUEST_CODE_SELF_DATA_ALTER = 0X310;
    public static final int REQUEST_USER_BYMIBILE = 0x01;
    private Handler handler = new MyHandler();
    @ViewInject(R.id.fragment_mine_tv_exit)
    private TextView fragment_mine_tv_exit;
    @ViewInject(R.id.fragment_mine_rl_about)
    private RelativeLayout see_user;

    @Override
    protected void init() {
        bar_iv_left.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_tv_title_left.setVisibility(View.VISIBLE);
        bar_tv_title_left.setText("我的资料设置");
        initPersonalDataShow();

    }

    @Override
    protected void initListener() {
        callCame.setOnClickListener(this);
        callCamera.setOnClickListener(this);
        iv_nickname.setOnClickListener(this);
        iv_sex.setOnClickListener(this);
        tv_birth.setOnClickListener(this);
        iv_reset.setOnClickListener(this);
        bar_iv_left.setOnClickListener(this);
        fragment_mine_tv_exit.setOnClickListener(this);
        see_user.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    /**
     * 初始化个人资料显示
     */
    private void initPersonalDataShow() {
        if (TextUtils.isEmpty(SessionHolder.user.getUserpic()) || "".equals(SessionHolder.user.getUserpic())) {
            circleImageView.setImageResource(R.mipmap.unknow_head);
        } else {
           NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://192.168.8.133:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
     // NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).toLoadImage("http://120.76.27.233:8080" + "/" + SessionHolder.user.getUserpic(), circleImageView, R.mipmap.unknow_head, R.mipmap.unknow_head);
        }
        tv_nickName.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置" : SessionHolder.user.getNickName());
        tv_phone.setText(SessionHolder.user.getMobile());
        tv_simle.setText(TextUtils.isEmpty(SessionHolder.user.getNickName()) ? "未设置 " : SessionHolder.user.getNickName());
        birthDayTextView.setText(TextUtils.isEmpty(SessionHolder.user.getBirthday()) ? "未设置" : SessionHolder.user.getBirthday());
        tv_sex.setText(TextUtils.isEmpty(SessionHolder.user.getGender()) ? "未设置" : TextUtils.equals(SessionHolder.user.getGender(), ParameterManager.USER_SEX_CODE_MAN) ? "男" : "女");
    }

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
                    circleImageView.setImageURI(Uri.fromFile(headImageFile));
                    // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "nickName", "headFile", "birthday", "sex"}, SessionHolder.user.getMobile(), SessionHolder.user.getNickName(), "", SessionHolder.user.getBirthday(), SessionHolder.user.getGender());
                    // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "nickName", "headFile", "birthday", "sex"}, SessionHolder.user.getMobile()," "," "," "," ");
                    if (headImageFile != null && headImageFile.exists()) {
                        Map<String, String> map = new HashMap<>();
                        byte[] buffer = changeFileToByte(headImageFile);
                        byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
                        String photo = new String(encode);
                        map.put("headFile", photo);
                        //  NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_USER_MSG, params, map, REQUEST_CODE_SELF_DATA_ALTER, MeSettingFragment.this);
                        //上传服务器，且写入sdcard
                        alterSelfData(map);//2016-8-26 xpp add
                    }
                    // alterSelfData(null);
                } catch (Exception e) {
                    Log.e("MeSettingFragment", "onActivityResult:" + e.getMessage());
                }
            } else {
                Log.v("MeSettingFragment", "onActivityResult:请求图片从剪切器没有返回");
            }
            if (tempImageFile != null && tempImageFile.exists()) {
                tempImageFile.delete();
            }
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
            Log.v("RegistActivity", "getImageFromCamera:不存在SD卡");
            ToastUtils.showToastInUIThread("请确认已经插入SD卡");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_mine_iv_callAlbum:
                getImageFromAlbum();
                break;
            case R.id.fragment_mine_iv_callCamera:
                getImageFromCamera();
                break;
            case R.id.fragment_mine_iv_nickname:
                showSetNickNameDialog();
                break;
            case R.id.fragment_mine_iv_sex:
                showSetSexDialog();
                break;
            case R.id.fragment_mine_iv_birthday:
                showSetBirthDayDialog();
                break;
            case R.id.fragment_mine_iv_reset:
                EventBus.getDefault().post(new NavFragmentEvent(new ResetPasswordFragment()));
                break;
            case R.id.bar_iv_left:
                EventBus.getDefault().post(getResources().getString(R.string.reflush));
                getActivity().onBackPressed();//返回
                break;
            case R.id.fragment_mine_tv_exit:
                getActivity().finish();
                break;
            case R.id.fragment_mine_rl_about:
                //查看绑定的用户信息
                EventBus.getDefault().post(new NavFragmentEvent(new UserDetailInfo()));
                break;
        }
    }

    @Override
    public boolean onBack() {
        EventBus.getDefault().post(new String("刷新界面"));
        return super.onBack();
    }

    private void showSetBirthDayDialog() {

        String oldBirthDay = birthDayTextView.getText().toString();
        final WheelMain wheelMain;
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(getActivity());
        wheelMain = new WheelMain(timepickerview);
        wheelMain.screenheight = screenInfo.getHeight();
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(oldBirthDay));
        } catch (ParseException e) {
            calendar.set(1990, 0, 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelMain.initDateTimePicker(year, month, day);
        new AlertDialog.Builder(getActivity())
                .setTitle("选择生日")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newBirthDay = wheelMain.getTime();
                        String[] split = newBirthDay.split("-");
                        if (split[1].length() == 1) {
                            split[1] = "0" + split[1];
                        }
                        if (split[2].length() == 1) {
                            split[2] = "0" + split[2];
                        }
                        newBirthDay = split[0] + "-" + split[1] + "-" + split[2];

                        if (!TextUtils.equals(newBirthDay, birthDayTextView.getText().toString())) {
                            birthDayTextView.setText(newBirthDay);
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("birthday", newBirthDay);
                            alterSelfData(parameters);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showSetSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择性别");
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_radiogroup_sex, null);
        final RadioButton womanRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_sex_woman);
        RadioButton manRadioButton = (RadioButton) inflate.findViewById(R.id.dialog_radiogroup_sex_man);
        womanRadioButton.setChecked(TextUtils.equals(tv_sex.getText(), "女生"));
        manRadioButton.setChecked(!TextUtils.equals(tv_sex.getText(), "女生"));
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String newGender = womanRadioButton.isChecked() ? "女生" : "男生";
                if (!TextUtils.equals(newGender, tv_sex.getText())) {
                    tv_sex.setText(newGender);
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("sex", TextUtils.equals(newGender, "女生") ? ParameterManager.USER_SEX_CODE_WOMAN : ParameterManager.USER_SEX_CODE_MAN);
                    alterSelfData(parameters);
                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
    }

    private void showSetNickNameDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("输入新的昵称");
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_textview_nickname, null);
        final EditText editText = (EditText) inflate.findViewById(R.id.dialog_textview_tv);
        editText.setText(tv_nickName.getText());
        editText.setSingleLine();
        builder.setView(inflate);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String newNickName = editText.getText().toString().trim();
                if (TextUtils.isEmpty(newNickName)) {
                    ToastUtils.showToastInUIThread("昵称输入不能为空");
                } else if (newNickName.length() > ParameterManager.nickNameLength) {
                    ToastUtils.showToastInUIThread("昵称长度必须小于" + ParameterManager.nickNameLength + "位");
                } else if (!TextUtils.equals(newNickName, tv_nickName.getText())) {
                    tv_nickName.setText(newNickName);
                    tv_simle.setText(newNickName);
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("nickName", newNickName);
                    alterSelfData(parameters);

                }
            }
        });
        builder.setNegativeButton("放弃", null);
        builder.setCancelable(false).create().show();
    }

    /**
     * 修改个人资料
     */
    private void alterSelfData(Map<String, String> parameters) {
        // Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "nickName", "headFile", "birthday", "sex"}, SessionHolder.user.getMobile(), SessionHolder.user.getNickName(), SessionHolder.user.getUserpic(), SessionHolder.user.getBirthday(), SessionHolder.user.getGender());
        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile", "nickName", "headFile", "birthday", "sex"}, SessionHolder.user.getMobile(), "", "", "", "");
        params.putAll(parameters);
        try {
//            if (headImageFile != null && headImageFile.exists()) {
//                Map<String, String> map = new HashMap<>();
//                byte[] buffer = changeFileToByte(headImageFile);
//                byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
//                String photo = new String(encode);
//                map.put("headFile", photo);
//                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_USER_MSG, params, map, REQUEST_CODE_SELF_DATA_ALTER, MeSettingFragment.this);
//            }
//            if (!TextUtils.isEmpty(SessionHolder.user.getUserpic())) {
//                Log.e("head", "服务器的头像不为空");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("headFile","http://192.168.8.133:8080"+"/"+SessionHolder.user.getUserpic());
//                NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_USER_MSG, params, map, REQUEST_CODE_SELF_DATA_ALTER, MeSettingFragment.this);
//            }
            NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.UPDATE_USER_MSG, params, null, REQUEST_CODE_SELF_DATA_ALTER, MeSettingFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToastInUIThread("程序错误");
        }
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
            case REQUEST_USER_BYMIBILE:
                try {
                    DecodeManager.decodeSelfDataQuery(jsonObject, REQUEST_USER_BYMIBILE, handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showToastInUIThread("服务器异常！");
                }
                break;
            default:
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

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_SELF_DATA_ALTER://个人信息修改
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        ToastUtils.showToastInUIThread("修改成功！");
                        // 请求个人资料
                        Map<String, String> params = CommonTools.getParameterMap(new String[]{"mobile"}, SessionHolder.user.getMobile());
                        // if (TextUtils.isEmpty(SessionHolder.user.getUserpic()))
                        NetWorkAccessTools.getInstance(AppUtils.getBaseContext()).postAsyn(ParameterManager.GET_USER_BYMOBILE, params, null, MeFragment.REQUEST_USER_BYMIBILE, MeSettingFragment.this);
                    } else {//请求失败
                        ToastUtils.showToastInUIThread(msg.getData().getString("desc"));
                        Log.w("service error", msg.getData().getString("desc"));
                    }
                    break;
                case REQUEST_USER_BYMIBILE://个人信息查询
                    if (msg.getData().getInt("code") == 1) {//请求成功
                        String mobile = msg.getData().getString("mobile");
                        String nickname = msg.getData().getString("nickName");
                        String pic = msg.getData().getString("headFile");
                        Log.e("pic", pic);
                        String sex = msg.getData().getString("sex");
                        String birthday = msg.getData().getString("birthday");
                        SessionHolder.user.setMobile(mobile);
                        SessionHolder.user.setNickName(nickname);
                        SessionHolder.user.setUserpic(pic);
                        SessionHolder.user.setGender(sex);
                        SessionHolder.user.setBirthday(birthday);
                        initPersonalDataShow();
                    } else {//请求失败
                        ToastUtils.showToastInUIThread(msg.getData().getString("desc"));
                        Log.w("service error", msg.getData().getString("desc"));
                    }
                    initPersonalDataShow();
                    break;
                default:
                    ToastUtils.showToastInUIThread("服务器错误！");
                    break;
            }
        }
    }
}
