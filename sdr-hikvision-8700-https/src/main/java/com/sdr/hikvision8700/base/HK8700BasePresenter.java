package com.sdr.hikvision8700.base;

import com.sdr.lib.mvp.AbstractPresenter;
import com.sdr.lib.mvp.AbstractView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by HyFun on 2018/11/13.
 * Email: 775183940@qq.com
 * Description:
 */

public abstract class HK8700BasePresenter<T extends AbstractView> implements AbstractPresenter<T> {
    protected T view;
    private CompositeDisposable compositeDisposable;

    public HK8700BasePresenter(T view) {
        this.view = view;
    }

    @Override
    public void attachView(T t) {

    }

    @Override
    public void detachView() {
        view = null;
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    public void addSubscription(Disposable... disposables) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.addAll(disposables);
    }
}
