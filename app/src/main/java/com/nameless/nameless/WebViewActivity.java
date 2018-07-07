package com.nameless.nameless;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.nameless.nameless.http.NetworkUtils;
import com.nameless.nameless.http.RetrofitUtil;
import com.nameless.nameless.login.bean.LoginBean;
import com.nameless.nameless.login.user_centre.MyConfig;
import com.nameless.nameless.login.user_centre.UserCentre;
import com.nameless.nameless.utils.DateUtils;
import com.nameless.nameless.utils.MD5Pwd;
import com.nameless.nameless.utils.MobileIdentification;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * ===================================
 * describe:主页面
 * date:2018/6/25
 * author:zhuang
 * ===================================
 */
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private long exitTime = 0;
    private ImageView iv_main_share;
    private TextView tv_webview_exit;
    private PromptDialog promptDialog;
    private TextView tv_webview_title;
    private String urls;
    private String backurl;
    private MANService manService;
    private FrameLayout video_view;
    private MyWebChromeClient mMyWebChromeClient;
    private RelativeLayout rll;
    private String title =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    //初始化布局
    private void initView() {
        rll = (RelativeLayout) findViewById(R.id.rll);
        iv_main_share = (ImageView) findViewById(R.id.iv_main_share);
        iv_main_share.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        tv_webview_exit = (TextView) findViewById(R.id.tv_webview_exit);
        tv_webview_exit.setOnClickListener(this);
        tv_webview_exit.setVisibility(View.GONE);
        promptDialog = new PromptDialog(this);
        promptDialog.showLoading("加载中...");
        tv_webview_title = (TextView) findViewById(R.id.tv_webview_title);
        video_view = (FrameLayout) findViewById(R.id.video_view);
        //实例化阿里云对象
        manService = MANServiceProvider.getService();
        WebSettings settings = webView.getSettings();
        // 允许javascript的执行
        settings.setJavaScriptEnabled(true);
        //缩放
        settings.setBuiltInZoomControls(false);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        //可以访问的文件
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    //webview初始化数据   逻辑
    private void initData() {
        String types = getIntent().getStringExtra("type");
        Log.e("----type--------", types + "");
        if (types != null && !types.equals("")) {
            if (types.equals("0")) {
                String url = getIntent().getStringExtra("url");
                webView.loadUrl(url);
            } else {
                getLogin(1);
            }

        } else {
            getLogin(1);

        }
        //捕捉 html中点击过的链接
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                tv_webview_title.setText(view.getTitle());
                title=view.getTitle();
                if (NetworkUtils.isNetworkAvailable(WebViewActivity.this)) {
                    promptDialog.showSuccess("加载成功");
                } else {
                    promptDialog.showError("加载失败");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                promptDialog.showError("加载失败");
            }

            //捕捉 html中点击过的链接
            @Override
            public void doUpdateVisitedHistory(WebView view, final String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                Log.e("-----url__titlt----", view.getTitle() + "---" + view.getOriginalUrl());
                backurl = url;
                if (url.indexOf("detail") != -1) {
                    iv_main_share.setVisibility(View.VISIBLE);
                    iv_main_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT, url);
                            //todo 加图标和标题
                            textIntent.putExtra(Intent.EXTRA_TITLE,title);
                            textIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                                    Intent.ShortcutIconResource.fromContext(WebViewActivity.this,
                                    R.mipmap.icon_s) );
                            startActivity(Intent.createChooser(textIntent, "分享"));

                        }
                    });

                    return;
                } else {
                    iv_main_share.setVisibility(View.GONE);
                }
                if (url.indexOf("share") != -1) {
                    iv_main_share.setVisibility(View.VISIBLE);
                    iv_main_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT, urls);
                            //todo 加图标和标题
                            textIntent.putExtra(Intent.EXTRA_TITLE,title);
                            textIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                                    Intent.ShortcutIconResource.fromContext(WebViewActivity.this,
                                            R.mipmap.icon_s) );
                            startActivity(Intent.createChooser(textIntent, "分享"));
                        }
                    });

                    return;
                } else {
                    iv_main_share.setVisibility(View.GONE);
                }
            }
        });
        //修改html中弹出对话框的内容
        mMyWebChromeClient = new MyWebChromeClient();
        webView.setWebChromeClient(mMyWebChromeClient);
    }

    //登陆请求数据
    public void getLogin(int type) {
        //手机唯一标识
        String deviceId = MobileIdentification.getIMEI(this);
        //随机数生成
        Random rand = new Random();
        int nonce = rand.nextInt(100);
        //获取当前时间戳
        DateUtils date = new DateUtils();
        String timestamp = date.getTime();
        //token MD5加密生成
        String accessToken = MD5Pwd.stringToMD5(timestamp + nonce + deviceId + MyConfig.SLEEPWALKER);
        String code = UserCentre.getInstance().getAccounts();
        String pwd = UserCentre.getInstance().getPwd();
        Log.e("----------所有数据---------", "devucrid---" + deviceId + "---随机数---" + nonce + "---时间戳---" + timestamp
                + "---MD5---" + accessToken + "---账号---" + code + "---密码---" + pwd);
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("deviceId", deviceId);
        stringMap.put("nonce", nonce + "");
        stringMap.put("timestamp", timestamp);
        stringMap.put("accessToken", accessToken);
        stringMap.put("telephone", code);
        stringMap.put("password", pwd);
        RetrofitUtil.getInstance().login(stringMap, type, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(LoginBean value) {
                if (value.getStatus().getCode().equals(MyConfig.SUCCESS)) {
                    webView.loadUrl(value.getResult());
                    urls = value.getShare_url();
                } else {
                    promptDialog.showError("加载失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(WebViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RetrofitUtil.getInstance().destroy();
        UserCentre.getInstance().destroy();
        webView.destroy();
    }

    //对话框
    private AlertDialog.Builder getDialog(String message, final JsResult result) {
        AlertDialog.Builder s = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });

        return s;
    }

    //返回键操作
    @Override
    public void onBackPressed() {
        if (backurl != null) {
            if (backurl.indexOf("study") != -1 | backurl.indexOf("my") != -1) {
                // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    // 将系统当前的时间赋值给exitTime
                    exitTime = System.currentTimeMillis();
                    return;
                } else {
                    finish();
                }
            }
        } else {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
                return;
            } else {
                finish();
            }
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        //阿里云手动埋点
        manService.getMANPageHitHelper().pageDisAppear(this);
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        manService.getMANPageHitHelper().pageAppear(this);
        webView.onResume();
    }

    //自定义webview视频全屏  弹窗
    private class MyWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            video_view.addView(mCustomView);
            mCustomViewCallback = callback;
            webView.setVisibility(View.GONE);
            rll.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        public void onHideCustomView() {
            webView.setVisibility(View.VISIBLE);
            rll.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);

            video_view.removeView(mCustomView);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onHideCustomView();
        }

        //自定义弹窗
        @Override
        public boolean onJsConfirm(WebView view, String url, final String message, final JsResult result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getDialog(message, result).show().setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });
                }
            });
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_webview_exit://退出按钮 目前隐藏  todo
                UserCentre.getInstance().clear();
                //阿里云统计
                MANService manService = MANServiceProvider.getService();
                // 用户注销埋点
                manService.getMANAnalytics().updateUserAccount("", "");
                finish();
                break;
        }
    }
}
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (backurl != null) {
            if (backurl.indexOf("study") != -1 | backurl.indexOf("my") != -1) {
                // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    // 将系统当前的时间赋值给exitTime
                    exitTime = System.currentTimeMillis();
                    return true;
                } else {
                    finish();
                }
            }
        } else {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/