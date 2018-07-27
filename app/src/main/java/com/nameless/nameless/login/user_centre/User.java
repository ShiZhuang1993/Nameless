package com.nameless.nameless.login.user_centre;

import java.io.Serializable;

/**
 * ===================================
 * describe:
 * date:2018/6/25
 * author:zhuang
 * ===================================
 */

public class User implements Serializable {

    private String accounts; //帐号
    private String pwd; //密码
    private boolean rememberAccounts;
    private boolean autoLogin;
    private String url;
    private String verificationcode;
    private String type;

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVerificationcode() {
        return verificationcode == null ? "" : verificationcode;
    }

    public void setVerificationcode(String verificationcode) {
        this.verificationcode = verificationcode;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User() {
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isRememberAccounts() {
        return rememberAccounts;
    }

    public void setRememberAccounts(boolean rememberAccounts) {
        this.rememberAccounts = rememberAccounts;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }
}
