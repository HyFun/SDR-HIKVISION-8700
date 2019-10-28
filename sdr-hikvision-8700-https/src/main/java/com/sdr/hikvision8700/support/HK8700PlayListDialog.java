package com.sdr.hikvision8700.support;

import android.content.Context;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.bean.SubResourceParam;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.sdr.hikvision8700.R;
import com.sdr.lib.mvp.AbstractView;
import com.sdr.lib.util.AlertUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

/**
 * Created by Administrator on 2018/5/25.
 */

public class HK8700PlayListDialog {
    public interface OnCameraListClickListener {
        void onClick(SubResourceNodeBean bean);
    }


    private Context context;
    private AbstractView view;
    private OnCameraListClickListener listener;

    private MaterialDialog dialog = null;

    public HK8700PlayListDialog(Context context, AbstractView view, OnCameraListClickListener listener) {
        this.context = context;
        this.view = view;
        this.listener = listener;
    }

    public void show() {

        FrameLayout frameLayout = new FrameLayout(context);
        TreeNode root = TreeNode.root();

        AndroidTreeView tView = new AndroidTreeView(context, root);

        // 获取控制中心
        view.showLoadingDialog("正在加载...");
        // 显示dialog
        VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, SDKConstant.SysType.TYPE_VIDEO, 999, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                view.hideLoadingDialog();
                AlertUtil.showNegativeToast("获取控制中心失败");
            }

            @Override
            public void onSuccess(Object obj) {
                view.hideLoadingDialog();
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {
                    RootCtrlCenter rootCtrlCenter = (RootCtrlCenter) obj;
//                    int parentNodeType = Integer.parseInt(rootCtrlCenter.getNodeType());
//                    int parentId = rootCtrlCenter.getId();
//                    treeNodeList.add(new com.sdr.lib.ui.tree.TreeNode(rootCtrlCenter.getId() + "", rootCtrlCenter.getPid() + "", rootCtrlCenter.getName(), false, false, rootCtrlCenter));
//                    view.refreshCameraList(treeNodeList);
//                    getSubResourceList(parentNodeType, parentId);
                    tView.addNode(root, new TreeNode(new IconTreeItemHolder.IconTreeItem(getIcon(rootCtrlCenter.getNodeType()), rootCtrlCenter.getName(), rootCtrlCenter)));
                }
            }
        });


        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
                Object object = item.getObject();
                if ((object instanceof RootCtrlCenter) && node.getChildren().isEmpty()) {
                    RootCtrlCenter rootCtrlCenter = (RootCtrlCenter) object;
                    // 获取子数据
                    view.showLoadingDialog("正在加载...");
                    VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, Integer.parseInt(rootCtrlCenter.getNodeType()), String.valueOf(rootCtrlCenter.getId()), new OnVMSNetSDKBusiness() {
                        @Override
                        public void onFailure() {
                            super.onFailure();
                            view.hideLoadingDialog();
                            AlertUtil.showNegativeToast("获取失败");
                        }

                        @Override
                        public void onSuccess(Object obj) {
                            super.onSuccess(obj);
                            view.hideLoadingDialog();
                            if (obj instanceof SubResourceParam) {
                                List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                                if (list != null && list.size() > 0) {
                                    for (SubResourceNodeBean bean : list) {
                                        tView.addNode(node, new TreeNode(new IconTreeItemHolder.IconTreeItem(getIcon(bean.getNodeType() + ""), bean.getName(), bean)));
                                    }
                                }
                            }
                        }
                    });
                } else if (object instanceof SubResourceNodeBean) {
                    SubResourceNodeBean bean = (SubResourceNodeBean) object;
                    if (bean.getNodeType() == SDKConstant.NodeType.TYPE_CAMERA_OR_DOOR) {
                        if (listener != null) {
                            listener.onClick(bean);
                            dialog.dismiss();
                        }
                    } else if (node.getChildren().isEmpty()) {
                        // 获取子数据
                        view.showLoadingDialog("正在加载...");
                        VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, bean.getNodeType(), String.valueOf(bean.getId()), new OnVMSNetSDKBusiness() {
                            @Override
                            public void onFailure() {
                                super.onFailure();
                                view.hideLoadingDialog();
                                AlertUtil.showNegativeToast("获取失败");
                            }

                            @Override
                            public void onSuccess(Object obj) {
                                super.onSuccess(obj);
                                view.hideLoadingDialog();
                                if (obj instanceof SubResourceParam) {
                                    List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                                    if (list != null && list.size() > 0) {
                                        for (SubResourceNodeBean bean : list) {
                                            tView.addNode(node, new TreeNode(new IconTreeItemHolder.IconTreeItem(getIcon(bean.getNodeType() + ""), bean.getName(), bean)));
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });


        frameLayout.addView(tView.getView());

        dialog = new MaterialDialog.Builder(context)
                .title("请选择监控点")
                .customView(frameLayout, true)
                .show();

    }


    /**
     * 根据node type获取相对应的icon
     */

    private int getIcon(String type) {
        if ("1".equals(type)) {
            return R.drawable.hk_8700_ic_home;
        } else if ("2".equals(type)) {
            return R.drawable.hk_8700_ic_group;
        } else if ("3".equals(type)) {
            return R.drawable.hk_8700_ic_camera;
        }
        return R.drawable.hk_8700_ic_home;
    }
}
