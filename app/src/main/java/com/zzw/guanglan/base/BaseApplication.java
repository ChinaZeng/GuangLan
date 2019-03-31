package com.zzw.guanglan.base;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.zzw.guanglan.Contacts;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.utils.ToastUtils;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class BaseApplication extends Application   {


    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        Timber.plant(new Timber.DebugTree());
        ToastUtils.init(this);
        RetrofitHttpEngine.builder()
                .baseUrl(Contacts.BASE_URL)
                .interceptors(new Interceptor[]{
                        new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                            @Override
                            public void log(String message) {
                                Timber.tag("okhttp").w(message);
                            }
                        })
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                })
                .build();

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


        CrashReport.initCrashReport(getApplicationContext(), "a95c12706e", false);
    }

    public static BaseApplication getApplication() {
        return application;
    }


}
