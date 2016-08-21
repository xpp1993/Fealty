package com.lxkj.administrator.fealty.manager;

public class ParameterManager {
    public static int nickNameLength = 8 ;
    public static int messageQueryLength = 10 ;//一次请求的message个数
    public static final int MAX_PHOTO_NUM = 8; // 可以选择的图片数量上限
    public static final int MAX_BODY_TEXT_NUM = 80;//限制的最大字数
    public static final String RECEIVER_FILTER = "com.iv-tech.youyi.receiver";//监听器标杆
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_I_HAS_ISSUE = 0X12A ; //广播标杆 ,接收到此广播 ,则要求自动刷新messageList
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_I_HAS_COMMENT = 0X13A ; //广播标杆 ,接收到此广播 ,则要求自动刷新messageList
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_I_HAS_APPLY = 0X19A ; //广播标杆 ,接收到此广播 ,则要求自动刷新messageList
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_WRITE_OFF = 0X14A ; //广播标杆
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_UPLOAD_CONTACTS_FINISH = 0X15A ; //广播标杆
    public static final int REFRESH_MESSAGE_BROADCAST_TAG_MESSAGE_STATUS_HAS_BEEN_OPT = 0X18A ; //广播标杆 ,接收到此广播 ，表示操作了某一条message的状态，要求刷新这一条message
    public static final int REFRESH_MESSAGE_BROADCAST_STOP_INTENTSERVICE = 0X19A ; //广播标杆 ,接收到此广播 ，停止intentservice
    public static final String SESSION_CACHE_SP_NAME = "dexin_session_cache";//sp文件名称
    public static final int TOTAL_ROCK_TIME = 60 ;//计时器总时长
    public static final long SPLACH_DELAY_TIME = 1500;
    public static final String GET_CHECK_CODE="http://192.168.8.133:8080/cuffapi/user/get_check_code?phone=";
    public static  final String SIGN_IN_SUBMIT="http://192.168.8.133:8080/cuffapi/user/sign_in_submit";
    public static final String RESET_PASSWORD="http://192.168.8.133:8080/cuffapi/user/password_submit";
    public static  final  String SIGN_UP_COMMIT="http://192.168.8.133:8080/cuffapi/user/sign_up_submint";
    public static  final String  UPLOAD_CONTACTS_LIST="http://192.168.8.133:8080/cuffapi/user/upload_contact_list";
    public static final String GET_USER_BYMOBILE="http://192.168.8.133:8080/cuffapi/user/get_user_by_phone";
    public static final String UPDATE_USER_MSG="http://192.168.8.133:8080/cuffapi/user/update_user_msg";
    public static final int NOTIFICATION_ID = 0X2131 ;
    public static  final String UPDATE_SLEEP_SPORT="http://192.168.8.133:8080/cuffapi/sport/insert_user_sport_sleep";
    //性别标杆
    public static final String USER_SEX_CODE_MAN = "1", USER_SEX_CODE_WOMAN = "2";
    public static final String  SELECT_BIND_OLD ="http://192.168.8.133:8080/cuffapi/user/select_bind_old";
    public static final String INSERT_RATE="http://192.168.8.133:8080/cuffapi/heart/insert_user_heart";
    public static final String SELECT_USER_CURRENT_HEART= "http://192.168.8.133:8080/cuffapi/current_heart/select_user_current_heart";
}
