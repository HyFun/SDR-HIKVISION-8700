package com.sdr.hikvision8700.support;

import com.sdr.hikvision8700.HIKVISION8700;
import com.sdr.hikvision8700.constant.HK8700Constant;
import com.sdr.hikvision8700.data.HK8700ItemControl;
import com.sdr.lib.rx.RxUtils;
import com.sdr.lib.support.ACache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700Util {

    private static ACache hkACache;

    public static final ACache getHkACache() {
        if (hkACache == null) {
            synchronized (HK8700Util.class) {
                if (hkACache == null) {
                    hkACache = ACache.get(HIKVISION8700.getInstance().getApplication().getExternalCacheDir());
                }
            }
        }
        return hkACache;
    }


    /**
     * 关闭所有的视频
     *
     * @param list
     * @return
     */
    public static Observable<Boolean> closeAllPlayingVideo(List<HK8700ItemControl> list) {
        final List<HK8700ItemControl> hkItemControlList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            HK8700ItemControl hkItemControl = list.get(i);
            if (hkItemControl.getCurrentStatus() != HK8700Constant.PlayStatus.LIVE_INIT) {
                // 说明正在播放
                hkItemControlList.add(hkItemControl);
            }
        }
        return Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Integer integer) throws Exception {
                        for (HK8700ItemControl item : hkItemControlList) {
                            item.stopLiveSyn();
                        }
                        return RxUtils.createData(true);
                    }
                })
                .compose(RxUtils.io_main());
    }
}
