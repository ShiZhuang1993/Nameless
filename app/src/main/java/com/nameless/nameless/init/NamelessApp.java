package com.nameless.nameless.init;

import android.app.Application;
import android.content.Context;

/**
 * ===================================
 * describe:
 * date:2018/6/25
 * author:zhuang
 * ===================================
 */

public class NamelessApp extends Application {
    public static Context mContext;

    //全局上下文
    public static Context getContext() {

        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
