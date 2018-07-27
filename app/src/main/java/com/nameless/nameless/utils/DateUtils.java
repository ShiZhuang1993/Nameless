package com.nameless.nameless.utils;

/**
 * 格式化时间
 */
public class DateUtils {

    public String getTime(){

        long time=System.currentTimeMillis();//获取系统时间的10位的时间戳

        String  str=String.valueOf(time);

        return str;

    }

}