package com.lxkj.administrator.fealty.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lxkj.administrator.fealty.bean.RateListData;
import com.lxkj.administrator.fealty.bean.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DecodeManager {
    /**
     * 解析 获取验证码
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeCheckCode(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeCheckCode", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);

        if (isRequestOK(jsonObject)) {//请求服务器成功
//            int code = jsonObject.optInt("code");
//            String desc = jsonObject.optString("message");
//            String check_code = jsonObject.optString("check_code");
            String seesion = jsonObject.optString("data");
            String id = seesion.substring(0, seesion.indexOf("@"));
//            data.putString("check_code", check_code);
//            data.putInt("code", code);
//            data.putString("desc", desc);
            data.putString("id", id);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解码 注册确认
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
    public static void decodeRegistConfirm(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeRegistConfirm", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        //  insertRecInformation(data, jsonObject);

        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code");
            String desc = jsonObject.optString("desc");
            data.putInt("code", code);
            data.putString("desc", desc);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 登陆信息
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
//    public static void decodeLogin(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
//        Log.v("decodeLogin", jsonObject.toString());
//        Message msg = new Message();
//        Bundle data = new Bundle();
//        msg.what = messageWhat;
//        insertRecInformation(data, jsonObject);
//        if (isRequestOK(jsonObject)) {
//            int identity = jsonObject.optInt("identity", 0);
//            int binded = jsonObject.optInt("binded", 0);
//            String mobile = jsonObject.optString("mobile");
//            String password = jsonObject.optString("password");
//            String nickName = jsonObject.optString("nickName");
//            String headFile = jsonObject.optString("headFile");
//            data.putInt("identity", identity);
//            data.putInt("binded", binded);
//            data.putString("mobile", mobile);
//            data.putString("password", password);
//            data.putString("nickName", nickName);
//            data.putString("headFile", headFile);
//        }
//        msg.setData(data);
//        handler.sendMessage(msg);
//    }
    public static void decodeLogin(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeLogin", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code");
            String desc = jsonObject.optString("desc");
            data.putInt("code", code);
            data.putString("desc", desc);
            JSONObject object = jsonObject.optJSONObject("json").optJSONObject("user");
            int sex = object.optInt("sex", 0);
            int binded = object.optInt("binded", 0);
            String mobile = object.optString("mobile");
            String password = object.optString("password");
            String nickName = object.optString("nickName");
            String headFile = object.optString("headFile");
            String birthday = object.optString("birthday");
            data.putInt("sex", sex);
            data.putInt("binded", binded);
            data.putString("mobile", mobile);
            data.putString("password", password);
            data.putString("nickName", nickName);
            data.putString("headFile", headFile);
            data.putString("birthday", birthday);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 GPS信息
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
    public static void decodeGPSMessage(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeIssueMessage", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String[]> result = new HashMap<>();
            JSONArray jsonArray = jsonObject.getJSONObject("json").getJSONArray("gpsMsg_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String lat = object.optString("lat");
                String lon = object.optString("lon");
                String locationdescrible = object.optString("locationdescrible");
                String address = object.optString("address");
                String parentPhone = object.optString("parentPhone");
                result.put(parentPhone, new String[]{lat, lon, locationdescrible, address});
            }
            data.putSerializable("result", result);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析折线心率信息
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
    public static void decodeHeartMessage(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeIssueMessage", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, List<RateListData>> result = new HashMap<>();
            JSONArray array = jsonObject.optJSONObject("json").optJSONArray("heartMsg");
            for (int i = 0; i < array.length(); i++) {
                List<RateListData> list = new ArrayList<>();
                JSONObject object1 = array.getJSONObject(i);
                String heartRateString = object1.optString("heartRate");
                if (TextUtils.isEmpty(heartRateString))
                    continue;
                com.alibaba.fastjson.JSONObject ob1 = (com.alibaba.fastjson.JSONObject) JSON.parse(heartRateString);
                String mobile = ob1.getString("mobile");
                com.alibaba.fastjson.JSONArray jsonArray = ob1.getJSONArray("heartRate");
                for (int j = 0; j < jsonArray.size(); j++) {
                    com.alibaba.fastjson.JSONObject object = jsonArray.getJSONObject(j);
                    //    com.alibaba.fastjson.JSONObject object = (com.alibaba.fastjson.JSONObject) jsonArray.get(i);
                    String rate_time = object.getString("rate_time");
                    int rate = object.getInteger("rate");
                    RateListData rateListData = new RateListData(rate, rate_time);
                    list.add(rateListData);
                    Log.e("rateListData", rateListData.toString());
                }
                result.put(mobile, list);
            }
            data.putSerializable("result", result);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 通用jsonObject
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeCommon(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeCommon", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        // insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code");
            String desc = jsonObject.optString("desc");
            data.putInt("code", code);
            data.putString("desc", desc);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 个人资料查询
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
    public static void decodeSelfDataQuery(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeSelfDataQuery", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        //  insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code");
            String desc = jsonObject.optString("desc");
            data.putInt("code", code);
            data.putString("desc", desc);
            JSONObject jsonObject1 = jsonObject.getJSONObject("json").getJSONObject("user");
            String mobile = jsonObject1.getString("mobile");
            String nickName = jsonObject1.getString("nickName");
            String headFile = jsonObject1.optString("headFile", "");
            String sex = String.valueOf(jsonObject1.optInt("sex", 0) == 0 ? "" : jsonObject1.getInt("sex"));
            //  String sex = String.valueOf(jsonObject1.optInt("sex", 0));
            String birthday = jsonObject1.optString("birthday", "");
            data.putString("mobile", mobile);
            data.putString("nickName", nickName);
            data.putString("sex", sex);
            data.putString("birthday", birthday);
            data.putString("headFile", headFile);
            Log.e("healFile", headFile);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 用户信息查询 ,查询别人的
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeUserInfoQuery(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeUserInfoQuery", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code", 1);
            String desc = jsonObject.optString("desc", "");
            data.putInt("code", code);
            data.putString("desc", desc);
            JSONArray userMsg_list = jsonObject.optJSONObject("json").optJSONArray("userMsg_list");
            if (userMsg_list != null || userMsg_list.length() > 0) {
                ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
                for (int i = 0; i < userMsg_list.length(); i++) {
                    JSONObject friendJsonObject = userMsg_list.getJSONObject(i);
                    String parentPhone = friendJsonObject.optString("parentPhone");
                    int currentHeart = friendJsonObject.optInt("currentHeart");
                    String identity = friendJsonObject.optString("identity");
                    String cuffElectricity = friendJsonObject.optString("cuffElectricity");
                    String mobileElectricity = friendJsonObject.optString("mobileElectricity");
                    String address = friendJsonObject.optString("address");
                    UserInfo userInfo = new UserInfo(identity, parentPhone, currentHeart, address, cuffElectricity, mobileElectricity);
                    friends.add(userInfo);
                }
                data.putSerializable("userMsg_list", friends);
            }
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 修改用户信息
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeChangeInfo(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeChangeInfo", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code", 1);
            String desc = jsonObject.optString("desc", "");
            data.putInt("code", code);
            data.putString("desc", desc);
            JSONObject jsonObject1 = jsonObject.getJSONObject("params");
            String mobile = jsonObject1.getString("mobile");
            String nickName = jsonObject1.getString("nickName");
            String headFile = jsonObject1.optString("headFile", "");
            String sex = String.valueOf(jsonObject1.optInt("sex"));
            String birthday = jsonObject1.optString("birthday", "");
            data.putString("mobile", mobile);
            data.putString("nickName", nickName);
            data.putString("sex", sex);
            data.putString("birthday", birthday);
            data.putString("headFile", headFile);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 上传通讯录得到的返回json数据
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeFriendMessage(JSONObject jsonObject, int messageWhat,
                                           Handler handler) throws JSONException {
        Log.v("decodeFriendMessage", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int code = jsonObject.optInt("code", 1);
            String desc = jsonObject.optString("desc", "");
            data.putInt("code", code);
            data.putString("desc", desc);
            JSONArray old_people_list = jsonObject.optJSONObject("json").optJSONArray("old_people_list");
            if (old_people_list != null && old_people_list.length() > 0) {
                ArrayList<UserInfo> friends = new ArrayList<UserInfo>();
                for (int i = 0; i < old_people_list.length(); i++) {
                    JSONObject friendJsonObject = old_people_list.getJSONObject(i);
                    String nickname = friendJsonObject.getString("nickName");
                    String mobile = friendJsonObject.optString("mobile");
                    Log.e("moblie", mobile);
                    String userpic = friendJsonObject.optString("headFile");
                    UserInfo user = new UserInfo();
                    user.setNickName(nickname);
                    user.setMobile(mobile);
                    user.setUserpic(userpic);
                    friends.add(user);
                }
                data.putSerializable("old_people_list", friends);
            }
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 查询用户首页运动，睡眠信息
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodeSportSleep(JSONObject jsonObject, int messageWhat,
                                        Handler handler) throws JSONException {
        Log.v("SportandSleep", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String[]> result = new HashMap<>();
            JSONArray jsonArray = jsonObject.optJSONObject("json").optJSONArray("userMsg_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                String parentPhone = object.optString("parentPhone");
                String light_hour = object.optString("light_hour");
                String total_hour_str = object.optString("total_hour_str");
                Log.e("total_hour_str", total_hour_str);
                String light_minute = object.optString("light_minute");
                String deep_hour = object.optString("deep_hour");
                String deep_minute = object.optString("deep_minute");
                String calories = object.optString("calories");
                String distance = object.optString("distance");
                String step = object.optString("step");
                String currentHeart = String.valueOf(object.optInt("currentHeart"));
                String identity = object.optString("identity");
                Log.e("userinfo", identity);
                result.put(parentPhone, new String[]{light_hour, light_minute, deep_hour, deep_minute, total_hour_str, calories, step, distance, identity, currentHeart});
            }
            data.putSerializable("result", result);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }


    /**
     * jsonobject 返回结果校验 ,校验retcode
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private static boolean isRequestOK(JSONObject jsonObject) throws JSONException {
        int retcode = jsonObject.getInt("code");
        if (retcode == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 完成rectcode  ,rectinfo ,params(Map) 响应字段的读取,存入bundle, 完成timestamp的维护
     *
     * @param bundle
     * @param jsonObject
     * @throws JSONException
     */
    private static void insertRecInformation(Bundle bundle, JSONObject jsonObject) throws JSONException {
        int code = jsonObject.getInt("code");
        String desc = jsonObject.getString("desc");
        bundle.putInt("code", code);
        bundle.putString("desc", desc);
        // bundle.putSerializable("params", (HashMap<String, String>) (jsonObject.opt("params")));
    }
}