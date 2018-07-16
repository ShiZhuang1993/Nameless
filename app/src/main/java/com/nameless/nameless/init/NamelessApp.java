package com.nameless.nameless.init;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

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
        // 获取MAN服务
        MANService manService = MANServiceProvider.getService();
        // 打开调试日志，线上版本建议关闭
        // manService.getMANAnalytics().turnOnDebug();
        // 设置渠道（用以标记该app的分发渠道名称），如果不关心可以不设置即不调用该接口，
        // 渠道设置将影响控制台【渠道分析】栏目的报表展现。如果文档3.3章节更能满足您渠道配置的需求，
        // 就不要调用此方法，按照3.3进行配置即可；1.1.6版本及之后的版本，请在init方法之前调用此方法设置channel.
        manService.getMANAnalytics().setChannel("Android");
        // MAN初始化方法之一，从AndroidManifest.xml中获取appKey和appSecret初始化，
        // 若您采用上述 2.3中"统一接入的方式"，则使用当前init方法即可。
        manService.getMANAnalytics().init(this, getApplicationContext());
        // 通过此接口关闭页面自动打点功能，详见文档4.2
        manService.getMANAnalytics().turnOffAutoPageTrack();
   /*     Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.READ_PHONE_STATE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        //授权成功后调用
                    }

                    @Override
                    public void onDenied(List<String> permissions) {

                    }
                });*/
    }
}
