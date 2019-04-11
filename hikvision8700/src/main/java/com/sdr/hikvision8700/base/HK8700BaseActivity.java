package com.sdr.hikvision8700.base;

import android.graphics.drawable.Drawable;

import com.sdr.hikvision8700.HIKVISION8700;
import com.sdr.lib.base.BaseActivity;
import com.sdr.lib.mvp.AbstractPresenter;
import com.sdr.lib.mvp.AbstractView;
import com.sdr.lib.ui.dialog.SDRLoadingDialog;
import com.sdr.lib.util.ToastTopUtil;
import com.sdr.lib.util.ToastUtil;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700BaseActivity<T extends AbstractPresenter> extends BaseActivity implements AbstractView {
    protected T presenter;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }



    @Override
    protected int onHeaderBarToolbarRes() {
        return HIKVISION8700.getInstance().getToolbarRes();
    }

    @Override
    protected Drawable onHeaderBarDrawable() {
        return HIKVISION8700.getInstance().getDrawable();
    }


    // ————————————————————————————————————————————

    private SDRLoadingDialog sdrLoadingDialog;

    @Override
    public void showLoadingDialog(String s) {
        if (sdrLoadingDialog == null) {
            sdrLoadingDialog = new SDRLoadingDialog.Builder(getContext())
                    .blur(true)
                    .cancel(false)
                    .build();
        }
        sdrLoadingDialog.setContent(s);
    }

    @Override
    public void hideLoadingDialog() {
        if (sdrLoadingDialog != null) {
            sdrLoadingDialog.dismiss();
        }
    }

    @Override
    public void showSuccessMsg(String s) {
        ToastTopUtil.showCorrectTopToast(s);
    }

    @Override
    public void showErrorMsg(String s) {
        ToastTopUtil.showErrorTopToast(s);
    }

    @Override
    public void showNormalMsg(String s) {
        ToastTopUtil.showNormalTopToast(s);
    }

    @Override
    public void showSuccessToast(String s) {
        ToastUtil.showCorrectMsg(s);
    }

    @Override
    public void showErrorToast(String s) {
        ToastUtil.showErrorMsg(s);
    }

    @Override
    public void showNormalToast(String s) {
        ToastUtil.showNormalMsg(s);
    }
}
