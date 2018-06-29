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
