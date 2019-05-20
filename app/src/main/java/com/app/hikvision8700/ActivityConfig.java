package com.app.hikvision8700;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.sdr.lib.base.BaseActivityConfig;

/**
 * Created by HyFun on 2018/11/23.
 * Email: 775183940@qq.com
 * Description:
 */

public class ActivityConfig extends BaseActivityConfig {

    private Context context;

    public ActivityConfig(Context context) {
        this.context = context;
    }


    @Override
    public int onHeaderBarStatusViewAlpha() {
//        return 90;
        return 0;
    }

    @Override
    public int onHeaderBarToolbarRes() {
        return R.layout.layout_public_toolbar_white;
    }

    @Override
    public Drawable onHeaderBarDrawable() {
        ColorDrawable colorDrawable = new ColorDrawable(context.getResources().getColor(R.color.colorPrimary));
        return colorDrawable;
    }

    @Override
    public int onHeaderBarTitleGravity() {
        return Gravity.CENTER;
    }
}
