package com.nameless.nameless.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.nameless.nameless.R;
import com.nameless.nameless.WebViewActivity;
import com.nameless.nameless.http.RetrofitUtil;
import com.nameless.nameless.login.bean.EvaluteBean;
import com.nameless.nameless.login.bean.LoginBean;
import com.nameless.nameless.login.user_centre.MyConfig;
import com.nameless.nameless.login.user_centre.UserCentre;
import com.nameless.nameless.utils.CountDownUtils;
import com.nameless.nameless.utils.DateUtils;
import com.nameless.nameless.utils.MD5Pwd;
import com.nameless.nameless.utils.MobileIdentification;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * ===================================
 * describe:登录页面
 * date:2018/6/25
 * author:zhuang
 * ===================================
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_login_code;
    private EditText et_login_pwsd;
    private Button bt_login;
    private TextView te_register;
    private CheckBox cb_login_commitid;
    private CheckBox cb_login_commitlogin;
    private RadioButton rb_01;
    private RadioButton rb_02;
    private RelativeLayout rl_01;
    private RelativeLayout rl_02;
    private RadioGroup rg_login;
    private LinearLayout ll_login_authcode;
    private LinearLayout ll_login_pwsd;
    private Button bt_login_authcode;
    private EditText et_login_authcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
    }

    //初始化布局
    private void initView() {
        //头部布局
        rg_login = (RadioGroup) findViewById(R.id.rg_login);
        rg_login.setOnClickListener(this);
        rb_01 = (RadioButton) findViewById(R.id.rb_01);
        rb_01.setOnClickListener(this);
        rb_02 = (RadioButton) findViewById(R.id.rb_02);
        rb_02.setOnClickListener(this);
        rl_01 = (RelativeLayout) findViewById(R.id.rl_01);
        rl_01.setVisibility(View.VISIBLE);
        rl_02 = (RelativeLayout) findViewById(R.id.rl_02);
        ll_login_authcode = (LinearLayout) findViewById(R.id.ll_login_authcode);
        ll_login_pwsd = (LinearLayout) findViewById(R.id.ll_login_pwsd);
        ll_login_pwsd.setVisibility(View.VISIBLE);

        //账号密码布局
        cb_login_commitid = (CheckBox) findViewById(R.id.cb_login_commitid);
        cb_login_commitlogin = (CheckBox) findViewById(R.id.cb_login_commitlogin);
        et_login_code = (EditText) findViewById(R.id.et_login_code);
        et_login_pwsd = (EditText) findViewById(R.id.et_login_pwsd);
        et_login_authcode = (EditText) findViewById(R.id.et_login_authcode);
        bt_login = (Button) findViewById(R.id.bt_login);
        te_register = (TextView) findViewById(R.id.te_register);
        te_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        bt_login_authcode = (Button) findViewById(R.id.bt_login_authcode);
        bt_login_authcode.setOnClickListener(this);
        //读取状态
        et_login_code.setText(UserCentre.getInstance().getAccounts());
        cb_login_commitid.setChecked(UserCentre.getInstance().getRememberAccounts());
        et_login_pwsd.setText(UserCentre.getInstance().getPwd());
        //头部选择监听
        rg_login.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_01:
                        rl_01.setVisibility(View.VISIBLE);
                        rl_02.setVisibility(View.GONE);
                        ll_login_pwsd.setVisibility(View.VISIBLE);
                        ll_login_authcode.setVisibility(View.GONE);
                        et_login_code.setText("");
                        et_login_pwsd.setText("");
                        bt_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitOne();
                            }
                        });
                        break;
                    case R.id.rb_02:
                        rl_01.setVisibility(View.GONE);
                        rl_02.setVisibility(View.VISIBLE);
                        ll_login_pwsd.setVisibility(View.GONE);
                        ll_login_authcode.setVisibility(View.VISIBLE);
                        et_login_code.setText("");
                        et_login_authcode.setText("");
                        bt_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitTwo();
                            }
                        });
                        break;
                }
            }
        });
    }

    //初始化数据
    private void initData() {
        //判断本地存储得密码是否为null,为null时，当退出应用再次进入时展示登录页面
        String pwd = UserCentre.getInstance().getPwd();
        boolean autoLogin = UserCentre.getInstance().getAutoLogin();
        if (pwd != null && !pwd.equals("")) {
            if (autoLogin) {
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            et_login_code.setText("");
        }
    }

    //账号 验证码  必填项判断
    private void submitTwo() {
        // validate
        String code = et_login_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String authcode = et_login_authcode.getText().toString().trim();
        if (TextUtils.isEmpty(authcode)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        getLogin(0, authcode);
    }    //账号密码 必填项判断

    private void submitOne() {
        // validate
        String code = et_login_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String pwsd = et_login_pwsd.getText().toString().trim();
        if (TextUtils.isEmpty(pwsd)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        getLogin(1, pwsd);


    }


    //登陆请求数据
    public void getLogin(final int type, final String pwd) {
        //手机唯一标识
        String deviceId = MobileIdentification.getIMEI(this);
        if (deviceId == null) {
            getDialog("您拒绝权限申请，此功能将不能正常使用，您可以去设置页面重新授权").show();
            return;
        }
        //随机数生成
        Random rand = new Random();
        int nonce = rand.nextInt(100);
        //获取当前时间戳
        DateUtils date = new DateUtils();
        String timestamp = date.getTime();
        //token MD5加密生成
        String accessToken = MD5Pwd.stringToMD5(timestamp + nonce + deviceId + MyConfig.SLEEPWALKER);

        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("deviceId", deviceId);
        stringMap.put("nonce", nonce + "");
        stringMap.put("timestamp", timestamp);
        stringMap.put("accessToken", accessToken);
        stringMap.put("telephone", et_login_code.getText().toString().trim());
        stringMap.put("password", pwd);
        RetrofitUtil.getInstance().login(stringMap, type, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean value) {
                if (value.getStatus().getCode().equals(MyConfig.SUCCESS)) {
                    //登陆成功后保存状态
                    boolean remember_accounts = cb_login_commitid.isChecked();
                    boolean auto_login = cb_login_commitlogin.isChecked();
                    UserCentre.getInstance().setRememberAccounts(remember_accounts);
                    UserCentre.getInstance().setAutoLogin(auto_login);
                    //阿里云统计
                    MANService manService = MANServiceProvider.getService();
                    // 用户登录埋点
                    manService.getMANAnalytics().updateUserAccount("用戶--" + type + "--", et_login_code.getText().toString().trim());
                    //判断状态
                    if (!remember_accounts) {
                        //保存账号。密码。
                        UserCentre.getInstance().setUserAccounts(et_login_code.getText().toString().trim());
                        UserCentre.getInstance().setUserPwd(et_login_pwsd.getText().toString().trim());
                    }

                    if (type == 0) {
                        //登录点击跳转逻辑在此
                        Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                        intent.putExtra("type", type + "");
                        intent.putExtra("url", value.getResult());
                        startActivity(intent);
                        finish();
                    } else {
                        //登录点击跳转逻辑在此
                        Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                        intent.putExtra("type", type + "");
                        startActivity(intent);
                        finish();
                    }
                } else {
                    UserCentre.getInstance().clearPwd();
                    Toast.makeText(LoginActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                submitOne();
                break;
            case R.id.te_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_login_authcode:
                String code = et_login_code.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                CountDownUtils.getcountDown().authCode(bt_login_authcode);
                getSendcode();
                break;
        }
    }


    //请求短信
    private void getSendcode() {
        RetrofitUtil.getInstance().getSendcode(et_login_code.getText().toString().trim(), new Observer<EvaluteBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(EvaluteBean value) {
                String stringCode = value.getStatus().getCode();
                if (stringCode.equals(MyConfig.SUCCESS)) {
                } else {
                    Toast.makeText(LoginActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //对话框
    private AlertDialog.Builder getDialog(String message) {
        AlertDialog.Builder s = new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("设置权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return s;
    }
}
