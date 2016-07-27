package com.lxkj.administrator.fealty.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.lxkj.administrator.fealty.R;
import com.lxkj.administrator.fealty.base.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 2016/7/26.
 */
@ContentView(R.layout.fragment_regist)
public class RegistFragment extends BaseFragment {
    @ViewInject(R.id.fragment_regist_iv_callCamera)
    private ImageView takePhone;//调动摄像头拍照
    @ViewInject(R.id.fragment_regist_iv_callAlbum)
    private ImageView callAlbum;//调动相册的图片
    @ViewInject(R.id.fragment_regist_iv_head)
    private ImageView headImageView;//头像展示容器
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    public static final int CROP_PHOTO = 2;

    @Override
    protected void init() {
    }

    @Override
    protected void initListener() {
            takePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //创建File对象，用于存储拍照后的图片
                    File outputImage=new File(Environment.getExternalStorageDirectory(),"tempImage.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                       outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageUri=Uri.fromFile(outputImage);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if (requestCode== Activity.RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver()
                                .openInputStream(imageUri));
                        headImageView.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {

    }
}
