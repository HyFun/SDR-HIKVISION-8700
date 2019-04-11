package com.sdr.hikvision8700.data;

import com.hikvision.sdk.net.bean.LoginData;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700User {
    private String url;
    private String userName;
    private String passWord;

    private LoginData loginData;
    public int windowIndex = 0;

    private HK8700User() {
    }

    private static HK8700User user;

    public static final HK8700User getInstance() {
        if (user == null) {
            synchronized (HK8700User.class) {
                if (user == null) {
                    user = new HK8700User();
                }
            }
        }
        return user;
    }

    public static final void destroy() {
        user = null;
    }


    public void init(String url, String userName, String passWord) {
        this.url = url;
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

}
