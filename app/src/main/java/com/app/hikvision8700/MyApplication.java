package com.app.hikvision8700;

import android.app.Application;
import android.graphics.drawable.ColorDrawable;

import com.sdr.hikvision8700.HIKVISION8700;
import com.sdr.lib.SDRLibrary;

/**
 * Created by HyFun on 2019/04/09.
 * Email: 775183940@qq.com
 * Description:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDRLibrary.getInstance().init(this, BuildConfig.DEBUG);

        HIKVISION8700.getInstance().init(this, BuildConfig.DEBUG, new ColorDrawable(getResources().getColor(R.color.colorPrimary)), R.layout.layout_public_toolbar_white);
    }
}
