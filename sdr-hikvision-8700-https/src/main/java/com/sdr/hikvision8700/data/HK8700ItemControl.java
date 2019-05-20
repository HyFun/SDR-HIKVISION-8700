package com.sdr.hikvision8700.data;

import android.view.SurfaceView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.sdr.hikvision8700.constant.HK8700Constant;
import com.sdr.hikvision8700.contract.HK8700PlayContract;
import com.sdr.lib.rx.RxUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700ItemControl {
    private int position;
    private HK8700PlayContract.View view;
    private int windowIndex;
    private SubResourceNodeBean cameraInfo;

    private int currentStatus = HK8700Constant.PlayStatus.LIVE_INIT;

    private int currentCommand = -1; // 当前指令

    public HK8700ItemControl(int index, HK8700PlayContract.View view) {
        this.position = index;
        this.view = view;
    }


    /**
     * 是否正在录像
     */
    private boolean mIsRecord = false;


    private boolean mIsAudio = false;
    /**
     * 抓图文件
     */
    private File mPictureFile = null;
    /**
     * 录像文件
     */
    private File mRecordFile = null;


    public void startLive(SurfaceView surfaceView, SubResourceNodeBean camera) {
        if (currentStatus != HK8700Constant.PlayStatus.LIVE_INIT) return;
        view.showLoadingDialog("正在加载");
        windowIndex = ++HK8700User.getInstance().windowIndex;
        cameraInfo = camera;
        VMSNetSDK.getInstance().startLiveOpt(windowIndex, camera.getSysCode(), surfaceView, SDKConstant.LiveSDKConstant.MAIN_HIGH_STREAM, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                view.hideLoadingDialog();
                view.onPlayMsg(position, HK8700Constant.PlayLive.PLAY_LIVE_FAILED, "播放失败");
            }

            @Override
            public void onSuccess(Object o) {
                view.hideLoadingDialog();
                view.onPlayMsg(position, HK8700Constant.PlayLive.PLAY_LIVE_SUCCESS, "播放成功");
                currentStatus = HK8700Constant.PlayStatus.LIVE_PLAY;
            }
        });
    }

    /**
     * 停止预览   异步
     */
    public void stopLive() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT) return;
        view.showLoadingDialog("正在停止");
        //停止预览按钮点击操作
        Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Integer integer) throws Exception {
                        boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(windowIndex);
                        if (mIsRecord) {
                            stopRecord();
                        }
                        if (mIsAudio) {
                            stopAudio();
                        }

                        return RxUtils.createData(stopLiveResult);
                    }
                })
                .compose(RxUtils.io_main())
                .subscribe(result -> {
                    view.hideLoadingDialog();
                    if (result) {
                        view.onPlayMsg(position, HK8700Constant.PlayLive.PLAY_LIVE_STOP_SUCCESS, "停止成功");
                        currentStatus = HK8700Constant.PlayStatus.LIVE_INIT;
                        HK8700User.getInstance().windowIndex--;
                    }
                });
    }


    /**
     * 同步停止
     */
    public void stopLiveSyn() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT) return;
        boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(windowIndex);
        if (mIsRecord) {
            stopRecord();
        }
        if (mIsAudio) {
            stopAudio();
        }

        if (stopLiveResult) {
            view.onPlayMsg(position, HK8700Constant.PlayLive.PLAY_LIVE_STOP_SUCCESS, "停止成功");
            currentStatus = HK8700Constant.PlayStatus.LIVE_INIT;
            HK8700User.getInstance().windowIndex--;
        }
    }

    /**
     * 截图拍照
     */
    public void capture(String path, String fileName) {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.CAPTURE_FAILED, "视频没有正在播放，操作失败");
            return;
        }


        int opt = VMSNetSDK.getInstance().captureLiveOpt(windowIndex, path, fileName);
        if (opt == SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS) {
            mPictureFile = new File(path, fileName);
            view.onPlayMsg(position, HK8700Constant.PlayLive.CAPTURE_SUCCESS, mPictureFile.getAbsolutePath());
        } else {
            mPictureFile = null;
            view.onPlayMsg(position, HK8700Constant.PlayLive.CAPTURE_FAILED, "抓图失败");
        }
    }


    /**
     * 开始录像
     */
    public void startRecord(String path, String fileName) {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.RECORD_FAILED, "视频没有正在播放，操作失败");
            return;
        }

        int recordOpt = VMSNetSDK.getInstance().startLiveRecordOpt(windowIndex, path, fileName);
        if (recordOpt == SDKConstant.LiveSDKConstant.RECORD_SUCCESS) {
            mRecordFile = new File(path, fileName);
            mIsRecord = true;
            view.onPlayMsg(position, HK8700Constant.PlayLive.RECORD_START, "开始录像");
        } else {
            view.onPlayMsg(position, HK8700Constant.PlayLive.RECORD_FAILED, "启动录像失败");
            mRecordFile = null;
            mIsRecord = false;
        }
    }

    /**
     * 停止录像
     */
    public void stopRecord() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.RECORD_FAILED, "视频没有正在播放，操作失败");
            return;
        }


        VMSNetSDK.getInstance().stopLiveRecordOpt(windowIndex);
        if (mRecordFile != null) {
            view.onPlayMsg(position, HK8700Constant.PlayLive.RECORD_SUCCESS, mRecordFile.getAbsolutePath());
        }
        mIsRecord = false;
    }

    /**
     * 开启音频
     */
    public void startAudio() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.AUDIO_FAILED, "视频没有正在播放，无法开启音频");
            return;
        }

        boolean ret = VMSNetSDK.getInstance().startLiveAudioOpt(windowIndex);
        if (ret) {
            mIsAudio = true;
            view.onPlayMsg(position, HK8700Constant.PlayLive.AUDIO_SUCCESS, "音频播放成功");
        } else {
            view.onPlayMsg(position, HK8700Constant.PlayLive.AUDIO_FAILED, "音频播放失败");
        }
    }

    /**
     * 关闭音频
     */
    public void stopAudio() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.AUDIO_FAILED, "视频没有正在播放，操作失败");
            return;
        }

        boolean audioOpt = VMSNetSDK.getInstance().stopLiveAudioOpt(windowIndex);
        if (audioOpt) {
            mIsAudio = false;
            view.onPlayMsg(position, HK8700Constant.PlayLive.AUDIO_CLOSE_SUCCESS, "音频已关闭");
        }
    }


    public void startControl(int cmd) {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED, "视频没有正在播放，无法控制");
            return;
        }
        if (currentCommand != -1) {
            view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED, "请先停止当前的控制");
            return;
        }
        view.showLoadingDialog("正在发送指令");
        VMSNetSDK.getInstance().sendPTZCtrlCommand(windowIndex, true, SDKConstant.PTZCommandConstant.ACTION_START, cmd, 256, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                view.hideLoadingDialog();
                super.onFailure();
                view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED, "控制失败");
            }

            @Override
            public void onSuccess(Object o) {
                view.hideLoadingDialog();
                super.onSuccess(o);
                view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_SUCCESS, "发送指令成功");
                currentCommand = cmd;
            }
        });
    }


    public void stopControl() {
        if (currentStatus == HK8700Constant.PlayStatus.LIVE_INIT){
            view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED, "视频没有正在播放，无法控制");
            return;
        }
        if (currentCommand == -1) return;

        view.showLoadingDialog("正在停止");
        VMSNetSDK.getInstance().sendPTZCtrlCommand(windowIndex, true, SDKConstant.PTZCommandConstant.ACTION_STOP, currentCommand, 256, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                view.hideLoadingDialog();
                super.onFailure();
                view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_FAILED, "停止失败");
            }

            @Override
            public void onSuccess(Object o) {
                view.hideLoadingDialog();
                super.onSuccess(o);
                view.onPlayMsg(position, HK8700Constant.PlayLive.SEND_CTRL_CMD_SUCCESS, "停止成功");
                currentCommand = -1;
            }
        });
    }
    // ——————————————————————————————————————————————————————————————————

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public int getCurrentStatus() {
        return currentStatus;
    }

    public SubResourceNodeBean getCameraInfo() {
        return cameraInfo;
    }
}
