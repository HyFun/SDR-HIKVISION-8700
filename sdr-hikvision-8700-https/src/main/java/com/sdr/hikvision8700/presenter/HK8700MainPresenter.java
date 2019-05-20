package com.sdr.hikvision8700.presenter;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.sdr.hikvision8700.data.HK8700User;
import com.sdr.hikvision8700.base.HK8700BasePresenter;
import com.sdr.hikvision8700.contract.HK8700MainContract;
import com.sdr.hikvision8700.support.HK8700ResourceHelper;
import com.sdr.lib.rx.RxUtils;
import com.sdr.lib.ui.tree.TreeNode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by HyFun on 2019/04/08.
 * Email: 775183940@qq.com
 * Description:
 */

public class HK8700MainPresenter extends HK8700BasePresenter<HK8700MainContract.View> implements HK8700MainContract.Presenter {

    public HK8700MainPresenter(HK8700MainContract.View view) {
        super(view);
    }

    @Override
    public void init(String url, String userName, String passWord, String macAddr) {
        VMSNetSDK.getInstance().Login(
                url,
                userName,
                passWord,
                macAddr,
                new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        super.onFailure();
                        view.initFailed("登录失败");
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        if (obj instanceof LoginData) {
                            LoginData loginData = (LoginData) obj;
                            // 保存至全局变量中
                            HK8700User.getInstance().setLoginData(loginData);
                            // 开始获取资源列表
                            final HK8700ResourceHelper resourceHelper = new HK8700ResourceHelper();
                            Observable.just(0)
                                    .flatMap(new Function<Integer, ObservableSource<List<TreeNode>>>() {
                                        @Override
                                        public ObservableSource<List<TreeNode>> apply(Integer integer) throws Exception {
                                            resourceHelper.start();
                                            return RxUtils.createData(resourceHelper.getTreeNodeList());
                                        }
                                    })
                                    .compose(RxUtils.<List<TreeNode>>io_main())
                                    .subscribe(new Consumer<List<TreeNode>>() {
                                        @Override
                                        public void accept(List<TreeNode> treeNodeList) throws Exception {
                                            view.initSuccess(treeNodeList);
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            view.initFailed(throwable.getMessage());
                                        }
                                    });
                        }
                    }
                }
        );
    }
}
