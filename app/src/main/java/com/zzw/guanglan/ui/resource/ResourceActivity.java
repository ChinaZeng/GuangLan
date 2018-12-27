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

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.ResBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.ui.guanglan.add.GuangLanAddActivitty;
import com.zzw.guanglan.ui.guangland.GuangLanDListActivity;
import com.zzw.guanglan.ui.juzhan.add.JuZhanAddActivity;
import com.zzw.guanglan.utils.PopWindowUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

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


    private int nowType = 0; //0 机房  1光缆

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


    private void showResDataPop(final int type, View view) {
        if(location ==null){
            ToastUtils.showToast("请先定位!");
        }
        PopWindowUtils.showListPop(this, view,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        100.0f, view.getContext().getResources().getDisplayMetrics()),
                new String[]{"1km", "2km", "3km", "4km"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getResData(type, position + 1);
                    }
                });

    }

    /**
     * @param type     0 机房  1光缆
     * @param distance 千米数
     */
    private void getResData(final int type, int distance) {
        if (type == 0) {
            RetrofitHttpEngine.obtainRetrofitService(Api.class)
                    .getAppJfOrGlByType("机房",
                            String.valueOf(location.longitude),
                            String.valueOf(location.latitude),
                            String.valueOf(String.valueOf(distance)))
                    .compose(LifeObservableTransformer.<ListDataBean<ResBean>>create(this))
                    .subscribe(new ErrorObserver<ListDataBean<ResBean>>(this) {
                        @Override
                        public void onNext(ListDataBean<ResBean> listDataBean) {
                            addRoomMark(type, listDataBean.getList());
                        }
                    });
        } else {
            RetrofitHttpEngine.obtainRetrofitService(Api.class)
                    .getAppJfOrGlByTypeGuangLan("光缆",
                            String.valueOf(location.longitude),
                            String.valueOf(location.latitude),
                            String.valueOf(String.valueOf(distance)))
                    .compose(LifeObservableTransformer.<ListDataBean<GuangLanBean>>create(this))
                    .subscribe(new ErrorObserver<ListDataBean<GuangLanBean>>(this) {
                        @Override
                        public void onNext(ListDataBean<GuangLanBean> listDataBean) {
                            addGuangLanMark(type, listDataBean.getList());
                        }
                    });
//            String testData = " [{\n" +
//                    "\t\t\"CABLE_NAME\": \"南京浦口西葛光跳站-花旗CS机房光缆01\",\n" +
//                    "\t\t\"AHOSTNAME\": \"江宁至花旗营001045#\",\n" +
//                    "\t\t\"ZGEOX\": 118.65685,\n" +
//                    "\t\t\"AREA_NAME\": \"南京\",\n" +
//                    "\t\t\"CITY_NAME\": \"南京\",\n" +
//                    "\t\t\"ZGEOY\": 32.16453333333333,\n" +
//                    "\t\t\"ZHOSTNAME\": \"京沪005506#\",\n" +
//                    "\t\t\"CABLE_TYPE\": \"本地骨干光缆\",\n" +
//                    "\t\t\"AGEOY\": 32.162977933107506,\n" +
//                    "\t\t\"FLAG\": \"N\",\n" +
//                    "\t\t\"AGEOX\": 118.65298999671664,\n" +
//                    "\t\t\"CABLE_ID\": 3583357\n" +
//                    "\t}, {\n" +
//                    "\t\t\"CABLE_NAME\": \"旭日华庭-浦东花园CS光缆01\",\n" +
//                    "\t\t\"AHOSTNAME\": \"大桥北路020307#\",\n" +
//                    "\t\t\"ZGEOX\": 118.72404333333334,\n" +
//                    "\t\t\"AREA_NAME\": \"南京\",\n" +
//                    "\t\t\"CITY_NAME\": \"南京\",\n" +
//                    "\t\t\"ZGEOY\": 32.132196388888886,\n" +
//                    "\t\t\"ZHOSTNAME\": \"大桥北路020315#\",\n" +
//                    "\t\t\"CABLE_TYPE\": \"本地汇聚光缆\",\n" +
//                    "\t\t\"AGEOY\": 32.13957410788257,\n" +
//                    "\t\t\"FLAG\": \"N\",\n" +
//                    "\t\t\"AGEOX\": 118.7181289035044,\n" +
//                    "\t\t\"CABLE_ID\": 2261520\n" +
//                    "\t}, {\n" +
//                    "\t\t\"CABLE_NAME\": \"一干-宁汉1-路由段5（花旗CS南-虎踞路72芯）\",\n" +
//                    "\t\t\"AHOSTNAME\": \"花旗CS\",\n" +
//                    "\t\t\"ZGEOX\": 118.66545,\n" +
//                    "\t\t\"AREA_NAME\": \"江苏\",\n" +
//                    "\t\t\"ZGEOY\": 32.1722,\n" +
//                    "\t\t\"ZHOSTNAME\": \"花旗001066#\",\n" +
//                    "\t\t\"CABLE_TYPE\": \"一干光缆\",\n" +
//                    "\t\t\"AGEOY\": 32.16486,\n" +
//                    "\t\t\"FLAG\": \"N\",\n" +
//                    "\t\t\"AGEOX\": 118.6584,\n" +
//                    "\t\t\"CABLE_ID\": 63549\n" +
//                    "\t}]\n";
//            Observable.just(testData)
//                    .map(new Function<String, ListDataBean<GuangLanBean>>() {
//                        @Override
//                        public ListDataBean<GuangLanBean> apply(String s) throws Exception {
//                            Type type = new TypeToken<List<GuangLanBean>>() {
//                            }.getType();
//                            List<GuangLanBean> list = new Gson().fromJson(s, type);
//
//                            ListDataBean bean = new ListDataBean();
//                            bean.setList(list);
//                            bean.setTotal(103);
//                            return bean;
//                        }
//                    })
//                    .compose(LifeObservableTransformer.<ListDataBean<GuangLanBean>>create(this))
//                    .subscribe(new ErrorObserver<ListDataBean<GuangLanBean>>(this) {
//                        @Override
//                        public void onNext(ListDataBean<GuangLanBean> listDataBean) {
//                            addGuangLanMark(type, listDataBean.getList());
//                        }
//                    });
        }

    }


    private List<Marker> nowMarks = new ArrayList<>();
    private List<Polyline> polylines = new ArrayList<>();
    private List<ResBean> data = new ArrayList<>();

    private void addGuangLanMark(final int searchType, List<GuangLanBean> list) {
        cleanNowMark();

        this.nowType = searchType;
        aMap.setOnMarkerClickListener(null);

//        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker == locationMarker)
//                    return false;
//
//                int pos = Integer.parseInt(marker.getSnippet());
////                    QianXinListActivity.open(this,);
////                    EngineRoomDetailsActivity.open(ResourceActivity.this, data.get(pos));
//                return true;
//            }
//        });

        for (int i = 0; i < list.size(); i++) {
            GuangLanBean bean = list.get(i);

            if (!TextUtils.isEmpty(bean.getAGEOX()) && !TextUtils.isEmpty(bean.getAGEOY())) {
                LatLng aLatLng = new LatLng(Double.parseDouble(bean.getAGEOY())
                        , Double.parseDouble(bean.getAGEOX()));
                final Marker aMarker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_guanglan))
                        .position(aLatLng).title(bean.getCABLE_NAME()).snippet(i + ""));
                nowMarks.add(aMarker);

                if (!TextUtils.isEmpty(bean.getZGEOX()) && !TextUtils.isEmpty(bean.getZGEOY())) {
                    LatLng zLatLng = new LatLng(Double.parseDouble(bean.getZGEOY())
                            , Double.parseDouble(bean.getZGEOX()));
                    final Marker zMarker = aMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_guanglan))
                            .position(zLatLng).title(bean.getCABLE_NAME()).snippet(i + ""));
                    nowMarks.add(zMarker);

                    Polyline polyline = aMap.addPolyline(new PolylineOptions().add(aLatLng, zLatLng)
                            .width(5).color(Color.argb(255, 255, 0, 0)));
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

        data.clear();

    }


    private void addRoomMark(final int searchType, List<ResBean> list) {
        cleanNowMark();

        this.nowType = searchType;
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker == locationMarker)
                    return false;
                int pos = Integer.parseInt(marker.getSnippet());
                GuangLanDListActivity.open(ResourceActivity.this);
//                    EngineRoomDetailsActivity.open(ResourceActivity.this, data.get(pos));
                return true;
            }
        });

        for (int i = 0; i < list.size(); i++) {
            ResBean resBean = list.get(i);
            LatLng latLng = new LatLng(Double.parseDouble(resBean.getLatitude())
                    , Double.parseDouble(resBean.getLongitude()));
            final Marker marker = aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_room))
                    .position(latLng).title(resBean.getRoomName()).snippet(i + ""));
            nowMarks.add(marker);
        }
        this.data = list;

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

    private Marker locationMarker;
    void setLocationMark(LocationManager.LocationBean bean) {
        if (bean == null)
            return;

        if (location != null) {
            getResData(0, 2);
        }

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
        aMap.moveCamera(CameraUpdateFactory.zoomBy(4));
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
        aMap.moveCamera(CameraUpdateFactory.zoomBy(4));
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

//        todo 这里写死了 测试数据
//        bean.longitude = 118.695495;
//        bean.latitude = 32.154022;

        this.location = bean;
        setLocationMark(bean);
    }

    @Override
    public void onError(int code, String msg) {
        ToastUtils.showToast("定位失败:" + msg);
    }
}
