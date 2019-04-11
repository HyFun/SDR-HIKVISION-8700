package com.sdr.hikvision8700.support;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.bean.SubResourceParam;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.sdr.lib.ui.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700ResourceHelper {

    private List<TreeNode> treeNodeList = new ArrayList<>();

    public void start() {
        treeNodeList.clear();
        getRootControlCenter();
    }

    public List<TreeNode> getTreeNodeList() {
        return treeNodeList;
    }

    /**
     * 获取根控制中心
     */
    private void getRootControlCenter() {
        VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, SDKConstant.SysType.TYPE_VIDEO, 999, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                throw new RuntimeException("获取控制中心内容失败");
            }

            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {
                    RootCtrlCenter rootCtrlCenter = (RootCtrlCenter) obj;
                    int parentNodeType = Integer.parseInt(rootCtrlCenter.getNodeType());
                    int parentId = rootCtrlCenter.getId();
                    getSubResourceList(parentNodeType, parentId);
                }
            }
        });
    }


    /**
     * 获取父节点资源列表
     *
     * @param parentNodeType 父节点类型
     * @param pId            父节点ID
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, parentNodeType, String.valueOf(pId), new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                super.onFailure();
            }

            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof SubResourceParam) {
                    List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                    if (list != null && list.size() > 0) {
                        for (SubResourceNodeBean bean : list) {
                            treeNodeList.add(new TreeNode(bean.getId() + "", bean.getPid(), bean.getName(), false, bean));

                            int nodeType = bean.getNodeType();
                            if (nodeType != SDKConstant.NodeType.TYPE_CAMERA_OR_DOOR) {
                                getSubResourceList(nodeType, bean.getId());
                            }
                            //读取监控点在线数和总数
                            //CNetSDKLog.info("cameraOnline:  " + bean.getCameraOnline() + "cameraTotal: " + bean.getCameraTotal());
                        }
                    }
                }
            }
        });
    }

}
