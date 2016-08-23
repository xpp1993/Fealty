package com.lxkj.administrator.fealty.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lxkj.administrator.fealty.bean.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static void decodeLogin(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodeLogin", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int identity = jsonObject.optInt("identity", 0);
            int binded = jsonObject.optInt("binded", 0);
            String mobile = jsonObject.optString("mobile");
            String password = jsonObject.optString("password");
            String nickName = jsonObject.optString("nickName");
            String headFile = jsonObject.optString("headFile");
            data.putInt("identity", identity);
            data.putInt("binded", binded);
            data.putString("mobile", mobile);
            data.putString("password", password);
            data.putString("nickName", nickName);
            data.putString("headFile", headFile);
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
/**
 * {
 "success": true,
 "code": 1,
 "desc": "查询心率成功",
 "json": {
 "pageSize": 20,
 "totalCount": 0,
 "pageNo": 1,
 "start": 0,
 "limit": 20,
 "gpsMsg_list": [
 {
 "pageSize": 20,
 "totalCount": 0,
 "pageNo": 1,
 "start": 0,
 "limit": 20,
 "lat": "1",
 "lon": "1",
 "locationdescrible": "江西",
 "address": "南昌",
 "parentPhone": "18907084338"
 }
 ]
 }
 }
 */
        if (isRequestOK(jsonObject)) {
            HashMap<String,String[]> result = new HashMap<>();
            JSONArray jsonArray = jsonObject.getJSONObject("json").getJSONArray("gpsMsg_list");
            for (int i = 0; i < jsonArray.length(); i++) {
//                Object object = jsonArray.get(i);
                JSONObject object = jsonArray.getJSONObject(i);
                String lat = object.optString("lat");
                String lon = object.optString("lon");
                String locationdescrible = object.optString("locationdescrible");
                String address = object.optString("address");
                String parentPhone = object.optString("parentPhone");
                result.put(parentPhone,new String[]{lat,lon,locationdescrible,address});
            }
            data.putSerializable("result",result);
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
        insertRecInformation(data, jsonObject);
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
        insertRecInformation(data, jsonObject);

        if (isRequestOK(jsonObject)) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("json").getJSONObject("user");
            String mobile = jsonObject1.getString("mobile");
            String nickName = jsonObject1.getString("nickName");
            String headFile = jsonObject1.optString("headFile", "");
            String sex = String.valueOf(jsonObject1.optInt("sex", 0) == 0 ? "" : jsonObject1.getInt("sex"));
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
            int relation = jsonObject.getInt("relation");
            String friend = jsonObject.optString("friend", "");
            String nickname = jsonObject.getString("nickname");
            String userpic = jsonObject.optString("userpic", "");
            String gender = String.valueOf(jsonObject.optInt("gender", 0) == 0 ? "" : jsonObject.getInt("gender"));

            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String q_userid = params.get("q_userid");

//            User user = new User();
//            user.setUserid(q_userid);
//            user.setNickName(nickname);
//            user.setUserpic(userpic);
//            user.setRelation(relation);
//            user.setFriend(friend);
//            user.setGender(gender);
//            data.putSerializable("user", user);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 小红点查询
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws JSONException
     */
    public static void decodePostStatus(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
        Log.v("decodePostStatus", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);

        if (isRequestOK(jsonObject)) {

            int my_pub_update_cnt = jsonObject.optInt("my_pub_update_cnt", 0);
            int my_wants_update_cnt = jsonObject.optInt("my_wants_update_cnt", 0);
            int my_comments_update_cnt = jsonObject.optInt("my_comments_update_cnt", 0);

            data.putInt("my_pub_update_cnt", my_pub_update_cnt);
            data.putInt("my_wants_update_cnt", my_wants_update_cnt);
            data.putInt("my_comments_update_cnt", my_comments_update_cnt);
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

    public static void decodeComment(JSONObject jsonObject, int messageWhat,
                                     Handler handler) throws JSONException {
        Log.v("decodeComment", jsonObject.toString());
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);

        if (isRequestOK(jsonObject)) {
            int id = jsonObject.getInt("id");
            data.putInt("id", id);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解析 信息流查询
     *
     * @param jsonObject
     * @param handler
     * @throws JSONException
     */
//    public static void decodeShowMessageStream(JSONObject jsonObject, int messageWhat, Handler handler) throws JSONException {
//        Log.v("decodeShowMessageStream", jsonObject.toString());
//        Message msg = new Message();
//        Bundle data = new Bundle();
//        msg.what = messageWhat;
//        insertRecInformation(data, jsonObject);
//
//        if (isRequestOK(jsonObject)) {
//            //维护sessionupdate部分
//            JSONObject session_update = jsonObject.optJSONObject("session_update");
//            if (session_update != null) {
//                String sess_id = session_update.getString("sess_id");
//                String sess_key = session_update.getString("sess_key");
//                String login_time = session_update.getString("login_time");
//                data.putString("sess_id", sess_id);
//                data.putString("sess_key", sess_key);
//                data.putString("login_time", login_time);
//            }
//
//            //维护contact checksum字段
//            JSONObject contact = jsonObject.optJSONObject("contact");
//            if (contact != null) {
//                String checksum = contact.getString("checksum");
//                String checksum_extra = contact.optString("checksum_extra");
//                data.putString("checksum", checksum);
//                data.putString("checksum_extra", checksum_extra);
//            }
//
//            //维护信息条数部分
//            int post_cnt = jsonObject.getInt("post_cnt");
//            data.putInt("post_cnt", post_cnt);
//
//            //维护信息列表部分
//            ArrayList<Show> showMessages = new ArrayList<Show>();
//            JSONArray post_list = jsonObject.optJSONArray("post_list");
//            if (post_list != null && post_list.length() > 0) {
//                for (int i = 0; i < post_list.length(); i++) {
//                    JSONObject messageJsonObject = post_list.getJSONObject(i);
//                    int id = messageJsonObject.getInt("id");
//                    int type = messageJsonObject.getInt("type");
//                    int status = messageJsonObject.getInt("status");
//                    String userid = messageJsonObject.getString("userid");
//                    String mobile = messageJsonObject.getString("mobile");
//                    String userpic = messageJsonObject.optString("userpic", "");
//                    String nickname = messageJsonObject.getString("nickname");
//                    int relation = messageJsonObject.getInt("relation");
//                    String friend = messageJsonObject.optString("friend", "");
//                    String body = messageJsonObject.optString("body", "");
//                    String pub_time = messageJsonObject.getString("pub_time");
//                    String location = messageJsonObject.optString("location", "未知位置");
//
//                    User user = new User();
//                    user.setUserid(userid);
//                    user.setMobilePlain(mobile);
//                    user.setUserpic(userpic);
//                    user.setNickName(nickname);
//                    user.setRelation(relation);
//                    user.setFriend(friend);
//
//                    Show showMessage = null;
//
//                    if (type == com.ivtech.luffy.entity.Message.MESSAGE_TYPT_OUT) {//想出
//                        //维护图片集合
//                        List<String> images = new ArrayList<String>();
//                        JSONArray pic_s = messageJsonObject.optJSONArray("pic_s");
//                        if (pic_s != null && pic_s.length() > 0) {//如果有图片
//                            for (int k = 0; k < pic_s.length(); k++) {
//                                String pic_url = pic_s.getString(k);
//                                images.add(pic_url);
//                            }
//                        }
//
//                        //如果是想卖信息
//                        if (messageJsonObject.optInt("sell", 0) == 1) {
//                            showMessage = new SaleShowMessage();
//                            int sell_price = messageJsonObject.optInt("sell_price", 0);
//                            ((SaleShowMessage) showMessage).setSell_price(sell_price);
//                            ((SaleShowMessage) showMessage).setPic_s(images);
//                        }
//
//                        //如果是想送信息
//                        if (messageJsonObject.optInt("give", 0) == 1) {
//                            showMessage = new PresentShowMessage();
//                            int give_dest = messageJsonObject.getInt("give_dest");
//                            int give_dest_choose = messageJsonObject.getInt("give_dest_choose");
//                            int give_wantcnt = messageJsonObject.optInt("give_wantcnt", 0);
//                            String give_expire = messageJsonObject.optString("give_expire", "");
//                            int give_can_i_want = messageJsonObject.optInt("give_can_i_want", 0);
//                            int my_want = messageJsonObject.optInt("my_want", 0);
//                            int my_want_choosen = messageJsonObject.optInt("my_want_choosen", 0);
//                            ((PresentShowMessage) showMessage).setGive_dest(give_dest);
//                            ((PresentShowMessage) showMessage).setGive_dest_choose(give_dest_choose);
//                            ((PresentShowMessage) showMessage).setGive_expire(give_expire);
//                            ((PresentShowMessage) showMessage).setPic_s(images);
//                            ((PresentShowMessage) showMessage).setGive_wantcnt(give_wantcnt);
//                            ((PresentShowMessage) showMessage).setMy_want(my_want);
//                            ((PresentShowMessage) showMessage).setGive_can_i_want(give_can_i_want);
//                            ((PresentShowMessage) showMessage).setMy_want_choosen(my_want_choosen);
//
//                        }
//                    } else if (type == com.ivtech.luffy.entity.Message.MESSAGE_TYPT_IN) {//想入信息
//                        showMessage = new WantShowMessage();
//                        int in_price = messageJsonObject.optInt("in_price", 0);
//                        ((WantShowMessage) showMessage).setIn_price(in_price);
//                    }
//
//                    //维护留言部分
//                    List<Comment> comments = new ArrayList<Comment>();
//                    JSONArray commentJsonArray = messageJsonObject.optJSONArray("comments");
//                    if (commentJsonArray != null && commentJsonArray.length() > 0) {
//                        for (int j = 0; j < commentJsonArray.length(); j++) {
//
//                            JSONObject commentJsonObject = commentJsonArray.getJSONObject(j);
//                            int commentId = commentJsonObject.getInt("id");
//                            String fromUserId = commentJsonObject.getString("userid");
//                            String fromNickname = commentJsonObject.getString("nickname");
//                            String fromUserPic = commentJsonObject.optString("userpic", "");
//                            String toUserID = commentJsonObject.optString("to_userid", "");
//                            String toNickName = commentJsonObject.optString("to_nickname", "");
//                            String toUserPic = commentJsonObject.optString("to_userpic", "");
//                            String commentBody = commentJsonObject.getString("body");
//                            String commentTime = commentJsonObject.getString("pub_time");
//
//                            if (TextUtils.isEmpty(toUserID)) {
//                                Comment comment = new Comment();
//                                comment.setId(commentId);
//                                comment.setUserid(fromUserId);
//                                comment.setNickname(fromNickname);
//                                comment.setUserpic(fromUserPic);
//                                comment.setBody(commentBody);
//                                comment.setPub_time(commentTime);
//                                comment.setMessage_id(id);
//                                comments.add(comment);
//                            } else {
//                                SecondComment comment = new SecondComment();
//                                comment.setId(commentId);
//                                comment.setUserid(fromUserId);
//                                comment.setNickname(fromNickname);
//                                comment.setUserpic(fromUserPic);
//                                comment.setBody(commentBody);
//                                comment.setPub_time(commentTime);
//                                comment.setTo_userid(toUserID);
//                                comment.setTo_nickname(toNickName);
//                                comment.setTo_userpic(toUserPic);
//                                comment.setMessage_id(id);
//                                comments.add(comment);
//                            }
//                        }
//                    }
//                    if (showMessage != null) {
//                        ((com.ivtech.luffy.entity.Message) showMessage).setId(id);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setStatus(status);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setBody(body);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setPub_time(pub_time);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setLocation(location);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setComments(comments);
//                        ((com.ivtech.luffy.entity.Message) showMessage).setUser(user);
//                        showMessages.add(showMessage);
//                    }
//                }
//            }
//            data.putSerializable("post_list", showMessages);
//        }
//        msg.setData(data);
//        handler.sendMessage(msg);
//    }

//    public static void decodeMineMessageStream(JSONObject jsonObject, int messageWhat,
//                                               Handler handler) throws JSONException {
//        Log.v("decodeMineMessageStream", jsonObject.toString());
//        Message msg = new Message();
//        Bundle data = new Bundle();
//        msg.what = messageWhat;
//        insertRecInformation(data, jsonObject);
//
//        if (isRequestOK(jsonObject)) {
//
//            //维护信息条数部分
//            int post_cnt = jsonObject.getInt("post_cnt");
//            data.putInt("post_cnt", post_cnt);
//
//            //维护信息列表部分
//            ArrayList<Mine> mineMessages = new ArrayList<Mine>();
//            JSONArray post_list = jsonObject.optJSONArray("post_list");
//            if (post_list != null && post_list.length() > 0) {
//                for (int i = 0; i < post_list.length(); i++) {
//                    JSONObject messageJsonObject = post_list.getJSONObject(i);
//                    int id = messageJsonObject.getInt("id");
//                    int type = messageJsonObject.getInt("type");
//                    String body = messageJsonObject.getString("body");
//                    int status = messageJsonObject.getInt("status");
//                    String pub_time = messageJsonObject.getString("pub_time");
//                    String location = messageJsonObject.optString("location", "未知位置");
//
//                    Mine mineMessage = null;
//
//                    if (type == com.ivtech.luffy.entity.Message.MESSAGE_TYPT_OUT) {//想出
//                        //维护图片集合
//                        List<String> images = new ArrayList<String>();
//                        JSONArray pic_s = messageJsonObject.optJSONArray("pic_s");
//                        if (pic_s != null && pic_s.length() > 0) {//如果有图片
//                            for (int k = 0; k < pic_s.length(); k++) {
//                                String pic_url = pic_s.getString(k);
//                                images.add(pic_url);
//                            }
//                        }
//
//                        //如果是想卖信息
//                        if (messageJsonObject.optInt("sell", 0) == 1) {
//                            mineMessage = new SaleMineMessage();
//                            int sell_price = messageJsonObject.optInt("sell_price", 0);
//                            ((SaleMineMessage) mineMessage).setSell_price(sell_price);
//                            ((SaleMineMessage) mineMessage).setPic_s(images);
//                        }
//
//                        //如果是想送信息
//                        if (messageJsonObject.optInt("give", 0) == 1) {
//                            mineMessage = new PresentMineMessage();
//                            int give_dest = messageJsonObject.getInt("give_dest");
//                            int give_dest_choose = messageJsonObject.getInt("give_dest_choose");
//                            String give_choosen_userid = messageJsonObject.optString("give_choosen_userid", "");
//                            String give_choosen_nickname = messageJsonObject.optString("give_choosen_nickname", "");
//                            String give_choosen_mobile = messageJsonObject.optString("give_choosen_mobile", "");
//                            int give_wantcnt = messageJsonObject.optInt("give_wantcnt", 0);
//                            String give_expire = messageJsonObject.optString("give_expire", "");
//                            //维护想要用户列表
//                            List<Expire> expires = new ArrayList<Expire>();
//                            JSONArray give_wantsJsonArray = messageJsonObject.optJSONArray("give_wants");
//                            if (give_wantsJsonArray != null && give_wantsJsonArray.length() > 0) {
//                                for (int n = 0; n < give_wantsJsonArray.length(); n++) {
//                                    Expire expire = new Expire();
//                                    JSONObject expireJsonObject = give_wantsJsonArray.getJSONObject(n);
//                                    int expireID = expireJsonObject.getInt("id");
//                                    String expireUserID = expireJsonObject.getString("userid");
//                                    String expireUserNickname = expireJsonObject.getString("nickname");
//                                    String userpic = expireJsonObject.optString("userpic");
//                                    String expireWantTime = expireJsonObject.getString("want_time");
//                                    String expireUserMobile = expireJsonObject.getString("mobile");
//                                    User user = new User();
//                                    user.setUserid(expireUserID);
//                                    user.setUserpic(userpic);
//                                    user.setNickName(expireUserNickname);
//                                    user.setMobilePlain(expireUserMobile);
//                                    expire.setId(expireID);
//                                    expire.setUser(user);
//                                    expire.setWant_time(expireWantTime);
//                                    expires.add(expire);
//                                }
//                            }
//
//                            ((PresentMineMessage) mineMessage).setGive_dest(give_dest);
//                            ((PresentMineMessage) mineMessage).setGive_dest_choose(give_dest_choose);
//                            ((PresentMineMessage) mineMessage).setGive_choosen_userid(give_choosen_userid);
//                            ((PresentMineMessage) mineMessage).setGive_choosen_nickname(give_choosen_nickname);
//                            ((PresentMineMessage) mineMessage).setGive_choosen_mobile(give_choosen_mobile);
//                            ((PresentMineMessage) mineMessage).setGive_wantcnt(give_wantcnt);
//                            ((PresentMineMessage) mineMessage).setGive_expire(give_expire);
//                            ((PresentMineMessage) mineMessage).setExpires(expires);
//                            ((PresentMineMessage) mineMessage).setPic_s(images);
//                        }
//                    } else if (type == com.ivtech.luffy.entity.Message.MESSAGE_TYPT_IN) {//想入信息
//                        mineMessage = new WantMineMessage();
//                        int in_price = messageJsonObject.optInt("in_price", 0);
//                        ((WantMineMessage) mineMessage).setIn_price(in_price);
//                    }
//
//                    //维护留言部分
//                    List<Comment> comments = new ArrayList<Comment>();
//                    JSONArray commentJsonArray = messageJsonObject.optJSONArray("comments");
//                    if (commentJsonArray != null && commentJsonArray.length() > 0) {
//                        for (int j = 0; j < commentJsonArray.length(); j++) {
//
//                            JSONObject commentJsonObject = commentJsonArray.getJSONObject(j);
//                            int commentId = commentJsonObject.getInt("id");
//                            String fromUserId = commentJsonObject.getString("userid");
//                            String fromNickname = commentJsonObject.getString("nickname");
//                            String fromUserPic = commentJsonObject.optString("userpic", "");
//                            String toUserID = commentJsonObject.optString("to_userid", "");
//                            String toNickName = commentJsonObject.optString("to_nickname", "");
//                            String toUserPic = commentJsonObject.optString("to_userpic", "");
//                            String commentBody = commentJsonObject.getString("body");
//                            String commentTime = commentJsonObject.getString("pub_time");
//
//                            if (TextUtils.isEmpty(toUserID)) {
//                                Comment comment = new Comment();
//                                comment.setId(commentId);
//                                comment.setUserid(fromUserId);
//                                comment.setNickname(fromNickname);
//                                comment.setUserpic(fromUserPic);
//                                comment.setBody(commentBody);
//                                comment.setPub_time(commentTime);
//                                comment.setMessage_id(id);
//                                comments.add(comment);
//                            } else {
//                                SecondComment comment = new SecondComment();
//                                comment.setId(commentId);
//                                comment.setUserid(fromUserId);
//                                comment.setNickname(fromNickname);
//                                comment.setUserpic(fromUserPic);
//                                comment.setBody(commentBody);
//                                comment.setPub_time(commentTime);
//                                comment.setTo_userid(toUserID);
//                                comment.setTo_nickname(toNickName);
//                                comment.setTo_userpic(toUserPic);
//                                comment.setMessage_id(id);
//                                comments.add(comment);
//                            }
//                        }
//                    }
//                    if (mineMessage != null) {
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setId(id);
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setStatus(status);
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setBody(body);
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setPub_time(pub_time);
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setLocation(location);
//                        ((com.ivtech.luffy.entity.Message) mineMessage).setComments(comments);
//                        mineMessages.add(mineMessage);
//                    }
//                }
//            }
//            data.putSerializable("post_list", mineMessages);
//        }
//        msg.setData(data);
//        handler.sendMessage(msg);
//    }

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
        bundle.putSerializable("params", (HashMap<String, String>) (jsonObject.opt("params")));

    }
}