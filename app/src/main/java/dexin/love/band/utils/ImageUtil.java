package dexin.love.band.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {
	/**
	 * 调度剪裁intent
	 * @param dataUri 选中图片的uri路径
	 * @return 返回意图
	 */
	public static Intent getPhotoZoomIntent(Uri dataUri) {
		Intent intent = new Intent();
		//系统裁剪活动
		intent.setAction("com.android.camera.action.CROP");
		//设置裁剪的源图片和类型
		intent.setDataAndType(dataUri, "image/*");
		//打开裁剪
		intent.putExtra("crop", "true");
		// 裁剪框比例，宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 输出图片大小，剪裁图片的宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		//返回结果,但是大图片很有可能直接内存溢出
//		intent.putExtra("return-data", true);
		//黑边【可以缩放】
		intent.putExtra("scale", true);
		//黑边【可以缩放】
		intent.putExtra("scaleUpIfNeeded", true);
		return intent;
	}
	public static File scal(Uri fileUri){
		String path = fileUri.getPath();
		File outputFile = new File(path);
		long fileSize = outputFile.length();
		final long fileMaxSize = 100 * 1024;
		if (fileSize >= fileMaxSize) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int height = options.outHeight;
			int width = options.outWidth;

			double scale = Math.sqrt((float) fileSize / fileMaxSize);
			options.outHeight = (int) (height / scale);
			options.outWidth = (int) (width / scale);
			options.inSampleSize = (int) (scale + 0.5);
			options.inJustDecodeBounds = false;

			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			outputFile = CommonTools.getTempFile(".jpg") ;
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outputFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}else{
				File tempFile = outputFile;
				outputFile = CommonTools.getTempFile(".jpg") ;
				copyFileUsingFileChannels(tempFile, outputFile);
			}

		}
		return outputFile;

	}
	public static Uri createImageFile(){
		// Create an image file name
		String imageFileName = new SimpleDateFormat("'TEMP_FILE'_yyyy-MM-dd-HH-mm-ss").format(new Date());
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = null;
		try {
			image = File.createTempFile(imageFileName,".jpg", storageDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Save a file: path for use with ACTION_VIEW intents
		return Uri.fromFile(image);
	}
	public static void copyFileUsingFileChannels(File source, File dest){
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			try {
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
