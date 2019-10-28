package com.sdr.hikvision8700.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdr.hikvision8700.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by HyFun on 2019/10/28.
 * Email: 775183940@qq.com
 * Description:
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {


    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem item) {
        View view = LayoutInflater.from(context).inflate(R.layout.hk_8700_layout_recycler_item_hkvideo_camera_list, null, false);
        ImageView icon = view.findViewById(R.id.hk8700_camera_list_item_iv_icon);
        TextView textView = view.findViewById(R.id.hk8700_camera_list_item_tv_title);
        icon.setImageResource(item.getIcon());
        textView.setText(item.getText());

        return view;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        private Object object;

        public IconTreeItem(int icon, String text, Object object) {
            this.icon = icon;
            this.text = text;
            this.object = object;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }
}
