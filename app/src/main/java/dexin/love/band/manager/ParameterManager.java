package dexin.love.band.manager;

import android.os.Environment;

public class ParameterManager {
    public static int nickNameLength = 8;
    public static final int CURRENT_STATUES = 0; // 当前状态不是睡眠状态，当前状态为运动状态
    public static final String DEVICES_ADDRESS = "device_address"; //sp文件中存储睡眠定位时间间隔的key
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
//    public static final String BAOJIN_SOUND = "baojin_sound"; //保存在sp文件中消息提醒中声音提醒是否打开的key值
//    public static final String BAOJIN_zhend = "baojin_zhend"; //保存在sp文件中消息提醒中震动提醒是否打开的key值
//    public static final String BAOJIN_yuyin = "baojin_yuyin"; //保存在sp文件中消息提醒中语音提醒是否打开的key值
//    public static final String BAOJIN_dialog = "baojin_tanc"; //保存在sp文件中消息提醒中弹窗提醒是否打开的key值
    public static final String LOGIN_TIME = "login_time"; //保存在sp文件中消息提醒中弹窗提醒是否打开的key值
    public static final String TEST_RATE = "TEST_RATE"; //保存在sp文件中
       public static final String BAND_ADRRESS= "band_address";
    public static final int REQURST_CODE_SLEEP = 0X18A;
    public static final int REQURST_CODE_SPORT = 0X19A;
    public static final int REQURST_CODE_NORMAL = 0X29A;
    // public static final int REQURST_CODE_CONNECT = 0X31A;
    public static final String SESSION_CACHE_SP_NAME = "dexin_session_cache";//sp文件名称
    public static final int TOTAL_ROCK_TIME = 60;//计时器总时长
     public static final String FIRSTRATE_TIME = "06:28:15";//心率测试获取的第一个心率值得时间
   // public static final String Local_HOST = "http://192.168.8.133:8080/";//本地服务器
    //public static final String HOST = "http://120.76.27.233:8080/";//外网服务器
    public static final String HOST = "http://139.129.217.190:8080/";//外网服务器
    public static final String SOS = "cuffapi/user/send_sms_sos";
    public static final String FIRMWAREUPGRADE = "cuffapi/user/get_fierware_msg";
    public static final String REJECT_BINDED = "cuffapi/user/reject_binded";
    public static final String GET_CHECK_CODE = HOST+"cuffapi/user/get_check_code?phone=";
    public static final String SIGN_IN_SUBMIT = HOST+"cuffapi/user/sign_in_submit";
    public static final String RESET_PASSWORD = HOST+"cuffapi/user/password_submit";
    public static final String SIGN_UP_COMMIT = HOST+"cuffapi/user/sign_up_submint";
    public static final String UPLOAD_CONTACTS_LIST = HOST+"cuffapi/user/upload_contact_list";
    public static final String GET_USER_BYMOBILE = HOST+"cuffapi/user/get_user_by_phone";
    public static final String UPDATE_USER_MSG = HOST+"cuffapi/user/update_user_msg";
    //public static final int NOTIFICATION_ID = 0X2131;
    public static final String TTS ="http://tts.baidu.com/text2audio";
    public static final String UPDATE_SLEEP_SPORT =HOST+ "cuffapi/sport/insert_user_sport_sleep";
    //性别标杆
    public static final String USER_SEX_CODE_MAN = "1", USER_SEX_CODE_WOMAN = "2";
    public static final String SELECT_BIND_OLD =HOST+ "cuffapi/user/select_bind_old";
    public static final String INSERT_CURRENTRATE =HOST+ "cuffapi/current_heart/insert_user_heart";
    public static final String SELECT_USER_CURRENT_HEART =HOST+ "cuffapi/current_heart/select_user_current_heart";
    public static final String GPS_UPLOAD_URL =HOST+ "cuffapi/gps/operation_user_gps";
    public static final String GET_GPS_FROM_URL = HOST+"cuffapi/gps/select_user_gps";
    public static final String UPLOAD_ZHEXIAN =HOST+ "cuffapi/heart/insert_user_heart";
    public static final String SELECT_USER_HEART =HOST+ "cuffapi/heart/select_user_heart";
    public static final String SELECT_USER_BINDED =HOST+ "cuffapi/current_heart/select_binded_user_msg";
    public static final String APPLICATION_NAME = "dexin.love.band.apk";
    public static final String FIRMWARE_NAME = "upgrade.bin";
    public static String filesDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Suota";
    public static long Time = 30000;
}
