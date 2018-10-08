package com.zzw.socketdemo.base;

import android.app.Application;

import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.utils.ToastUtils;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class BaseApplication extends Application implements HttpLoggingInterceptor.Logger {


    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        Timber.plant(new Timber.DebugTree());
        ToastUtils.init(this);
        RetrofitHttpEngine.builder()
                .baseUrl("http://47.97.167.95:7088")
                .interceptors(new Interceptor[]{
                        new HttpLoggingInterceptor(this)
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                })
                .build();

    }

    public static BaseApplication getApplication() {
        return application;
    }

    @Override
    public void log(String message) {
        Timber.tag("okhttp").w(message);
    }
}
