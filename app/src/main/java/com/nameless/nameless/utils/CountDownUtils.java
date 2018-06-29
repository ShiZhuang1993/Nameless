package com.nameless.nameless.utils;

import android.widget.Button;

import com.nameless.nameless.R;
import com.nameless.nameless.init.NamelessApp;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * ===================================
 * describe:
 * date:2018/6/26
 * author:zhuang
 * ===================================
 */

public class CountDownUtils {

    public static CountDownUtils countDownUtils;

    private CountDownUtils() {
    }

    public static CountDownUtils getcountDown() {
        if (countDownUtils == null) {
            countDownUtils();
        }
        return countDownUtils;
    }

    private synchronized static void countDownUtils() {
        if (countDownUtils == null) {
            countDownUtils = new CountDownUtils();
        }
    }

    //登陆验证倒计时
    public void authCode(final Button button) {
        //请求验证码
        final long count = 30000 / 1000;
        Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (count + 1)) //设置总共发送的次数
                .map(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber
                // 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        button.setEnabled(false);//在发送数据的时候设置为不能点击

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(final Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        button.setText(value + "秒后可重发");
                        button.setTextColor(NamelessApp.getContext().getResources().getColor(R.color.colorqianhui));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        button.setText("获取验证码");
                        button.setTextColor(NamelessApp.getContext().getResources().getColor(R.color.colororange))
                        ;
                        button.setEnabled(true);
                    }
                });
    }

}
