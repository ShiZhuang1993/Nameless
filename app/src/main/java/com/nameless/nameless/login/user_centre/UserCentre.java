package com.nameless.nameless.login.user_centre;


import com.nameless.nameless.utils.SharedPreferencesUtils;


/**
 * ===================================
 * describe:
 * date:2018/6/25
 * author:zhuang
 * ===================================
 */

public class UserCentre {

    private static UserCentre instance;
    private static User mUser;

    private UserCentre() {
    }

    public static UserCentre getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new UserCentre();
            mUser = new User();
            String accounts = (String) SharedPreferencesUtils.getParam(MyConfig.ACCOUNTS, null);
            String pwd = (String) SharedPreferencesUtils.getParam(MyConfig.PWD, null);
            boolean auto_login = (boolean) SharedPreferencesUtils.getParam(MyConfig.AUTO_LOGIN,
                    false);
            boolean remember_accounts = (boolean) SharedPreferencesUtils.getParam(MyConfig
                    .REMEMBER_ACCOUNTS, false);
            String url = (String) SharedPreferencesUtils.getParam(MyConfig.URL, null);
            mUser.setAccounts(accounts);
            mUser.setPwd(pwd);
            mUser.setAutoLogin(auto_login);
            mUser.setRememberAccounts(remember_accounts);
            mUser.setUrl(url);
        }
    }

    //设置帐号
    public void setUserAccounts(String accounts) {
        SharedPreferencesUtils.setParam(MyConfig.ACCOUNTS, accounts);
        mUser.setAccounts(accounts);
    }

    //设置密码
    public void setUserPwd(String pwd) {
        SharedPreferencesUtils.setParam(MyConfig.PWD, pwd);
        mUser.setPwd(pwd);
    }

    //设置是否自动登录
    public void setAutoLogin(boolean login) {
        SharedPreferencesUtils.setParam(MyConfig.AUTO_LOGIN, login);
        mUser.setAutoLogin(login);
    }

    //设置是否记住帐号
    public void setRememberAccounts(boolean rememberAccounts) {
        SharedPreferencesUtils.setParam(MyConfig.REMEMBER_ACCOUNTS, rememberAccounts);
        mUser.setRememberAccounts(rememberAccounts);
    }

    //设置URL
    public void setUrl(String url) {
        SharedPreferencesUtils.setParam(MyConfig.URL, url);
        mUser.setUrl(url);
    }
    //获取帐号
    public String getAccounts() {
        return mUser.getAccounts();
    }

    //获取密码
    public String getPwd() {
        return mUser.getPwd();
    }


    //获取自动登录状态
    public boolean getAutoLogin() {
        return mUser.isAutoLogin();
    }

    //获取记住密码状态
    public boolean getRememberAccounts() {
        return mUser.isRememberAccounts();
    }
    //获取url
    public String geturl() {
        return mUser.getUrl();
    }
    //清除所有信息 （退出登录用）
    public void clear() {
        setUserAccounts(null);
        setUserPwd(null);
        setAutoLogin(false);
        setRememberAccounts(false);
        setUrl(null);
    }

    //清除帐号
    public void clearAccounts() {
        setUserAccounts(null);
    }
    //清除密码
    public void clearPwd() {
        setUserPwd(null);
    }

    //释放资源
    public void destroy() {
        if (!mUser.isAutoLogin()) {
            setUserAccounts(null);
        }
    }

}
