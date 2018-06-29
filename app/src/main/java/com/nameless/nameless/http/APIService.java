package com.nameless.nameless.http;


import com.nameless.nameless.login.bean.EvaluteBean;
import com.nameless.nameless.login.bean.LoginBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 网络接口
 */

public interface APIService {
    //注册
    @FormUrlEncoded
    @POST("app/register")
    Observable<LoginBean> register(@FieldMap Map<String, String> params);

    //获取验证码
    @POST("app/sendCode")
    Observable<EvaluteBean> getSendcode(@Query("telephone") String params);

    //登陆
    @FormUrlEncoded
    @POST("app/login")
    Observable<LoginBean> login(@FieldMap Map<String, String> params, @Query("type") int type);

}
