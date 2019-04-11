package com.sdr.hikvision8700.contract;

import com.sdr.lib.mvp.AbstractPresenter;
import com.sdr.lib.mvp.AbstractView;
import com.sdr.lib.ui.tree.TreeNode;

import java.util.List;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public interface HK8700MainContract {
    interface View extends AbstractView {
        void initSuccess(List<TreeNode> treeNodeList);

        void initFailed(String message);
    }


    interface Presenter extends AbstractPresenter<View> {
        void init(String url, String userName, String passWord, String macAddr);
    }
}
