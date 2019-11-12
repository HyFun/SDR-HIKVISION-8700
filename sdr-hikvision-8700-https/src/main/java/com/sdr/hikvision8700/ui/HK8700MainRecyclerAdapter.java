package com.sdr.hikvision8700.ui;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.sdr.hikvision8700.R;
import com.sdr.hikvision8700.constant.HK8700Constant;
import com.sdr.hikvision8700.contract.HK8700PlayContract;
import com.sdr.hikvision8700.data.HK8700History;
import com.sdr.hikvision8700.data.HK8700ItemControl;
import com.sdr.hikvision8700.support.HK8700PlayListDialog;
import com.sdr.lib.mvp.AbstractView;
import com.sdr.lib.ui.tree.TreeNode;

import java.util.List;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700MainRecyclerAdapter extends BaseQuickAdapter<HK8700ItemControl, BaseViewHolder> implements HK8700PlayContract.View {

    private AbstractView view;
    private List<TreeNode> treeNodeList;
    private List<HK8700History.CameraInfo> cameraInfoList;

    private int lastClickPosition = -1;

    public HK8700MainRecyclerAdapter(int layoutResId, @Nullable List<HK8700ItemControl> data, AbstractView view, List<TreeNode> treeNodeList) {
        super(layoutResId, data);
        this.view = view;
        this.treeNodeList = treeNodeList;
    }

    public void setCameraInfoList(List<HK8700History.CameraInfo> cameraInfoList) {
        this.cameraInfoList = cameraInfoList;
    }

    @Override
    protected void convert(BaseViewHolder helper, HK8700ItemControl item) {
        final int position = helper.getLayoutPosition();

        final FrameLayout frameLayout = helper.getView(R.id.hk_video_main_item_sfl_container);
        final SurfaceView surfaceView = helper.getView(R.id.hk_video_main_item_sv);
        final ImageView imageView = helper.getView(R.id.hk_video_main_item_iv_add);
        surfaceView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        if (cameraInfoList != null && !cameraInfoList.isEmpty()) {
            for (HK8700History.CameraInfo camera : cameraInfoList) {
                if (camera.getPosition() == position) {
                    surfaceView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    item.startLive(surfaceView, camera.getCamera());
                }
            }
        }

        // 点击事件
        surfaceView.setOnClickListener(v -> {
            if (lastClickPosition != -1) {
                // 设置之前的view为透明
                FrameLayout lastFrameLayout = (FrameLayout) getViewByPosition(lastClickPosition, R.id.hk_video_main_item_sfl_container);
                lastFrameLayout.setBackgroundColor(Color.TRANSPARENT);
            }
            // 设置当前的view为
            frameLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            lastClickPosition = position;
        });

        surfaceView.setOnLongClickListener(v -> {
            new MaterialDialog.Builder(mContext)
                    .title("提示")
                    .content("是否关闭播放")
                    .positiveText("关闭")
                    .negativeText("取消")
                    .onPositive((dialog, which) -> item.stopLive())
                    .show();
            return true;
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示选择的dialog
                new HK8700PlayListDialog(mContext, HK8700MainRecyclerAdapter.this.view, new HK8700PlayListDialog.OnCameraListClickListener() {
                    @Override
                    public void onClick(SubResourceNodeBean cameraInfo) {
                        // 开始加载播放
                        surfaceView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                        item.startLive(surfaceView, cameraInfo);
                    }
                })
                        .show();
            }
        });

    }

    @Override
    public void showLoadingDialog(String message) {
        view.showLoadingDialog(message);
    }

    @Override
    public void hideLoadingDialog() {
        view.hideLoadingDialog();
    }

    @Override
    public void onPlayMsg(int position, int code, String msg) {
        SurfaceView surfaceView = (SurfaceView) getViewByPosition(position, R.id.hk_video_main_item_sv);
        ImageView imageView = (ImageView) getViewByPosition(position, R.id.hk_video_main_item_iv_add);
        if (surfaceView == null || imageView == null) return;
        if (code == HK8700Constant.PlayLive.PLAY_LIVE_SUCCESS) {
            // 取流成功
            surfaceView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else if (code == HK8700Constant.PlayLive.PLAY_LIVE_STOP_SUCCESS) {
            // 停止成功
            surfaceView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else if (code == HK8700Constant.PlayLive.PLAY_LIVE_FAILED || code == HK8700Constant.PlayLive.PLAY_LIVE_RTSP_FAIL) {
            surfaceView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            // 播放失败
            view.showErrorMsg("播放失败","第" + (position + 1) + "个位置" + msg);
        }
    }


    public int getSelectedPosition() {
        return lastClickPosition;
    }
}
