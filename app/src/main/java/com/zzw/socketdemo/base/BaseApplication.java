package com.zzw.socketdemo.base;

import android.app.Application;
import android.widget.Toast;

import com.zzw.socketdemo.http.retrofit.GlobeHttpHandler;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.utils.ToastUtils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseApplication extends Application implements GlobeHttpHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitHttpEngine.builder()
                .baseUrl("http://v.juhe.cn/")
                .globeHttpHandler(this)
//                    .interceptors()
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
