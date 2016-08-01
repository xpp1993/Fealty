package com.lxkj.administrator.fealty.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTools {

	private static Toast mToast;

	private static final String[] CONTACTOR_NEED = new String[]{	//联系人字段
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.Contacts.DISPLAY_NAME
	};

	/**
	 * 使用系统当前日期，产生一个临时文件，用户缓存
	 * @param fileType	缓存文件的格式  如：jpg png   不要加上小数点
	 * @return
	 */
	public static File getTempFile(String fileType) {
		File file = null ;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
			SimpleDateFormat dateFormat = new SimpleDateFormat("'TEMP_FILE'_yyyy-MM-dd-HH-mm-ss");
			String tempFileName = dateFormat.format(new Date()) ;
			try {
				file = File.createTempFile(tempFileName,fileType,Environment.getExternalStorageDirectory());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 获取手机联系人的方法
	 * @param context
	 * @return
	 */
	public static List<Contact> getContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();

		Cursor phones = null;
		ContentResolver cr = context.getContentResolver();
		try {
			phones = cr
					.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
							, CONTACTOR_NEED, null, null, null);

			if (phones != null) {
				final int contactIdIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
				final int displayNameIndex = phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				final int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				String phoneString, displayNameString, contactIdString;
				while (phones.moveToNext()) {
					try {
						contactIdString = phones.getString(contactIdIndex);	//id

						phoneString = phones.getString(phoneIndex);	//号码
						if(TextUtils.isEmpty(phoneString)){
							continue;
						}
						phoneString = phoneString.replace(" ", "");
						if(phoneString.length()>11){
							phoneString = phoneString.substring(phoneString.length() - 11, phoneString.length());
						}

						displayNameString = phones.getString(displayNameIndex);	//名称
						if(TextUtils.isEmpty(displayNameString)){
							continue;
						}

						contacts.add(new Contact(contactIdString, displayNameString, phoneString));
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (phones != null)
				phones.close();
		}

		return contacts;

	}

	/**
	 * 手机联系人实体类
	 * @author Carl
	 *
	 */
	public static class Contact {

		private String id ;
		private String name ;
		private String phone ;

		public Contact(String id, String name, String phone) {
			super();
			this.id = id;
			this.name = name;
			this.phone = phone;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	

	/**
	 * 将指定的字符串用MD5加密
	 * @param originstr 需要加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String encodeByMD5(String originstr) {
		String result = null;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}; //用来将字节转换成 16 进制表示的字符
		if(originstr != null){
			MessageDigest md = null;
			byte[] source = new byte[0];
			//返回实现指定摘要算法的 MessageDigest 对象
			//使用utf-8编码将originstr字符串编码并保存到source字节数组
			try {
				md = MessageDigest.getInstance("MD5");
				source = originstr.getBytes("UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			//使用指定的 byte 数组更新摘要
			md.update(source);
			//通过执行诸如填充之类的最终操作完成哈希计算，结果是一个128位的长整数
			byte[] tmp = md.digest();
			//用16进制数表示需要32位
			char[] str = new char[32];
			for(int i=0,j=0; i < 16; i++){
				//j表示转换结果中对应的字符位置
				//从第一个字节开始，对 MD5 的每一个字节
				//转换成 16 进制字符
				byte b = tmp[i];
				//取字节中高 4 位的数字转换
				//无符号右移运算符>>> ，它总是在左边补0
				//0x代表它后面的是十六进制的数字. f转换成十进制就是15
				str[j++] = hexDigits[b>>>4 & 0xf];
				// 取字节中低 4 位的数字转换
				str[j++] = hexDigits[b&0xf];
			}
			result = new String(str);//结果转换成字符串用于返回
		}
		return result;
	}
	
	public static InputStream  Bitmap2IS(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());    
		return sbs;  
	}
	
	/**
	 * 构建参数map对象的工具方法
	 * @param keys
	 * @param values
	 * @return
	 */
	public static Map<String, String> getParameterMap(String keys[] ,String ... values){
		Map<String, String> map = new HashMap<String, String>() ;
		for(int i = 0 ; i<keys.length ;i++){
			map.put(keys[i], values[i]) ;
		}
		return map;
	}
    public static void toSendMessage(Context context ,String mobile ,String content) {
        /*直接将段信息发送出去
         SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> texts = smsManager.divideMessage(content);//拆分短信,短信字数太多了的时候要分几次发
        for(String text : texts){
         smsManager.sendTextMessage(mobile, null, text, null, null);//发送短信,mobile是对方手机号
        }*/

        //调用发短信的activity
        Uri uri = Uri.parse("smsto://"+mobile);  
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);  
        it.putExtra("sms_body", "");  
        context.startActivity(it); 
    }

    public static void toCall(Context context ,String mobile) {
        /*直接将号码拨出去
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:"+ mobile));//mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
        startActivity(intent);*/

        //调用拨号activity
        Uri uri = Uri.parse("tel:"+mobile);  
        Intent it = new Intent(Intent.ACTION_DIAL, uri);  
        context.startActivity(it);  
    }

//	public static void showToast(Context context ,String text) {
//		if (!TextUtils.isEmpty(text)) {
//			if (mToast == null) {
//				mToast = Toast.makeText(context, text,
//						Toast.LENGTH_SHORT);
//			} else {
//				mToast.setText(text);
//			}
//			mToast.show();
//		}
//	}

	public static boolean isApplicationBroughtToBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (tasks != null && !tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
