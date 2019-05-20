package com.sdr.hikvision8700;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;
import com.orhanobut.logger.Logger;
import com.sdr.hikvision8700.data.HK8700User;
import com.sdr.hikvision8700.ui.HK8700MainActivity;
import com.sdr.lib.rx.RxUtils;
import com.sdr.lib.util.AlertUtil;
import com.sdr.lib.util.ToastTopUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HIKVISION8700 {
    private static HIKVISION8700 hikvision8700;

    /**
     * HIKVISION8700 的实例
     *
     * @return
     */
    public static final HIKVISION8700 getInstance() {
        if (hikvision8700 == null) {
            synchronized (HIKVISION8700.class) {
                if (hikvision8700 == null) {
                    hikvision8700 = new HIKVISION8700();
                }
            }
        }
        return hikvision8700;
    }


    private Application application;
    private boolean debug;
    private boolean loadJNI; // 是否已经加载过库文件

    public void init(Application application, final boolean debug) {
        this.application = application;
        this.debug = debug;
    }

    public Application getApplication() {
        return application;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * 获取登录设备mac地址
     *
     * @return
     */
    public String getMacAddr() {
        WifiManager wm = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        return mac == null ? "" : mac;
    }

    /**
     * 开始启动
     *
     * @param context
     * @param url
     * @param userName
     * @param passWord
     */
    public void start(final Context context, String url, String userName, String passWord) {
        url = "http://" + url;
        HK8700User.getInstance().init(url, userName, passWord);
        if (loadJNI) {
            startToMain(context);
        } else {
            Observable.just(0)
                    .flatMap(new Function<Integer, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(Integer integer) throws Exception {
                            try {
                                // 是第一次加载，需要加载初始化
                                MCRSDK.init();
                                // 初始化RTSP
                                RtspClient.initLib();
                                MCRSDK.setPrint(1, null);
                                // 初始化语音对讲
                                TalkClientSDK.initLib();
                                // SDK初始化
                                VMSNetSDK.init(application);
                                return RxUtils.createData(true);
                            } catch (Exception e) {
                                return Observable.error(e);
                            }
                        }
                    })
                    .compose(RxUtils.io_main())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            startToMain(context);
                            loadJNI = true;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Logger.e(throwable, throwable.getMessage());
                            AlertUtil.showPositiveToastTop("海康8700视频库文件加载失败");
                        }
                    });

        }
    }


    // —————————————————————private———————————————————————————

    /**
     * 开启海康视频主页面activity
     *
     * @param context
     */
    private void startToMain(Context context) {
        Intent intent = new Intent(context, HK8700MainActivity.class);
        context.startActivity(intent);
    }

}
