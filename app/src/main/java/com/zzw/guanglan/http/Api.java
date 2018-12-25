package com.zzw.guanglan.http;

import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.GuangLanParamBean;
import com.zzw.guanglan.bean.JuZhanBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.LoginResultBean;
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.bean.RemoveBean;
import com.zzw.guanglan.bean.ResBean;
import com.zzw.guanglan.bean.ResultBean;
import com.zzw.guanglan.bean.RoomTypeBean;
import com.zzw.guanglan.bean.StationBean;
import com.zzw.guanglan.bean.StatusInfoBean;
import com.zzw.guanglan.bean.TeamInfoBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {

    @Multipart
    @POST("/glcs/staffmgr/appLogin")
    Observable<LoginResultBean> login(@PartMap Map<String, RequestBody> s);


    @GET("/glcs/bseRoom/getAreaTree")
    Observable<List<AreaBean>> getAreaTree();

    @GET("/glcs/bseRoom/getAllStation")
    Observable<List<StationBean>> getAllStation(@Query("areaId") String areaId);

    @GET("/glcs/bseRoom/getBseRoomListByArea")
    Observable<List<BseRoomBean>> getBseRoomListByArea(@Query("id") String id);

    @GET("/glcs/patrolScheme/getAppConstructionTeamInfo")
    Observable<ListDataBean<TeamInfoBean>> getAppConstructionTeamInfo();

    @GET("/glcs/pubrestrion/quertstatuslistinfo")
    Observable<List<StatusInfoBean>> quertstatuslistinfo();

    @GET("/glcs/pubrestrion/quertListInfo")
    Observable<List<GradeBean>> quertListInfo();

    @GET("/glcs/cblFiber/updateFiberState")
    Observable<ResultBean<Object>> updateFiberState(@Query("fiberId") String fiberId
            , @Query("stateId") String stateId);

    @GET("/glcs/cblCable/searchAppParam")
    Observable<ListDataBean<GuangLanItemBean>> searchAppParam(@Query("paramName") String paramName
            , @Query("pageNum") String pageNum);

    @Multipart
    @POST("/glcs/cblCable/getAppListByPage")
    Observable<ListDataBean<GuangLanItemBean>> getGuangLanByPage(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblCable/getAppListDuanByPage")
    Observable<ListDataBean<GuangLanDItemBean>> getAppListDuanByPage(@PartMap Map<String, RequestBody> requestBodyMap);

    @Multipart
    @POST("/glcs/cblFiber/getAppListByPage")
    Observable<ListDataBean<QianXinItemBean>> getAppListByPage(@PartMap Map<String, RequestBody> s);


    @GET("/glcs/cblCable/appAddGldInfo")
    Observable<ResultBean<Object>> appAddGldInfo(@QueryMap Map<String, String> s);

    @Multipart
    @POST("/glcs/cblCable/duanAppAdd")
    Observable<ResultBean<Object>> duanAppAdd(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblFiber/saveFiberFile")
    Observable<ResultBean<Object>> saveFiberFile(@PartMap Map<String, RequestBody> s, @Part MultipartBody.Part file);


    @GET("/glcs/cblFiber/remove")
    Observable<RemoveBean> remove(@Query("id") String id);


    @GET("/glcs/bseRoom/getAppJfOrGlByType")
    Observable<ListDataBean<ResBean>> getAppJfOrGlByType(
            @Query("type") String type,
            @Query("LONGITUDE") String LONGITUDE,
            @Query("LATITUDE") String LATITUDE,
            @Query("DISTANCE") String DISTANCE
    );

    @GET("/glcs/bseRoom/getAppJfOrGlByOthers")
    Observable<ListDataBean<ResBean>> getAppJfOrGlByOthers(
            @Query("type") String type,
            @Query("cityName") String cityName,
            @Query("areaName") String areaName,
            @Query("typeName") String typeName
    );

    @GET("/glcs/bseRoom/getAppTypeJfInfo")
    Observable<ListDataBean<RoomTypeBean>> getAppTypeJfInfo();


    @GET("/glcs/bseRoom/appAddJfInfo")
    Observable<ResultBean<Object>> appAddJfInfo(@QueryMap Map<String, String> s);


    @GET("/glcs/bseRoom/getAppJfAZInfo")
    Observable<ListDataBean<JuZhanBean>> getAppJfAZInfo(@Query("areaId") String areaId);

}
