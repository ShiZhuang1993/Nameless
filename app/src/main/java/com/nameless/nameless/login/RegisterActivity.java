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
import com.nameless.nameless.utils.RegexUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * ===================================
 * describe:注册页面
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
    private CheckBox cb_login_commitid;
    private CheckBox cb_login_commitlogin;
    private EditText et_register_idcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    //初始化布局
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
        et_register_idcard = (EditText) findViewById(R.id.et_register_idcard);
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
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    //初始化必填项
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
        String idcode = et_register_idcard.getText().toString().trim();
        boolean byteIdCard = RegexUtils.ByteIdCard(idcode);
        if (TextUtils.isEmpty(idcode)) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!byteIdCard) {
            Toast.makeText(this, "请输入正确得身份证号码", Toast.LENGTH_SHORT).show();
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
                if (stringCode.equals(MyConfig.SUCCESS)) {
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
        //手机唯一标识
        String deviceId = MobileIdentification.getIMEI(this);
        //随机数生成
        Random rand = new Random();
        int nonce = rand.nextInt();
        //获取当前时间戳
        DateUtils date = new DateUtils();
        String timestamp = date.getTime();
        //token MD5加密生成
        String accessToken = MD5Pwd.stringToMD5(timestamp + nonce + deviceId + MyConfig.SLEEPWALKER);

        String code = et_register_code.getText().toString().trim();
        String authcode = et_register_authcode.getText().toString().trim();
        String pwsd = et_register_pwsd.getText().toString().trim();
        String name = et_register_name.getText().toString().trim();
        String invitationcode = et_register_invitationcode.getText().toString().trim();
        String idcode = et_register_idcard.getText().toString().trim();//获取身份证号码   todo

        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("deviceId", deviceId);
        stringMap.put("nonce", nonce + "");
        stringMap.put("timestamp", timestamp);
        stringMap.put("accessToken", accessToken);
        stringMap.put("telephone", code);
        stringMap.put("code", authcode);
        stringMap.put("password", pwsd);
        stringMap.put("username", name);
        stringMap.put("idCard",idcode);
        stringMap.put("activeCode", invitationcode);
        RetrofitUtil.getInstance().register(stringMap, new Observer<LoginBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginBean value) {
                String stringCode = value.getStatus().getCode();
                if (stringCode.equals(MyConfig.SUCCESS)) {
                    //登陆成功后保存状态
                    boolean remember_accounts = cb_login_commitid.isChecked();
                    boolean auto_login = cb_login_commitlogin.isChecked();
                    UserCentre.getInstance().setRememberAccounts(remember_accounts);
                    UserCentre.getInstance().setAutoLogin(auto_login);
                    //阿里云注册统计
                    MANService manService = MANServiceProvider.getService();
                    // 注册用户埋点
                    manService.getMANAnalytics().userRegister(et_register_code.getText().toString().trim());
                    //保存账号。密码。
                    UserCentre.getInstance().setUserAccounts(et_register_code.getText().toString().trim());
                    UserCentre.getInstance().setUserPwd(et_register_pwsd.getText().toString().trim());
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

    //手机实体键 返回键得操作
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
