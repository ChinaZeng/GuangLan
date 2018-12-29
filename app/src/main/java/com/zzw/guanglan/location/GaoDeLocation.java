package com.zzw.guanglan.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zzw.guanglan.location.base.AbsLocation;

/**
 * Created by zzw on 2018/12/29.
 * 描述:
 */
public class GaoDeLocation extends AbsLocation implements AMapLocationListener{
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;

    public GaoDeLocation(Context context) {
       super(context);
    }

    @Override
    public void start() {

        if(mLocationClient == null){
            //初始化定位
            mLocationClient = new AMapLocationClient(mContext);
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
        }

        ////初始化AMapLocationClientOption对象
        AMapLocationClientOption option = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置setOnceLocationLatest(boolean b)接口为true，
        // 启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//        option.setOnceLocationLatest(true);
        ////获取一次定位结果
        option.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        option.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        option.setHttpTimeOut(10000);
        //关闭缓存机制,默认开启
//        option.setLocationCacheEnable(false);
        mLocationClient.setLocationOption(option);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.startLocation();
    }

    @Override
    public void stop() {
        //停止定位后，本地定位服务并不会被销毁
        mLocationClient.stopLocation();
        //销毁定位客户端，同时销毁本地定位服务。
        mLocationClient.onDestroy();
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                if (mLocationListener != null) {
                    mLocationListener.onSuccess(AMapLocation2LocationBean(amapLocation));
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                if (mLocationListener != null) {
                    mLocationListener.onError(amapLocation.getErrorCode(), amapLocation.getErrorInfo());
                }
            }
        }
    }
    private LocationManager.LocationBean AMapLocation2LocationBean(AMapLocation amapLocation) {
        LocationManager.LocationBean locationBean = new LocationManager.LocationBean();
        locationBean.latitude = amapLocation.getLatitude();
        locationBean.longitude = amapLocation.getLongitude();
        locationBean.addrss = amapLocation.getAddress();
        locationBean.country = amapLocation.getCountry();
        locationBean.province = amapLocation.getProvince();
        locationBean.city = amapLocation.getCity();
        locationBean.district = amapLocation.getDistrict();
        locationBean.street = amapLocation.getStreet();
        locationBean.streetNum = amapLocation.getStreetNum();
        locationBean.cityCode = amapLocation.getCityCode();
        locationBean.adCode = amapLocation.getAdCode();
        locationBean.time = amapLocation.getTime();
        return locationBean;
    }
}
