package com.sdr.hikvision8700.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hikvision.sdk.consts.HttpConstants;
import com.orhanobut.logger.Logger;
import com.sdr.hikvision8700.R;
import com.sdr.hikvision8700.SDR_HIKVISION_8700_HTTPS;
import com.sdr.hikvision8700.base.HK8700BaseActivity;
import com.sdr.hikvision8700.constant.HK8700Constant;
import com.sdr.hikvision8700.contract.HK8700MainContract;
import com.sdr.hikvision8700.data.HK8700History;
import com.sdr.hikvision8700.data.HK8700ItemControl;
import com.sdr.hikvision8700.data.HK8700User;
import com.sdr.hikvision8700.presenter.HK8700MainPresenter;
import com.sdr.hikvision8700.support.HK8700Util;
import com.sdr.lib.http.HttpClient;
import com.sdr.lib.ui.tree.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HK8700MainActivity extends HK8700BaseActivity<HK8700MainPresenter> implements HK8700MainContract.View {

    RecyclerView rvHkMain;
    RadioGroup rgSwitchView;
    ImageView ivHistory;
    ImageView ivZoomOut;

    private HK8700MainRecyclerAdapter mainRecyclerAdapter;
    // 默认显示的数量  2  x  2
    private int currentViewNum = 2;
    private List<TreeNode> treeNodeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hk8700_main);
        setTitle("实时监控");
        setDisplayHomeAsUpEnabled();
        initView();
        initData();
        initListener();

    }

    private void initView() {
        rvHkMain = findViewById(R.id.hk_video_main_rv);
        rgSwitchView = findViewById(R.id.hk_video_main_rg_switch);
        ivHistory = findViewById(R.id.hk_video_main_iv_history);
        ivZoomOut = findViewById(R.id.hk_video_main_iv_zoom_out);
    }

    private void initData() {
        presenter = new HK8700MainPresenter(this);
        // 先登录
        showLoadingView();
        presenter.init(
                HttpConstants.HTTPS + HK8700User.getInstance().getUrl(),
                HK8700User.getInstance().getUserName(),
                HK8700User.getInstance().getPassWord(),
                SDR_HIKVISION_8700_HTTPS.getInstance().getMacAddr()
        );

        changeRecycler(currentViewNum, null);
    }

    private void initListener() {
        for (int i = 0; i < rgSwitchView.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) rgSwitchView.getChildAt(i);
            final int switchCount = i + 1;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentViewNum == switchCount) return;
                    changeRecycler(switchCount, null);
                }
            });
        }


        // 点击历史的时候
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取历史记录
                final HK8700History hkHistory = (HK8700History) HK8700Util.getHkACache().getAsObject(HK8700Constant.HIK_VISION_8700_HISTORY);
                if (hkHistory == null || hkHistory.getCameraInfoList().isEmpty()) {
                    showErrorMsg("没有浏览历史记录");
                    return;
                }
                // 有历史记录  开启预览
                // 关闭正在播放的视频  然后开启历史记录
                HK8700Util.closeAllPlayingVideo(mainRecyclerAdapter.getData())
                        .subscribe(ret -> {
                            changeRecycler(hkHistory.getViewNum(), hkHistory.getCameraInfoList());
                        });
            }
        });


        // 点击放大的时候
        ivZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainRecyclerAdapter.getSelectedPosition() != -1 && mainRecyclerAdapter.getData().get(mainRecyclerAdapter.getSelectedPosition()).getCurrentStatus() !=
                        HK8700Constant.PlayStatus.LIVE_INIT) {
                    // 正在播放的时候才方法
                    HK8700ControlActivity.startHK8700ControlActivity(getActivity(), mainRecyclerAdapter.getData().get(mainRecyclerAdapter.getSelectedPosition()).getCameraInfo());
                } else {
                    showErrorMsg("请选择一个正在播放的窗口");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HK8700User.destroy();
    }

    @Override
    protected void setNavigationOnClickListener() {
        new MaterialDialog.Builder(getContext())
                .title("提示")
                .content("确定退出？")
                .positiveText("退出")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // 1.先保存最后的信息
                        if (mainRecyclerAdapter != null) {
                            List<HK8700History.CameraInfo> cameraInfoList = new ArrayList<>();
                            List<HK8700ItemControl> hkItemControls = mainRecyclerAdapter.getData();
                            for (HK8700ItemControl item : hkItemControls) {
                                if (item.getCurrentStatus() != HK8700Constant.PlayStatus.LIVE_INIT && item.getCameraInfo() != null) {
                                    cameraInfoList.add(new HK8700History.CameraInfo(item.getPosition(), item.getCameraInfo()));
                                }
                            }
                            HK8700History hkHistory = new HK8700History(currentViewNum, cameraInfoList);
                            HK8700Util.getHkACache().put(HK8700Constant.HIK_VISION_8700_HISTORY, hkHistory);
                        }
                        // 2.关闭正在播放的视频  关闭完成之后  结束anctivity
                        showLoadingDialog("正在关闭视频");
                        HK8700Util.closeAllPlayingVideo(mainRecyclerAdapter.getData()).subscribe(ret -> {
                            hideLoadingDialog();
                            finish();
                        });
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        setNavigationOnClickListener();
    }

    // ——————————————————PRIVATE——————————————————————

    private void changeRecycler(int num, List<HK8700History.CameraInfo> cameraInfoList) {
        if (mainRecyclerAdapter == null || cameraInfoList != null) {
            List<HK8700ItemControl> itemList = new ArrayList<>();
            mainRecyclerAdapter = new HK8700MainRecyclerAdapter(R.layout.hk_8700_layout_hkvideo_main_recycler_item, itemList, this, treeNodeList);
            mainRecyclerAdapter.setCameraInfoList(cameraInfoList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), num);
            rvHkMain.setLayoutManager(gridLayoutManager);
            mainRecyclerAdapter.bindToRecyclerView(rvHkMain);
            rvHkMain.setAdapter(mainRecyclerAdapter);
            for (int i = 0; i < num * num; i++) {
                itemList.add(new HK8700ItemControl(i, mainRecyclerAdapter));
            }
            mainRecyclerAdapter.notifyDataSetChanged();
        } else {
            mainRecyclerAdapter.setCameraInfoList(cameraInfoList);
            GridLayoutManager gridLayoutManager = (GridLayoutManager) rvHkMain.getLayoutManager();
            gridLayoutManager.setSpanCount(num);
            if (num >= currentViewNum) {
                // 少 变  多
                int addCount = (num * num) - (currentViewNum * currentViewNum);
                for (int i = 0; i < addCount; i++) {
                    mainRecyclerAdapter.addData(new HK8700ItemControl(i + (currentViewNum * currentViewNum), mainRecyclerAdapter));
                }
            } else {
                // 多 变 少
                // 找出后边所有正在播放的窗口
                List<HK8700ItemControl> hkItemControlList = new ArrayList<>();
                for (int i = (num * num); i < mainRecyclerAdapter.getData().size(); i++) {
                    HK8700ItemControl hkItemControl = mainRecyclerAdapter.getData().get(i);
                    hkItemControlList.add(hkItemControl);
                }
                // 关闭播放
                HK8700Util.closeAllPlayingVideo(hkItemControlList)
                        .subscribe(ret -> {
                            List<HK8700ItemControl> hkItemControls = mainRecyclerAdapter.getData();
                            Iterator<HK8700ItemControl> iterator = hkItemControls.iterator();
                            while (iterator.hasNext()) {
                                HK8700ItemControl item = iterator.next();
                                if (item.getPosition() >= (num * num)) {
                                    iterator.remove();
                                    mainRecyclerAdapter.notifyItemRemoved(item.getPosition());
                                }
                            }
                        });
            }
        }
    }

    // ——————————————————VIEW——————————————————————

    @Override
    public void loginSuccess() {
        showContentView();
    }

    @Override
    public void refreshCameraList(List<TreeNode> treeNodeList) {
        this.treeNodeList.clear();
        this.treeNodeList.addAll(treeNodeList);
    }

    @Override
    public void initFailed(String message) {
        showErrorMsg(message);
        finish();
    }
}
