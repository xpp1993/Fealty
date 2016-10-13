package com.example.xpp.blelib;

/**
 * Created by XPP on 2016/10/6/006.
 */

public class GlobalValues {
    //********************************广播类型********************************
    /**
     * 广播类型
     * 连接状态
     */
    public static final String BROADCAST_INTENT_CONNECT_STATE_CHANGED = "BROADCAST_INTENT_CONNECT_STATE_CHANGED";
    /**
     * 广播类型
     * 当前运动信息
     */
    public static final String BROADCAST_INTENT_CURRENTMOTION = "BROADCAST_INTENT_CURRENTMOTION";
    /**
     * 广播类型
     * 电量
     */
    public static final String BROADCAST_INTENT_ELECTRICITY = "BROADCAST_INTENT_ELECTRICITY";
    /**
     * 广播类型
     * 指令送达
     */
    public static final String BROADCAST_INTENT_COMMAND_RECEIVED = "BROADCAST_INTENT_COMMAND_RECEIVED";
    /**
     * 广播类型
     * 睡眠质量数据
     */
    public static final String BROADCAST_INTENT_SLEEPQ = "BROADCAST_INTENT_SLEEPQ";
    /**
     * 广播类型
     * 一键报警
     */
    public static final String BROADCAST_INTENT_A_KEY_ALARM = "BROADCAST_INTENT_A_KEY_ALARM";
    /**
     * 广播类型
     * 心率测试数据
     */
    public static final String BROADCAST_INTENT_RATE = "BROADCAST_INTENT_RATE";
    /**
     * 广播类型
     * 发送手环信息数据，固件版本，MAC以及穿戴状态
     */
    public static final String BROADCAST_INTENT_BAND_INFO = "BROADCAST_INTENT_BAND_INFO";
    /**
     * 广播类型
     * 发送心率停止测试
     */
    public static final String BROADCAST_INTENT_STOPRATE = "BROADCAST_INTENT_STOPRATE";
    //********************************广播类型********************************


    //********************************值类型********************************
    public static final String VALUE_CONNECT_STATE_YES = "VALUE_CONNECT_STATE_YES";
    public static final String VALUE_CONNECT_STATE_NO = "VALUE_CONNECT_STATE_NO";
    /**
     * 01为手环处于充电状态
     */
    public static final String VALUE_ISELECTRICITE_YES = "01";
    /**
     * 00为手环处于未充电状态
     */
    public static final String VALUE_ISELECTRICITE_NO = "00";
    //********************************值类型********************************


    //********************************名称类型********************************
    /**
     * 名称类型
     * 连接状态
     */
    public static final String NAME_CONNECT_STATE = "NAME_CONNECT_STATE";

    /**
     * 名称类型
     * 当前运动信息
     */
    public static final String NAME_DEVICE_TIME = "NAME_DEVICE_TIME";

    /**
     * 名称类型
     * 当前睡眠时间信息
     */
    public static final String NAME_SLEEP = "NAME_SLEEP";

    /**
     * 名称类型
     * 当前步数信息
     */
    public static final String NAME_STEPS = "NAME_STEPS";

    /**
     * 名称类型
     * 当前运动距离信息
     */
    public static final String NAME_DISTANCE = "NAME_DISTANCE";

    /**
     * 名称类型
     * 当前消耗卡路里信息
     */
    public static final String NAME_CALORIES = "NAME_CALORIES";
    /**
     * 名称类型
     * 电量
     */
    public static final String NAME_ELECTRICITY = "NAME_ELECTRICITY";
    /**
     * 名称类型
     * 是否充电
     */
    public static final String NAME_ISELECTRICITE = "NAME_ISELECTRICITE";
    /**
     * 名称类型
     * 浅度睡眠时间
     */
    public static final String NAME_LIGHTSLEEP = "NAME_LIGHTSLEEP";
    /**
     * 名称类型
     * 深度睡眠时间
     */
    public static final String NAME_DEEPSLEEP = "NAME_DEEPSLEEP";
    /**
     * 名称类型
     * 心率测试当前时间
     */
    public static final String NAME_RATETIME = "NAME_RATETIME";
    /**
     * 名称类型
     * 测试心率值
     */
    public static final String NAME_RATE = "NAME_RATE";
    /**
     * 名称类型
     * 心率测试的状态，是否是睡眠状态，运动状态
     */
    public static final String NAME_RATE_STATUS = "NAME_RATE_STATUS";
    /**
     * 名称类型
     * 手环固件版本
     */
    public static final String NAME_VERSION = "NAME_VERSION";
    /**
     * 名称类型
     * 手环MAC地址
     */
    public static final String NAME_MAC = "NAME_MAC";
    /**
     * 名称类型
     * 手环的状态，是否是穿戴状态
     */
    public static final String NAME_BAND_STATUS = "NAME_BAND_STATUS";
    //********************************名称类型********************************


    //********************************蓝牙指令********************************
    /**
     * 蓝牙指令
     * 电量指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_ELECTRICITY = "0b";
    /**
     * 蓝牙指令
     * 读取手环信息指令（固件版本，MAC地址）
     */
    public static final String BLE_COMMAND_TYPE_CODE_VERSIONMAC = "0c";
    /**
     * 蓝牙指令
     * 时间指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_TIME = "01";
    /**
     * 蓝牙指令
     * 当前运动信息
     */
    public static final String BLE_COMMAND_TYPE_CODE_CURRENTMOTION = "03";
    /**
     * 蓝牙指令
     * 振动
     */
    public static final String BLE_COMMAND_TYPE_CODE_VIBRATION = "04";
    /**
     * 蓝牙指令
     * 同步睡眠信息
     */
    public static final String BLE_COMMAND_TYPE_CODE_SYNSLEEP = "10";
    /**
     * 蓝牙指令
     * 同步睡眠信息返回
     */
    public static final String BLE_COMMAND_TYPE_CODE_SYNSLEEP_BACK = "11";
    /**
     * 蓝牙指令
     * 发送心率测试开始指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_RATESTART = "14";
    /**
     * 蓝牙指令
     * 发送心率测试停止指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_RATESTOP = "15";
    /**
     * 蓝牙指令
     * 同步睡眠信息回复无数据
     */
    public static final String BLE_COMMAND_TYPE_CODE_SYNSLEEPNO = "90";
    /**
     * 蓝牙指令
     * 一键报警
     */
    public static final String BLE_COMMAND_TYPE_CODE_A_KEY_ALARM= "20";
    /**
     * 蓝牙指令
     * 进入固件升级模式命令
     */
    public static final String BLE_COMMAND_TYPE_CODE_FIRMWOREUPGRADE= "0E12345678FEDCBA98";
    //********************************蓝牙指令********************************
}
