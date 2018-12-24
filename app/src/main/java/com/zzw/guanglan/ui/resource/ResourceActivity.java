package com.zzw.guanglan.ui.resource;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.utils.PopWindowUtils;
import com.zzw.guanglan.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by zzw on 2018/12/7
 */
public class ResourceActivity extends BaseActivity {
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


    @OnClick({R.id.tv_res_look, R.id.tv_my_gd, R.id.tv_add, R.id.tv_hot_conn})
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
                            ToastUtils.showToast("0");
                        } else {
                            ToastUtils.showToast("1");
                        }
                    }
                });

                break;
            case R.id.tv_hot_conn:
                HotConnActivity.open(this);
                break;
        }
    }


    private LocationManager.LocationBean location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    LocationManager.LocationBean bean = new LocationManager.LocationBean();
                    bean.latitude = location.getLatitude();
                    bean.longitude = location.getLongitude();
                    ResourceActivity.this.location = bean;
                }
            });
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

    private void showMySelf() {
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.interval(2000);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
//        设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        //改变地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomBy(6));
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
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
}
