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

//    @FormUrlEncoded
//    @POST("/glcs/staffmgr/appLogin")
//    Observable<ResultBean<Object>> login(@FieldMap Map<String, String> s);


    @Multipart
    @POST("/glcs/staffmgr/appLogin")
    Observable<ResultBean<Object>> login(@PartMap Map<String, RequestBody> s);


    @GET("/glcs/bseRoom/getAreaTree")
    Observable<ResultBean<Object>> getAreaTree();


//    @FormUrlEncoded
//    @POST("/glcs/cblCable/getAppListDuanByPage")
//    Observable<ListDataBean<GuanLanItemBean>> getAppListDuanByPage(@FieldMap Map<String, String> s);

    @Multipart
    @POST("/glcs/cblCable/getAppListDuanByPage")
    Observable<ListDataBean<GuanLanItemBean>> getAppListDuanByPage(@PartMap Map<String, RequestBody> requestBodyMap);


    @FormUrlEncoded
    @POST("/glcs/cblCable/getAppListByPage")
    Observable<ListDataBean<GuanLanItemBean>> getAppListByPageCbl(@FieldMap Map<String, String> s);


    @FormUrlEncoded
    @POST("/glcs/cblFiber/getAppListByPage")
    Observable<ResultBean<List<QianXinItemBean>>> getAppListByPage(@FieldMap Map<String, String> s);


    @FormUrlEncoded
    @POST("/glcs/cblCable/duanAppAdd")
    Observable<ResultBean<Object>> duanAppAdd(@FieldMap Map<String, String> s);

}
