package com.nameless.nameless.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * ===================================
 * describe:
 * date:2018/7/2
 * author:zhuang
 * ===================================
 */

public class MobileIdentification {
    /**
     * 获取手机IMEI号
     * <p>
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {

        String imei = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {

        }
        return imei;
    }

}
