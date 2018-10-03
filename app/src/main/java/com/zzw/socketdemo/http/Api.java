package com.zzw.socketdemo.http;

import com.zzw.socketdemo.bean.GuanLanItemBean;
import com.zzw.socketdemo.bean.QianXinItemBean;
import com.zzw.socketdemo.bean.ResultBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("/glcs/staffmgr/appLogin")
    Observable<ResultBean<Object>> login(@FieldMap Map<String, String> s);


    @FormUrlEncoded
    @POST("/glcs/cblCable/getAppListDuanByPage")
    Observable<ResultBean<List<GuanLanItemBean>>> getAppListDuanByPage(@FieldMap Map<String, String> s);

    @FormUrlEncoded
    @POST("/glcs/cblFiber/getAppListByPage")
    Observable<ResultBean<List<QianXinItemBean>>> getAppListByPage(@FieldMap Map<String, String> s);
}
