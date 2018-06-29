package com.nameless.nameless.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nameless.nameless.WebViewActivity;
import com.nameless.nameless.R;
import com.nameless.nameless.http.RetrofitUtil;
import com.nameless.nameless.login.bean.EvaluteBean;
import com.nameless.nameless.login.bean.LoginBean;
import com.nameless.nameless.login.user_centre.UserCentre;
import com.nameless.nameless.utils.CountDownUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * ===================================
 * describe:
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
    //判断成功失败的标识
    private String SUCCESS = "SUCCESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

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

    private void initData() {
        boolean autoLogin = UserCentre.getInstance().getAutoLogin();
        if (autoLogin) {
            Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    //登陆请求数据
    public void getLogin( int type) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("telephone", et_login_code.getText().toString().trim());
        stringMap.put("password", et_login_pwsd.getText().toString().trim());
        RetrofitUtil.getInstance().login(stringMap, type, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean value) {
                if (value.getStatus().getCode().equals(SUCCESS)) {
                    boolean remember_accounts = cb_login_commitid.isChecked();
                    boolean auto_login = cb_login_commitlogin.isChecked();
                    UserCentre.getInstance().setRememberAccounts(remember_accounts);
                    UserCentre.getInstance().setAutoLogin(auto_login);
                    if (!remember_accounts) {
                        UserCentre.getInstance().setUserAccounts(et_login_code.getText().toString().trim());
                        UserCentre.getInstance().setUserPwd(et_login_pwsd.getText().toString().trim());
                        UserCentre.getInstance().setUrl(value.getResult());
                    } else {
                        UserCentre.getInstance().clearAccounts();
                        UserCentre.getInstance().clearPwd();
                    }
                    //登录点击跳转逻辑在此
                    Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                    startActivity(intent);
                    finish();

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
        getLogin(1);


    }

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
        getLogin( 0);
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
                if (stringCode.equals(SUCCESS)) {
                    Toast.makeText(LoginActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
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

}
