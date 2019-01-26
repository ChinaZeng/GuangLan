package com.zzw.guanglan.location;

import android.content.Context;

import com.zzw.guanglan.location.base.AbsLocation;
import com.zzw.guanglan.location.base.ILocation;

import java.io.Serializable;

/**
 * Created by zzw on 2018/10/14.
 * 描述:
 */
public class LocationManager implements ILocation {

    private AbsLocation absLocation;

    public LocationManager(Context context, OnLocationListener locationListener) {
        absLocation = new GaoDeLocation(context);
        absLocation.setLocationListener(locationListener);
    }

    @Override
    public void start() {
        absLocation.start();
    }

    @Override
    public void stop() {
        absLocation.stop();
    }

    public interface OnLocationListener {
        void onSuccess(LocationBean bean);
        void onError(int code, String msg);
    }

    public static class LocationBean implements Serializable {
        //纬度
        public double latitude;
        //经度
        public double longitude;
        //地址
        public String addrss;
        //国家
        public String country;
        //省
        public String province;
        //城市
        public String city;
        //城区
        public String district;
        //街道
        public String street;
        //街道门牌号信息
        public String streetNum;
        //城市编码
        public String cityCode;
        //地区编码
        public String adCode;
        //定位时间
        public long time;

        //位置描述信息
        public String locationDescribe;

        @Override
        public String toString() {
            return "LocationBean{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", addrss='" + addrss + '\'' +
                    ", country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", district='" + district + '\'' +
                    ", street='" + street + '\'' +
                    ", streetNum='" + streetNum + '\'' +
                    ", cityCode='" + cityCode + '\'' +
                    ", adCode='" + adCode + '\'' +
                    ", time=" + time +
                    ", locationDescribe='" + locationDescribe + '\'' +
                    '}';
        }
    }
}
