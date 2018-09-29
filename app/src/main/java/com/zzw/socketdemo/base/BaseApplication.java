package com.zzw.socketdemo.base;

import android.app.Application;

import com.zzw.socketdemo.http.retrofit.GlobeHttpHandler;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.socket.utils.MyLog;
import com.zzw.socketdemo.utils.ToastUtils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class BaseApplication extends Application implements GlobeHttpHandler {


    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitHttpEngine.builder()
                .baseUrl("http://47.97.167.95:7088")
                .globeHttpHandler(this)
                .interceptors(new Interceptor[]{new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        MyLog.e("okhttp", message);
                    }
                }).setLevel(HttpLoggingInterceptor
                        .Level.BODY)})
                .build();
        ToastUtils.init(this);
    }

    @Override
    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
        return response;
    }

    @Override
    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
        return request;
    }
}
