package com.zzw.guanglan.http;

import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GongDanBean;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanBean;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.JuZhanBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.UserBean;
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
    Observable<UserBean> login(@PartMap Map<String, RequestBody> s);


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

    @GET("/glcs/cblCable/appGetCblCableOpByJf")
    Observable<ListDataBean<GuangLanDItemBean>> appGetCblCableOpByJf(@QueryMap Map<String, String> s);


    @GET("/glcs/cblFiber/getAppFiberListByPage")
    Observable<ListDataBean<QianXinItemBean>> getAppFiberListByPage(@QueryMap Map<String, String> s);

    @GET("/glcs/cblCable/appAddGldInfo")
    Observable<ResultBean<Object>> appAddGldInfo(@QueryMap Map<String, String> s);

    @Multipart
    @POST("/glcs/cblCable/appAddGldInfo")
    Observable<ResultBean<Object>> duanAppAdd(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblFiber/saveFiberFile")
    Observable<ResultBean<Object>> saveFiberFile(@PartMap Map<String, RequestBody> s, @Part MultipartBody.Part file);


    @Multipart
    @POST("/glcs/cblFiber/saveImgFile")
    Observable<ResultBean<Object>> saveImgFile(@PartMap Map<String, RequestBody> s,
                                               @Part MultipartBody.Part aFile,
                                               @Part MultipartBody.Part zFile);


    @GET("/glcs/cblFiber/remove")
    Observable<RemoveBean> remove(@Query("id") String id);


    @GET("/glcs/bseRoom/getAppJfInfo")
    Observable<ListDataBean<ResBean>> getAppJfInfo(
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("distance") String distance,
            @Query("pageNum") String pageNum,
            @Query("areaId") String areaId
    );


    @GET("/glcs/cblCable/getAppGlInfo")
    Observable<ListDataBean<GuangLanBean>> getAppGlInfo(
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("distance") String distance,
            @Query("areaId") String areaId,
            @Query("pageNum") String pageNum
    );


    @GET("/glcs/bseRoom/getAppJfByOthers")
    Observable<ListDataBean<ResBean>> getAppJfByOthers(
            @Query("jfName") String jfName,
            @Query("cityName") String cityName,
            @Query("areaName") String areaName,
            @Query("areaId") String areaId,
            @Query("pageNum") int pageNum
    );

    @GET("/glcs/bseRoom/getAppTypeJfInfo")
    Observable<ListDataBean<RoomTypeBean>> getAppTypeJfInfo();


    @GET("/glcs/bseRoom/appAddJfInfo")
    Observable<ResultBean<Object>> appAddJfInfo(@QueryMap Map<String, String> s);


    @GET("/glcs/bseRoom/getAppJfAZInfo")
    Observable<ListDataBean<JuZhanBean>> getAppJfAZInfo(@Query("areaId") String areaId,
                                                        @Query("roomName") String roomName,
                                                        @Query("pageNum") String pageNum
    );


    @GET("/glcs/cableWorkOrder/getAppWorkOrderByPage")
    Observable<ListDataBean<GongDanBean>> getAppWorkOrderByPage(
            @Query("userId") String userId,
            @Query("areaId") String areaId,
            @Query("pageNum") String pageNum,
            @Query("cableOpName") String cableOpName
    );


    @GET("/glcs/cblCable/checkSerial")
    Observable<ResultBean<Object>> checkSerial(@Query("serial") String serial);


}
