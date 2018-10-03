package com.zzw.socketdemo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zzw.socketdemo.http.retrofit.error.ExceptionHandler;
import com.zzw.socketdemo.http.retrofit.error.IExceptionHandler;
import com.zzw.socketdemo.rx.IError;
import com.zzw.socketdemo.rx.SchedulersIoMainTransformer;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public abstract class BaseFragment extends RxFragment implements IError {
    protected View rootView;
    private Unbinder mUnbinder;
    private IExceptionHandler exceptionHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), null);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }


    protected void initView() {

    }

    protected void initData() {

    }

    protected abstract int getLayoutId();


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showError(final Throwable t) {
        final FragmentActivity activity = getActivity();
        if (t != null && activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (exceptionHandler == null) {
                        exceptionHandler = new ExceptionHandler();
                    }
                    exceptionHandler.handle(activity, t);
                }
            });
        }
    }

    public <S> ObservableTransformer<S, S> transformer() {
        return new ObservableTransformer<S, S>() {
            @Override
            public ObservableSource<S> apply(Observable<S> upstream) {
                return upstream
                        .compose(BaseFragment.this.<S>bindToLifecycle())
                        .compose(SchedulersIoMainTransformer.<S>create());
            }
        };
    }

}
