package com.zzw.guanglan.http;

import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.LoginResultBean;
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.bean.ResultBean;
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

    @Multipart
    @POST("/glcs/cblCable/getAppListByPage")
    Observable<ListDataBean<GuangLanItemBean>> getGuangLanByPage(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblCable/getAppListDuanByPage")
    Observable<ListDataBean<GuangLanDItemBean>> getAppListDuanByPage(@PartMap Map<String, RequestBody> requestBodyMap);

    @Multipart
    @POST("/glcs/cblFiber/getAppListByPage")
    Observable<ListDataBean<QianXinItemBean>> getAppListByPage(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblCable/appadd")
    Observable<ResultBean<Object>> appAdd(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblCable/duanAppAdd")
    Observable<ResultBean<Object>> duanAppAdd(@PartMap Map<String, RequestBody> s);

    @Multipart
    @POST("/glcs/cblFiber/saveFiberFile")
    Observable<ResultBean<Object>> saveFiberFile(@PartMap Map<String, RequestBody> s, @Part MultipartBody.Part file);

}
