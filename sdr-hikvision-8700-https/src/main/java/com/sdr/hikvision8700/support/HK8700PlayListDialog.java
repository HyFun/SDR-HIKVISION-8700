package com.sdr.hikvision8700.support;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sdr.lib.ui.tree.TreeNode;
import com.sdr.lib.ui.tree.TreeNodeRecyclerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/5/25.
 */

public class HK8700PlayListDialog {
    private Context mContext;
    private List<TreeNode> mTreeNodeList;
    private TreeNodeRecyclerAdapter.OnTreeNodeSigleClickListener mOnTreeNodeSigleClickListener;

    public HK8700PlayListDialog(Context context, List<TreeNode> treeNodeList, TreeNodeRecyclerAdapter.OnTreeNodeSigleClickListener onTreeNodeSigleClickListener) {
        mContext = context;
        mTreeNodeList = treeNodeList;
        mOnTreeNodeSigleClickListener = onTreeNodeSigleClickListener;
    }

    private MaterialDialog dialog;

    public void show() {
        try {
            RecyclerView recyclerView = new RecyclerView(mContext);
            TreeNodeRecyclerAdapter adapter = new TreeNodeRecyclerAdapter(mContext, mTreeNodeList, new TreeNodeRecyclerAdapter.OnTreeNodeSigleClickListener() {
                @Override
                public void onSigleClick(TreeNode treeNode, int visablePositon, int realDatasPositon, boolean isLeaf) {
                    if (isLeaf && dialog != null) {
                        dialog.dismiss();
                        if (mOnTreeNodeSigleClickListener != null)
                            mOnTreeNodeSigleClickListener.onSigleClick(treeNode, visablePositon, realDatasPositon, isLeaf);
                    }
                }
            }, 2);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(adapter);
            dialog = new MaterialDialog.Builder(mContext)
                    .title("请选择监控点")
                    .customView(recyclerView, false)
                    .show();
        } catch (Exception e) {
        }
    }
}
