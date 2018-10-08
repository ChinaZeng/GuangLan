package com.zzw.socketdemo.rx;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.zzw.socketdemo.base.BaseFragment;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class LifeObservableTransformer<S> implements ObservableTransformer<S, S> {

    private LifecycleProvider provider;

    public LifeObservableTransformer(LifecycleProvider provider) {
        this.provider = provider;
    }

    @Override
    public ObservableSource<S> apply(Observable<S> upstream) {
        return upstream
                .compose(provider.<S>bindToLifecycle())
                .compose(SchedulersIoMainTransformer.<S>create());
    }


    public static <S> LifeObservableTransformer<S> create(LifecycleProvider provider) {
        return new LifeObservableTransformer<>(provider);
    }
}
