package com.zzw.guanglan.dialogs.area;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.dialogs.multilevel.IDataSet;
import com.zzw.guanglan.dialogs.multilevel.MultilLevelDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.SchedulersIoMainTransformer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class AreaDialog {

    public static MultilLevelDialog createCityDialog(final LifecycleProvider provider, String title, OnConfirmCallback<AreaBean> confirmCallback) {
        return MultilLevelDialog.newInstance(new IDataSet<AreaBean>() {

            @Override
            public Observable<List<AreaBean>> provideFirstLevel() {
                return RetrofitHttpEngine.obtainRetrofitService(Api.class).getAreaTree()
                        .compose(SchedulersIoMainTransformer.<List<AreaBean>>create())
                        .compose(LifeObservableTransformer.<List<AreaBean>>create(provider));
            }

            @Override
            public Observable<List<AreaBean>> provideChildren(List<AreaBean> parents) {
                List<AreaBean> children = parents.get(parents.size() - 1).getChildren();
                if(children !=null && children.size()!=0){
                   return Observable.just(children);
                }else {
                    List<AreaBean> children2  = new ArrayList<>();
                    return Observable.just(children2);
                }
            }
        },title).setConfirmCallback(confirmCallback);
    }



}
