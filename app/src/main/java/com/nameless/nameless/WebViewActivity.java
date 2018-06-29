package com.nameless.nameless;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nameless.nameless.http.NetworkUtils;
import com.nameless.nameless.http.RetrofitUtil;
import com.nameless.nameless.login.user_centre.UserCentre;

import me.leefeng.promptlibrary.PromptDialog;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private long exitTime = 0;
    private ImageView iv_main_share;
    private TextView tv_webview_exit;
    private PromptDialog promptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitUtil.getInstance().destroy();
        UserCentre.getInstance().destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
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
        return super.onKeyDown(keyCode, event);
    }

    //初始化布局
    private void initView() {
        iv_main_share = (ImageView) findViewById(R.id.iv_main_share);
        iv_main_share.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.webView);
        tv_webview_exit = (TextView) findViewById(R.id.tv_webview_exit);
        tv_webview_exit.setOnClickListener(this);
        tv_webview_exit.setVisibility(View.GONE);
        promptDialog = new PromptDialog(this);
        promptDialog.showLoading("加载中...");
    }

    //webview初始化数据   逻辑
    private void initData() {
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

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (NetworkUtils.isNetworkAvailable(WebViewActivity.this)) {
                    promptDialog.showSuccess("加载成功");
                } else {
                    promptDialog.showError("加载失败");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                promptDialog.showError("加载失败");
            }
        });
        String geturl = UserCentre.getInstance().geturl();
        webView.loadUrl(geturl);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_share:
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT,  UserCentre.getInstance().geturl());
                startActivity(Intent.createChooser(textIntent, "分享"));
                break;
            case R.id.tv_webview_exit:
                UserCentre.getInstance().clear();
                finish();
                break;
        }
    }

}
/*
    //登陆请求数据
    public void getLogin(int type) {
        String code = UserCentre.getInstance().getAccounts();
        String pwd = UserCentre.getInstance().getPwd();
        Toast.makeText(this, "账号：" + code + "密码：" + pwd, Toast.LENGTH_SHORT).show();

        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("telephone", code);
        stringMap.put("password", pwd);
        RetrofitUtil.getInstance().login(stringMap, type, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean value) {
                if (value.getStatus().getCode().equals("SUCCESS")) {
      */
/*  Intent intent = getIntent();
        String url = intent.getStringExtra("url");*//*

                    webView.loadUrl(value.getResult());
                } else {
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
    }*/
