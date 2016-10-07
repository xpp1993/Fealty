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
    //********************************广播类型********************************


    //********************************值类型********************************
    public static final String VALUE_CONNECT_STATE_YES = "VALUE_CONNECT_STATE_YES";
    public static final String VALUE_CONNECT_STATE_NO = "VALUE_CONNECT_STATE_NO";
    //********************************值类型********************************


    //********************************名称类型********************************
    /** 名称类型
     * 连接状态
     */
    public static final String NAME_CONNECT_STATE = "NAME_CONNECT_STATE";

    /** 名称类型
     * 当前运动信息
     */
    public static final String NAME_DEVICE_TIME = "NAME_DEVICE_TIME";

    /** 名称类型
     * 当前睡眠时间信息
     */
    public static final String NAME_SLEEP = "NAME_SLEEP";

    /** 名称类型
     * 当前步数信息
     */
    public static final String NAME_STEPS = "NAME_STEPS";

    /** 名称类型
     * 当前运动距离信息
     */
    public static final String NAME_DISTANCE = "NAME_DISTANCE";

    /** 名称类型
     * 当前消耗卡路里信息
     */
    public static final String NAME_CALORIES = "NAME_CALORIES";
    /** 名称类型
     * 电量
     */
    public static final String NAME_ELECTRICITY = "NAME_ELECTRICITY";
    //********************************名称类型********************************



    //********************************蓝牙指令********************************
    /** 蓝牙指令
     * 电量指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_ELECTRICITY = "0b";
    /** 蓝牙指令
     * 时间指令
     */
    public static final String BLE_COMMAND_TYPE_CODE_TIME = "01";
    /**蓝牙指令
     * 当前运动信息
     */
    public static final String BLE_COMMAND_TYPE_CODE_CURRENTMOTION = "03";
    /**蓝牙指令
     * 振动
     */
    public static final String BLE_COMMAND_TYPE_CODE_VIBRATION = "04";
    //********************************蓝牙指令********************************
}
