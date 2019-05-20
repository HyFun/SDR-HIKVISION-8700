package com.sdr.hikvision8700.data;

import com.hikvision.sdk.net.bean.SubResourceNodeBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700History implements Serializable {
    private int viewNum;
    private List<CameraInfo> cameraInfoList;

    public HK8700History(int viewNum, List<CameraInfo> cameraInfoList) {
        this.viewNum = viewNum;
        this.cameraInfoList = cameraInfoList;
    }

    public int getViewNum() {
        return viewNum;
    }

    public List<CameraInfo> getCameraInfoList() {
        return cameraInfoList;
    }

    public static class CameraInfo implements Serializable {
        private int position;
        private SubResourceNodeBean camera;

        public CameraInfo(int position, SubResourceNodeBean camera) {
            this.position = position;
            this.camera = camera;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public SubResourceNodeBean getCamera() {
            return camera;
        }

        public void setCamera(SubResourceNodeBean camera) {
            this.camera = camera;
        }
    }
}
