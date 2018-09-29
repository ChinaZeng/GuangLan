package com.zzw.socketdemo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zzw.socketdemo.http.retrofit.error.ExceptionHandler;
import com.zzw.socketdemo.http.retrofit.error.IExceptionHandler;
import com.zzw.socketdemo.rx.IError;
import com.zzw.socketdemo.rx.SchedulersIoMainTransformer;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class BaseActivity extends RxAppCompatActivity implements IError {

    private Unbinder mUnbinder;
    private IExceptionHandler exceptionHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        mUnbinder = ButterKnife.bind(this);

        initTitle();
        initView();
        initData();
    }


    //初始化界面
    protected abstract int initLayoutId();

    //初始化头部
    protected void initTitle() {

    }

    //初始化界面
    protected void initView() {

    }

    //初始化数据
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showError(final Throwable t) {
        if (t != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (exceptionHandler == null) {
                        exceptionHandler = new ExceptionHandler();
                    }
                    exceptionHandler.handle(BaseActivity.this, t);
                }
            });
        }
    }


    public <S> ObservableTransformer<S, S> transformer() {
        return new ObservableTransformer<S, S>() {
            @Override
            public ObservableSource<S> apply(Observable<S> upstream) {
                return upstream
                        .compose(BaseActivity.this.<S>bindToLifecycle())
                        .compose(SchedulersIoMainTransformer.<S>create());
            }
        };
    }

}
