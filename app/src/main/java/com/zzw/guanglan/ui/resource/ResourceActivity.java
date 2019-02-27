package com.zzw.guanglan.ui.resource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.ResBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.location.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.ui.guanglan.add.GuangLanAddActivitty;
import com.zzw.guanglan.ui.guangland.GuangLanDListActivity;
import com.zzw.guanglan.ui.room.add.RoomAddActivity;
import com.zzw.guanglan.utils.PopWindowUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Create by zzw on 2018/12/7
 */
public class ResourceActivity extends BaseActivity implements LocationManager.OnLocationListener,
        BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener {
    @BindView(R.id.map_view)
    MapView mapView;

    private BaiduMap aMap;


    private int nowType = 0, nowDistance = 2; //0 机房  1光缆

    public static void open(Context context) {
        context.startActivity(new Intent(context, ResourceActivity.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_resource;
    }


    @OnClick({R.id.tv_res_look, R.id.tv_my_gd, R.id.tv_add, R.id.tv_room, R.id.tv_guanglan, R.id.tv_hot_conn, R.id.iv_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_res_look:
                PopWindowUtils.showListPop(this, view, new String[]{"附近", "查询"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (myLocation == null) {
                            ToastUtils.showToast("请您先定位!");
                            return;
                        }
                        if (position == 0) {
                            NearbyResActivity.open(ResourceActivity.this, myLocation);
                        } else {
                            ResourceSearchActivity.open(ResourceActivity.this, myLocation);
                        }
                    }
                });
                break;
            case R.id.tv_my_gd:
                ToastUtils.showToast("我的工单");
                break;
            case R.id.tv_room:
                showResDataPop(0, view);
                break;
            case R.id.tv_guanglan:
                showResDataPop(1, view);
                break;
            case R.id.tv_add:
                PopWindowUtils.showListPop(this, view, new String[]{"局站", "光缆"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            RoomAddActivity.open(ResourceActivity.this);
                        } else {
                            GuangLanAddActivitty.open(ResourceActivity.this);
                        }
                    }
                });

                break;
            case R.id.tv_hot_conn:
                HotConnActivity.open(this);
                break;

            case R.id.iv_location:
                startLocation();
                break;
        }
    }


    private LocationManager.LocationBean centerLocation, myLocation;
    private LocationManager locationManager;

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {

//        todo 这里写死了 测试数据
//        bean.longitude = 116.450119;
//        bean.latitude = 39.927381;

//        bean.longitude = 118.70231499999993;
//        bean.latitude = 32.157075038623134;

        this.myLocation = bean;
        stopLocation();
        setLocationMark(bean);
    }

    @Override
    protected void initData() {
        super.initData();
        startLocation();
    }

    @SuppressLint("CheckResult")
    private void startLocation() {
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            stopLocation();
                            locationManager = new LocationManager(ResourceActivity.this,
                                    ResourceActivity.this);
                            locationManager.start();
                        } else {
                            ToastUtils.showToast("请开启定位权限");
                        }
                    }
                });
    }

    private void stopLocation() {
        if (locationManager != null) {
            locationManager.stop();
            locationManager = null;
        }
    }


    private void showResDataPop(final int type, View view) {
        if (centerLocation == null) {
            ToastUtils.showToast("请先定位!");
        }
        PopWindowUtils.showListPop(this, view,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        100.0f, view.getContext().getResources().getDisplayMetrics()),
                new String[]{"1km", "2km", "3km", "4km"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getResData(centerLocation, type, position + 1);
                    }
                });

    }

    /**
     * @param type     0 机房  1光缆
     * @param distance 千米数
     */
    private void getResData(final LocationManager.LocationBean locationBean, final int type, int distance) {
        if (locationBean == null) return;

        this.nowType = type;
        this.nowDistance = distance;
        if (type == 0) {
            RetrofitHttpEngine.obtainRetrofitService(Api.class)
                    .getAppJfInfo(String.valueOf(locationBean.longitude),
                            String.valueOf(locationBean.latitude),
                            String.valueOf(String.valueOf(distance)), null)
                    .compose(LifeObservableTransformer.<ListDataBean<ResBean>>create(this))
                    .subscribe(new ErrorObserver<ListDataBean<ResBean>>(this) {
                        @Override
                        public void onNext(ListDataBean<ResBean> listDataBean) {
                            if (centerLocation != locationBean)
                                return;
                            addRoomMark(type, listDataBean.getList());
                        }
                    });
        } else {
            RetrofitHttpEngine.obtainRetrofitService(Api.class)
                    .getAppGlInfo(
                            String.valueOf(locationBean.longitude),
                            String.valueOf(locationBean.latitude),
                            String.valueOf(String.valueOf(distance)), null)
                    .compose(LifeObservableTransformer.<ListDataBean<GuangLanBean>>create(this))
                    .subscribe(new ErrorObserver<ListDataBean<GuangLanBean>>(this) {
                        @Override
                        public void onNext(ListDataBean<GuangLanBean> listDataBean) {
                            if (centerLocation != locationBean)
                                return;

                            addGuangLanMark(type, listDataBean.getList());
                        }
                    });
        }

    }


    private Marker locationMarker;


    void setLocationMark(LocationManager.LocationBean bean) {

        if (bean == null)
            return;

        LatLng latLng = new LatLng(bean.latitude, bean.longitude);
        //添加Marker显示定位位置
        if (locationMarker == null) {
            locationMarker = addMark(bean.latitude, bean.longitude, bean, R.mipmap.icon_location_marker_2d);
        } else {
            //已经添加过了，修改位置即可
            locationMarker.setPosition(latLng);
        }

        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(latLng)
                //放大地图到20倍
                .zoom(12).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        aMap.animateMapStatus(mMapStatusUpdate);
    }


    private List<Marker> nowMarks = new ArrayList<>();
    private List<Polyline> polylines = new ArrayList<>();


    private Marker addMark(double latitude, double longitude, Serializable bean, int resId) {
        LatLng latLng = new LatLng(latitude
                , longitude);
        Bundle bundle = new Bundle();
        if (bean != null)
            bundle.putSerializable("bean", bean);
        OverlayOptions aOption = new MarkerOptions()
                .position(latLng)
                .extraInfo(bundle)
                .icon(BitmapDescriptorFactory.fromResource(resId));
        return (Marker) aMap.addOverlay(aOption);
    }

    private void addGuangLanMark(final int searchType, List<GuangLanBean> list) {
        cleanNowMark();

        this.nowType = searchType;
        aMap.removeMarkerClickListener(this);
//        aMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Bundle bundle = marker.getExtraInfo();
//                if (bundle != null) {
//                    GuangLanBean bean = (GuangLanBean) bundle.getSerializable("bean");
//                    if (bean != null) {
////                        QianXinListActivity.open(this,bean.get??);
////                        GuangLanDListActivity.open(ResourceActivity.this, bean.getRoomId(), centerLocation);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

        for (int i = 0; i < list.size(); i++) {
            GuangLanBean bean = list.get(i);

            if (!TextUtils.isEmpty(bean.getAGEOX()) && !TextUtils.isEmpty(bean.getAGEOY())) {
                nowMarks.add(
                        addMark(Double.parseDouble(bean.getAGEOY()),
                                Double.parseDouble(bean.getAGEOX()),
                                bean,
                                R.mipmap.icon_guanglan));

                if (!TextUtils.isEmpty(bean.getZGEOX()) && !TextUtils.isEmpty(bean.getZGEOY())) {
                    nowMarks.add(
                            addMark(Double.parseDouble(bean.getZGEOY()),
                                    Double.parseDouble(bean.getZGEOX()),
                                    bean,
                                    R.mipmap.icon_guanglan));

                    LatLng aLatLng = new LatLng(Double.parseDouble(bean.getAGEOY())
                            , Double.parseDouble(bean.getAGEOX()));
                    LatLng zLatLng = new LatLng(Double.parseDouble(bean.getZGEOY())
                            , Double.parseDouble(bean.getZGEOX()));
                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(aLatLng);
                    points.add(zLatLng);
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .points(points)
                            .width(5)
                            .color(Color.argb(255, 255, 0, 0));
                    Polyline polyline = (Polyline) aMap.addOverlay(polylineOptions);
                    polylines.add(polyline);
                }
            }
        }

    }

    private void cleanNowMark() {
        for (Marker roomMarker : nowMarks) {
            roomMarker.remove();
        }
        nowMarks.clear();

        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }


    private void addRoomMark(final int searchType, List<ResBean> list) {
        cleanNowMark();

        this.nowType = searchType;
        aMap.setOnMarkerClickListener(this);

        for (int i = 0; i < list.size(); i++) {
            ResBean bean = list.get(i);
            nowMarks.add(
                    addMark(Double.parseDouble(bean.getLatitude()),
                            Double.parseDouble(bean.getLongitude()),
                            bean,
                            R.mipmap.icon_room));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == locationMarker)
            return false;

        Bundle bundle = marker.getExtraInfo();
        if (bundle != null) {
            Serializable bean = bundle.getSerializable("bean");

            if (bean == null) return false;

            if (bean instanceof ResBean) {
                ResBean resBean = (ResBean) bean;
                GuangLanDListActivity.open(ResourceActivity.this, resBean.getRoomId(), centerLocation);
//                    EngineRoomDetailsActivity.open(ResourceActivity.this, data.get(pos));
                return true;
            }
        }

        return false;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapStatusChangeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        mapView = null;
        stopLocation();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
        super.onPause();

    }


    @Override
    public void onError(int code, String msg) {
        ToastUtils.showToast("定位失败:" + msg);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }


    //    private Marker centerMarker;
    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        LatLng la = aMap.getMapStatus().target;

        LocationManager.LocationBean locationBean = new LocationManager.LocationBean();
        locationBean.latitude = la.latitude;
        locationBean.longitude = la.longitude;
        this.centerLocation = locationBean;

//        if (centerMarker == null) {
//            centerMarker = addMark(la.latitude, la.longitude, null, R.mipmap.icon_center_location);
//        } else {
//            centerMarker.setPosition(la);
//        }

        getResData(locationBean, nowType, nowDistance);
    }


}
