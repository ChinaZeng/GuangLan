package com.zzw.socketdemo.http;

import com.zzw.socketdemo.bean.ResultBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("/glcs/staffmgr/appLogin")
    Observable<ResultBean> login(@FieldMap Map<String ,String> s);

}
