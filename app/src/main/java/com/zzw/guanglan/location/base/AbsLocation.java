package com.zzw.guanglan.location.base;

import android.content.Context;

import com.zzw.guanglan.location.LocationManager;

/**
 * Created by zzw on 2018/12/29.
 * 描述:
 */
public abstract  class AbsLocation implements ILocation {

    protected Context mContext;
    protected LocationManager.OnLocationListener mLocationListener;

    public AbsLocation(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public void setLocationListener(LocationManager.OnLocationListener mLocationListener){
        this.mLocationListener = mLocationListener;
    }





}
