package com.nameless.nameless.http;


import com.nameless.nameless.http.cookie.CookiesManager;
import com.nameless.nameless.init.NamelessApp;
import com.nameless.nameless.login.bean.EvaluteBean;
import com.nameless.nameless.login.bean.LoginBean;
import com.nameless.nameless.url.NamelessUrl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * retrofit工具类
 */

public class RetrofitUtil {
    public static final int DEFAULT_TIMEOUT = 10;
    private Retrofit mRetrofit;
    private APIService mApiService;
    private static RetrofitUtil mInstance;

    /**
     * 私有构造方法
     * private RetrofitUtil() {
     */
    private RetrofitUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new CookiesManager(NamelessApp.getContext()))
                .cache(new Cache(new File(NamelessApp.getContext().getExternalCacheDir(), "kangso_cache"), 5 * 2024 * 1024))
                .addInterceptor(cacheNetInterceptor)
                .addNetworkInterceptor(cacheNetInterceptor)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(NamelessUrl.NAMELESS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mApiService = mRetrofit.create(APIService.class);
    }

    public static RetrofitUtil getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                mInstance = new RetrofitUtil();
            }
        }
        return mInstance;
    }

    /**
     * Okhttp拦截器
     */
    private Interceptor cacheNetInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (NetworkUtils.isNetworkAvailable(NamelessApp.getContext())) {
                Response response = chain.proceed(request);
                // read from cache for 0 ratingbar_view  有网络不会使用缓存数据
                int maxAge = 10;
                String cacheControl = request.cacheControl().toString();
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                //无网络时强制使用缓存数据
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Response response = chain.proceed(request);
                int maxStale = 60 * 60 * 24 * 3;
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }

    };



    //注册
    public void register(Map<String, String> map, Observer<LoginBean> observer) {
        getInstance().mApiService.register(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    //登陆
    public void login(Map<String, String> map,int type, Observer<LoginBean> observer) {
        getInstance().mApiService.login(map,type).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
       //获取验证码
       public void getSendcode(String id, Observer<EvaluteBean> observer) {
           getInstance().mApiService.getSendcode(id).subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(observer);
       }
    //释放资源
    public void destroy() {
        mApiService = null;
        mInstance = null;
        mRetrofit = null;
    }
}