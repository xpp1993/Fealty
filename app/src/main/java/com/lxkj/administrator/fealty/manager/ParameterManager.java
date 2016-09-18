package com.lxkj.administrator.fealty.manager;

public class ParameterManager {
    public static int nickNameLength = 8;
    //    public static int messageQueryLength = 10;//一次请求的message个数
//    public static final int MAX_PHOTO_NUM = 8; // 可以选择的图片数量上限
//    public static final int MAX_BODY_TEXT_NUM = 80;//限制的最大字数
//    public static final String RECEIVER_FILTER = "com.iv-tech.youyi.receiver";//监听器标杆
    public static final String SHEZHI_SLEEP_GPS = "sleep_gps"; //sp文件中存储睡眠定位时间间隔的key
    public static final String SHEZHI_SPORT_GPS = "sport_gps"; //sp文件中存储运动定位时间间隔的key
    public static final String SHEZHI_JIANCEGPS = "jiance_gps"; //sp文件中存储间测定位时间间隔的key
    public static final String SHEZHI_JIANCEXINLV = "jiance_xinlv"; //sp文件中存储间测时间间隔的key
    public static final String NORMAL_RATE_MIN_MINUTE = "xinlv_min"; //保存在sp文件中正常心率范围的最低心率的key值
    public static final String NORMAL_RATE_MAX_MINUTE = "xinlv_max"; //保存在sp文件中正常心率范围的最高心率的key值
    public static final String SPORT_RATE_MIN_MINUTE = "sport_min"; //保存在sp文件中运动心率范围的最低心率的key值
    public static final String SPORT_RATE_MAX_MINUTE = "sport_max"; //保存在sp文件中运动心率范围的最高心率的key值
    public static final String SLEEP_RATE_MIN_MINUTE = "sleep_min"; //保存在sp文件中正常心率范围的最低心率的key值
    public static final String SLEEP_RATE_MAX_MINUTE = "sleep_max"; //保存在sp文件中正常心率范围的最高心率的key值
    public static final String MESSAGE_SOUND = "message_sound"; //保存在sp文件中消息提醒中声音提醒是否打开的key值
    public static final String MESSAGE_zhend = "message_zhend"; //保存在sp文件中消息提醒中震动提醒是否打开的key值
    public static final String MESSAGE_yuyin = "message_yuyin"; //保存在sp文件中消息提醒中语音提醒是否打开的key值
    public static final String MESSAGE_dialog = "message_tanc"; //保存在sp文件中消息提醒中弹窗提醒是否打开的key值
    public static final String BAOJIN_SOUND = "baojin_sound"; //保存在sp文件中消息提醒中声音提醒是否打开的key值
    public static final String BAOJIN_zhend = "baojin_zhend"; //保存在sp文件中消息提醒中震动提醒是否打开的key值
    public static final String BAOJIN_yuyin = "baojin_yuyin"; //保存在sp文件中消息提醒中语音提醒是否打开的key值
    public static final String BAOJIN_dialog = "baojin_tanc"; //保存在sp文件中消息提醒中弹窗提醒是否打开的key值
    public static final int REQURST_CODE_SLEEP = 0X18A;
    public static final int REQURST_CODE_SPORT = 0X19A;
    public static final int REQURST_CODE_NORMAL = 0X29A;
    public static final int REQURST_CODE_CONNECT = 0X31A;
    public static final String SESSION_CACHE_SP_NAME = "dexin_session_cache";//sp文件名称
    public static final int TOTAL_ROCK_TIME = 60;//计时器总时长
    public static final String DEVICE_ADDRESS = "diveceAddress";//设备地址存储在sp文件中的key
//        public static final String GET_CHECK_CODE = "http://192.168.8.133:8080/cuffapi/user/get_check_code?phone=";
//    public static final String SIGN_IN_SUBMIT = "http://192.168.8.133:8080/cuffapi/user/sign_in_submit";
//    public static final String RESET_PASSWORD = "http://192.168.8.133:8080/cuffapi/user/password_submit";
//    public static final String SIGN_UP_COMMIT = "http://192.168.8.133:8080/cuffapi/user/sign_up_submint";
//    public static final String UPLOAD_CONTACTS_LIST = "http://192.168.8.133:8080/cuffapi/user/upload_contact_list";
//    public static final String GET_USER_BYMOBILE = "http://192.168.8.133:8080/cuffapi/user/get_user_by_phone";
//    public static final String UPDATE_USER_MSG = "http://192.168.8.133:8080/cuffapi/user/update_user_msg";
//    // public static final int NOTIFICATION_ID = 0X2131 ;
//    public static final String UPDATE_SLEEP_SPORT = "http://192.168.8.133:8080/cuffapi/sport/insert_user_sport_sleep";
//    //性别标杆
//    public static final String USER_SEX_CODE_MAN = "1", USER_SEX_CODE_WOMAN = "2";
//    public static final String SELECT_BIND_OLD = "http://192.168.8.133:8080/cuffapi/user/select_bind_old";
//    public static final String INSERT_CURRENTRATE = "http://192.168.8.133:8080/cuffapi/current_heart/insert_user_heart";
//    public static final String SELECT_USER_CURRENT_HEART = "http://192.168.8.133:8080/cuffapi/current_heart/select_user_current_heart";
//    public static final String GPS_UPLOAD_URL = "http://192.168.8.133:8080/cuffapi/gps/operation_user_gps";
//    public static final String GET_GPS_FROM_URL = "http://192.168.8.133:8080/cuffapi/gps/select_user_gps";
//    public static final String UPLOAD_ZHEXIAN = "http://192.168.8.133:8080/cuffapi/heart/insert_user_heart";
//    public static final String SELECT_USER_HEART="http://192.168.8.133:8080/cuffapi/heart/select_user_heart";
//    public static final String SELECT_USER_BINDED="http://192.168.8.133:8080/cuffapi/current_heart/select_binded_user_msg";
    public static final String GET_CHECK_CODE = "http://120.76.27.233:8080/cuffapi/user/get_check_code?phone=";
    public static final String SIGN_IN_SUBMIT = "http://120.76.27.233:8080/cuffapi/user/sign_in_submit";
    public static final String RESET_PASSWORD = "http://120.76.27.233:8080/cuffapi/user/password_submit";
    public static final String SIGN_UP_COMMIT = "http://120.76.27.233:8080/cuffapi/user/sign_up_submint";
    public static final String UPLOAD_CONTACTS_LIST = "http://120.76.27.233:8080/cuffapi/user/upload_contact_list";
    public static final String GET_USER_BYMOBILE = "http://120.76.27.233:8080/cuffapi/user/get_user_by_phone";
    public static final String UPDATE_USER_MSG = "http://120.76.27.233:8080/cuffapi/user/update_user_msg";
    public static final int NOTIFICATION_ID = 0X2131;
    public static final String UPDATE_SLEEP_SPORT = "http://120.76.27.233:8080/cuffapi/sport/insert_user_sport_sleep";
    //性别标杆
    public static final String USER_SEX_CODE_MAN = "1", USER_SEX_CODE_WOMAN = "2";
    public static final String SELECT_BIND_OLD = "http://120.76.27.233:8080/cuffapi/user/select_bind_old";
    public static final String INSERT_CURRENTRATE = "http://120.76.27.233:8080/cuffapi/current_heart/insert_user_heart";
    public static final String SELECT_USER_CURRENT_HEART = "http://120.76.27.233:8080/cuffapi/current_heart/select_user_current_heart";
    public static final String GPS_UPLOAD_URL = "http://120.76.27.233:8080/cuffapi/gps/operation_user_gps";
    public static final String GET_GPS_FROM_URL = "http://120.76.27.233:8080/cuffapi/gps/select_user_gps";
    public static final String UPLOAD_ZHEXIAN = "http://120.76.27.233:8080/cuffapi/heart/insert_user_heart";
    public static final String SELECT_USER_HEART = "http://120.76.27.233:8080/cuffapi/heart/select_user_heart";
    public static final String SELECT_USER_BINDED = "http://120.76.27.233:8080/cuffapi/current_heart/select_binded_user_msg";
}
