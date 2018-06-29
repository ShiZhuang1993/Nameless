package com.nameless.nameless.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nameless.nameless.R;
import com.nameless.nameless.WebViewActivity;
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
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView iv_register_back;
    private EditText et_register_code;
    private EditText et_register_authcode;
    private Button bt_register_authcode;
    private EditText et_register_pwsd;
    private EditText et_register_name;
    private EditText et_register_invitationcode;
    private Button bt_register;
    //判断成功失败的标识
    private String SUCCESS = "SUCCESS";
    private CheckBox cb_login_commitid;
    private CheckBox cb_login_commitlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        iv_register_back = (ImageView) findViewById(R.id.iv_register_back);
        iv_register_back.setOnClickListener(this);
        et_register_code = (EditText) findViewById(R.id.et_register_code);
        et_register_authcode = (EditText) findViewById(R.id.et_register_authcode);
        bt_register_authcode = (Button) findViewById(R.id.bt_register_authcode);
        et_register_pwsd = (EditText) findViewById(R.id.et_register_pwsd);
        et_register_name = (EditText) findViewById(R.id.et_register_name);
        et_register_invitationcode = (EditText) findViewById(R.id.et_register_invitationcode);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_register_authcode.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        cb_login_commitid = (CheckBox) findViewById(R.id.cb_login_commitid);
        cb_login_commitid.setOnClickListener(this);
        cb_login_commitlogin = (CheckBox) findViewById(R.id.cb_login_commitlogin);
        cb_login_commitlogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                submit();
                break;
            case R.id.bt_register_authcode:
                if (TextUtils.isEmpty(et_register_code.getText().toString().trim())) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                CountDownUtils.getcountDown().authCode(bt_register_authcode);
                //获取验证码
                getSendcode();
                break;
            case R.id.iv_register_back:
                finish();
                break;
        }
    }

    private void submit() {
        // validate
        String code = et_register_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        String authcode = et_register_authcode.getText().toString().trim();
        if (TextUtils.isEmpty(authcode)) {
            Toast.makeText(this, "请输手机验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        String pwsd = et_register_pwsd.getText().toString().trim();
        if (TextUtils.isEmpty(pwsd)) {
            Toast.makeText(this, "请输6-20位数字或字母", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = et_register_name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "优先输入新姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        String invitationcode = et_register_invitationcode.getText().toString().trim();
        if (TextUtils.isEmpty(invitationcode)) {
            Toast.makeText(this, "请向您师傅索要", Toast.LENGTH_SHORT).show();
            return;
        }

        //注册
        getRegister();


    }

    //请求短信
    private void getSendcode() {
        RetrofitUtil.getInstance().getSendcode(et_register_code.getText().toString().trim(), new Observer<EvaluteBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(EvaluteBean value) {
                String stringCode = value.getStatus().getCode();
                if (stringCode.equals(SUCCESS)) {
                    Toast.makeText(RegisterActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //请求数据
    private void getRegister() {
        String code = et_register_code.getText().toString().trim();
        String authcode = et_register_authcode.getText().toString().trim();
        String pwsd = et_register_pwsd.getText().toString().trim();
        String name = et_register_name.getText().toString().trim();
        String invitationcode = et_register_invitationcode.getText().toString().trim();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("telephone", code);
        stringMap.put("code", authcode);
        stringMap.put("password", pwsd);
        stringMap.put("username", name);
        stringMap.put("activeCode", invitationcode);
        RetrofitUtil.getInstance().register(stringMap, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean value) {
                String stringCode = value.getStatus().getCode();
                if (stringCode.equals(SUCCESS)) {
                    boolean remember_accounts = cb_login_commitid.isChecked();
                    boolean auto_login = cb_login_commitlogin.isChecked();
                    UserCentre.getInstance().setRememberAccounts(remember_accounts);
                    UserCentre.getInstance().setAutoLogin(auto_login);
                    if (!remember_accounts) {
                        UserCentre.getInstance().setUserAccounts(et_register_code.getText().toString().trim());
                        UserCentre.getInstance().setUserPwd(et_register_pwsd.getText().toString().trim());
                    } else {
                        UserCentre.getInstance().clearAccounts();
                        UserCentre.getInstance().clearPwd();
                    }
                    //登录点击跳转逻辑在此
                    Intent intent = new Intent(RegisterActivity.this, WebViewActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, value.getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
