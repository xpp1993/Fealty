package com.lxkj.administrator.fealty.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;
import com.lxkj.administrator.fealty.event.NavFragmentEvent;
import com.lxkj.administrator.fealty.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_regist)
public class RegistFragment extends BaseFragment implements View.OnClickListener {
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private boolean showPassword;
    @ViewInject(R.id.activity_regist_et_password)
    private EditText regist_password_edittext;
    private File tempFile;
    // private CircleImageView headIcon;
    @ViewInject(R.id.fragment_regist_iv_head)
    private CircleImageView headImageView;//头像展示容器
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    public static final int CROP_PHOTO = 2;
    @ViewInject(R.id.bar_iv_left)
    private ImageView bar_iv_left;
    @ViewInject(R.id.bar_view_left_line)
    private ImageView bar_view_left_line;
    @ViewInject(R.id.bar_tv_title_left)
    private TextView bar_tv_title_left;
    @ViewInject(R.id.regist_iv_password_see_or_hidden)
    private ImageView seePasswordImageView;

    protected void init() {
        EventBus.getDefault().register(this);
        bar_iv_left.setVisibility(View.VISIBLE);
        bar_view_left_line.setVisibility(View.VISIBLE);
        bar_tv_title_left.setVisibility(View.VISIBLE);
        bar_tv_title_left.setText("账号注册");
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
//        headImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeHeadIcon();
//            }
//        });
        headImageView.setOnClickListener(this);
        bar_iv_left.setOnClickListener(this);
        seePasswordImageView.setOnClickListener(this);

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
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,
                                    PHOTO_REQUEST_GALLERY);
                        } else {
                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Environment.getExternalStorageState().equals(
                                    Environment.MEDIA_MOUNTED)) {
                                tempFile = new File(Environment
                                        .getExternalStorageDirectory(),
                                        PHOTO_FILE_NAME);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(intent,
                                        PHOTO_REQUEST_CAREMA);
                            } else {
                                ToastUtils.showToastInUIThread("未找到存储卡，无法存储照片！");
                            }
                        }
                    }
                }).create();
        dlg.show();
    }

    @Override
    public void onGetBunndle(Bundle arguments) {
        super.onGetBunndle(arguments);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                Log.e("图片路径？？", uri.toString());
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                crop(Uri.fromFile(tempFile));
            } else {
                ToastUtils.showToastInUIThread("未找到存储卡，无法存储照片！");
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                final Bitmap bitmap = data.getParcelableExtra("data");

                headImageView.setImageBitmap(bitmap);
                // 保存图片到internal storage
                FileOutputStream outputStream;
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    // out.close();
                    // final byte[] buffer = out.toByteArray();
                    // outputStream.write(buffer);
                    outputStream = getActivity().openFileOutput("head_icon.jpg",
                            Context.MODE_PRIVATE);
                    out.writeTo(outputStream);
                    out.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                if (tempFile != null && tempFile.exists())
                    tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspect", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
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
                getActivity().onBackPressed();//返回
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
        }
    }
}
