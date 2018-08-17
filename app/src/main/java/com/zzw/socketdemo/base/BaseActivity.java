package com.zzw.socketdemo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zzw.socketdemo.http.retrofit.error.ExceptionHandler;
import com.zzw.socketdemo.http.retrofit.error.IExceptionHandler;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends RxAppCompatActivity {

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

}
