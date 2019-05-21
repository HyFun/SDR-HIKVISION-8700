package com.app.hikvision8700;

import android.app.Application;

import com.sdr.hikvision8700.SDR_HIKVISION_8700_HTTPS;
import com.sdr.lib.SDR_LIBRARY;

/**
 * Created by HyFun on 2019/04/09.
 * Email: 775183940@qq.com
 * Description:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SDR_LIBRARY.register(this, new ActivityConfig(getApplicationContext()));
        SDR_HIKVISION_8700_HTTPS.getInstance().init(this, BuildConfig.DEBUG);
    }
}
