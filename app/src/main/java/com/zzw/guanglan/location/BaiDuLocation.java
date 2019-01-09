package com.zzw.guanglan.location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.zzw.guanglan.location.base.AbsLocation;

/**
 * Created by zzw on 2018/12/29.
 * 描述:
 */
public class BaiDuLocation extends AbsLocation {
    public LocationClient mLocationClient = null;

    public BaiDuLocation(Context context) {
        super(context);
    }

    @Override
    public void start() {

        if (mLocationClient == null) {
            //声明LocationClient类
            mLocationClient = new LocationClient(mContext);
            //注册监听函数
            mLocationClient.registerLocationListener(new MyLocationListener());
        }

        LocationClientOption option = new LocationClientOption();
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
//        option.setCoorType("bd09ll");

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(0);

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的位置信息，此处必须为true
        option.setIsNeedLocationDescribe(true);

        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);

        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(true);

        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);

        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);

        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);

        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setEnableSimulateGps(false);

        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    @Override
    public void stop() {
        //停止定位后，本地定位服务并不会被销毁
        mLocationClient.stop();
        mLocationClient = null;
    }

    public class MyLocationListener extends BDAbstractLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                int locType = location.getLocType();
                if (locType == 61 || locType == 161) {
                    //可在其中解析amapLocation获取相应内容。
                    if (mLocationListener != null) {
                        mLocationListener.onSuccess(BDLocation2LocationBean(location));
                    }
                } else {
                    //http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + locType);
                    if (mLocationListener != null) {
                        mLocationListener.onError(locType, "定位失败，请确认相关权限，网络或者GPS是否正常");
                    }
                }
            } else {

                //http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location is null");
                if (mLocationListener != null) {
                    mLocationListener.onError(-1, "定位失败，请确认相关权限，网络或者GPS是否正常");
                }
            }
        }
    }

    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private LatLng gps2baiduLocation(double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        LatLng latLng = new LatLng(latitude, longitude);
        converter.coord(latLng);
        return converter.convert();
    }


    private LocationManager.LocationBean BDLocation2LocationBean(BDLocation location) {
        LocationManager.LocationBean bean = new LocationManager.LocationBean();

        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明


        //获取纬度信息
        double latitude = location.getLatitude();
        bean.latitude = latitude;

        //获取经度信息
        double longitude = location.getLongitude();
        bean.longitude = longitude;
//        //获取定位精度，默认值为0.0f
//        float radius = location.getRadius();
//        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//        String coorType = location.getCoorType();
//        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//        int errorCode = location.getLocType();

        String addr = location.getAddrStr();    //获取详细地址信息
        bean.addrss = addr;
        String country = location.getCountry();    //获取国家
        bean.country = country;
        String province = location.getProvince();    //获取省份
        bean.province = province;

        String city = location.getCity();    //获取城市
        bean.city = city;

        String district = location.getDistrict();    //获取区县
        bean.district = district;


        String street = location.getStreet();    //获取街道信息
        bean.street = street;

        String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
        bean.locationDescribe = locationDescribe;

        return bean;
    }
}
