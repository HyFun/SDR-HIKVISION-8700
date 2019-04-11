package com.sdr.hikvision8700.contract;

import android.view.SurfaceView;

/**
 * Created by HyFun on 2018/11/13.
 * Email: 775183940@qq.com
 * Description:
 */

public interface HK8700PlayContract {
    interface View {
        //        void playLiveFailed(int position,String message);
//
//        void stopPlayComplete(int position,String message);
//
        void showLoadingDialog(String message);

        void hideLoadingDialog();


        void onPlayMsg(int position, int code, String msg);

    }

    interface Presenter {
        void startPlay(String cameraID, SurfaceView surfaceView);

        void stopPlay();

        void stopPlaySyn();

        void sendCtrlCmd(int gestureID);

        void stopControl();
    }
}
