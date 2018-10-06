package com.zzw.socketdemo.http;

import com.zzw.socketdemo.bean.GuanLanItemBean;
import com.zzw.socketdemo.bean.ListDataBean;
import com.zzw.socketdemo.bean.QianXinItemBean;
import com.zzw.socketdemo.bean.ResultBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface Api {

    @Multipart
    @POST("/glcs/staffmgr/appLogin")
    Observable<ResultBean<Object>> login(@PartMap Map<String, RequestBody> s);


    @GET("/glcs/bseRoom/getAreaTree")
    Observable<ResultBean<Object>> getAreaTree();

    @Multipart
    @POST("/glcs/cblCable/getAppListDuanByPage")
    Observable<ListDataBean<GuanLanItemBean>> getAppListDuanByPage(@PartMap Map<String, RequestBody> requestBodyMap);


    @Multipart
    @POST("/glcs/cblFiber/getAppListByPage")
    Observable<ListDataBean<QianXinItemBean>> getAppListByPage(@PartMap Map<String, RequestBody> s);


    @Multipart
    @POST("/glcs/cblCable/duanAppAdd")
    Observable<ResultBean<Object>> duanAppAdd(@PartMap Map<String, RequestBody> s);

}
