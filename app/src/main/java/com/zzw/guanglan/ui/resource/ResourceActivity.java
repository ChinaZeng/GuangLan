package com.zzw.guanglan.ui.resource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.ui.guanglan.add.GuangLanAddActivitty;
import com.zzw.guanglan.ui.juzhan.add.JuZhanAddActivity;
import com.zzw.guanglan.utils.PopWindowUtils;
import com.zzw.guanglan.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Create by zzw on 2018/12/7
 */
public class ResourceActivity extends BaseActivity implements LocationManager.OnLocationListener {
    @BindView(R.id.map_view)
    MapView mapView;

    private AMap aMap;

    public static void open(Context context) {
        context.startActivity(new Intent(context, ResourceActivity.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_resource;
    }


    @OnClick({R.id.tv_res_look, R.id.tv_my_gd, R.id.tv_add, R.id.tv_hot_conn, R.id.iv_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_res_look:
                PopWindowUtils.showListPop(this, view, new String[]{"附近", "查询"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (location == null) {
                            ToastUtils.showToast("请您先定位!");
                            return;
                        }
                        if (position == 0) {
                            NearbyResActivity.open(ResourceActivity.this, location);
                        } else {
                            ResourceSearchActivity.open(ResourceActivity.this);
                        }
                    }
                });
                break;
            case R.id.tv_my_gd:
                ToastUtils.showToast("我的工单");
                break;
            case R.id.tv_add:
                PopWindowUtils.showListPop(this, view, new String[]{"局站", "光缆"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            JuZhanAddActivity.open(ResourceActivity.this);
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


    private LocationManager.LocationBean location;
    private LocationManager locationManager;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
//            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    Log.e("zzz", "onMyLocationChange");
//                    LocationManager.LocationBean bean = new LocationManager.LocationBean();
//                    bean.latitude = location.getLatitude();
//                    bean.longitude = location.getLongitude();
//                    ResourceActivity.this.location = bean;
//                }
//            });
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showMySelf();
    }

    void addMark() {
        //https://lbs.amap.com/api/android-sdk/guide/draw-on-map/draw-marker

//        LatLng latLng = new LatLng(39.906901, 116.397972);
//        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
    }

    private Marker locationMarker;

    void setLocationMark(LocationManager.LocationBean bean) {
        //https://lbs.amap.com/api/android-sdk/guide/draw-on-map/draw-marker
        LatLng latLng = new LatLng(bean.latitude, bean.longitude);
        //添加Marker显示定位位置
        if (locationMarker == null) {
            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
            locationMarker = aMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_marker_2d)));
        } else {
            //已经添加过了，修改位置即可
            locationMarker.setPosition(latLng);
        }
        //然后可以移动到定位点,使用animateCamera就有动画效果
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //改变地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomBy(6));
    }

    private void showMySelf() {
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.interval(2000);
        //设置定位蓝点的Style
//        aMap.setMyLocationStyle(myLocationStyle);
//        设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(false);
        //改变地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomBy(6));
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        stopLocation();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {
        this.location = bean;
        setLocationMark(bean);
    }

    @Override
    public void onError(int code, String msg) {
        ToastUtils.showToast("定位失败:" + msg);
    }
}
