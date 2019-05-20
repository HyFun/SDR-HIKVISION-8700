package com.app.hikvision8700;

import android.app.Application;

import com.sdr.hikvision8700.HIKVISION8700;
import com.sdr.lib.SDR;

/**
 * Created by HyFun on 2019/04/09.
 * Email: 775183940@qq.com
 * Description:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDR.register(this, new ActivityConfig(getApplicationContext()));

        HIKVISION8700.getInstance().init(this, BuildConfig.DEBUG);
    }
}
