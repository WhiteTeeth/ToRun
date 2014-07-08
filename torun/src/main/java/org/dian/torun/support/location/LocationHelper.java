package org.dian.torun.support.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.dian.torun.bean.LocationData;

/**
 * Created by BaiYa on 2014/4/11.
 */
public class LocationHelper {

    private LocationClient mLocationClient;
    private MLocationListener mLocationListener;
    private LocationClientOption mLocationOption;


    public LocationHelper(Context context) {
        mLocationListener = new MLocationListener() {
            @Override
            public void onReceiveLocation(LocationData locationData) {

            }
        };
        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationClient.registerLocationListener(new LocationListener());
        setLocOption();
    }

    //设置定位参数
    private void setLocOption() {
        mLocationOption = new LocationClientOption();
        mLocationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位精度
        mLocationOption.setCoorType("bd09ll");  //地理编码
        mLocationOption.setScanSpan(5000 * 60); //定位时间间隔
        mLocationOption.setIsNeedAddress(true); //是否返回位置信息
        mLocationOption.setNeedDeviceDirect(true); //是否返回手机方向
        mLocationClient.setLocOption(mLocationOption);
    }

    /**启动定位服务*/
    public void startLocation() {
        mLocationClient.start();
    }
    /**结束定位服务*/
    public void stopLocation() {
        mLocationClient.stop();
    }

    /**设置定位监听器*/
    public void setLocationListener(MLocationListener listener) {
        if (listener == null) throw new NullPointerException("MLocationListener is null!");
        mLocationListener = listener;
    }

    /**发起定位请求*/
    public void requestLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            android.util.Log.d("ToRun", "locClient is null or not started");
        }
    }

    /**发起离线定位请求*/
    public void requestOfflineLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestOfflineLocation();
        } else {
            android.util.Log.d("ToRun", "locClient is null or not started");
        }
    }

    public interface MLocationListener {
        public void onReceiveLocation(LocationData locationData);
    }

    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLocationListener.onReceiveLocation(new LocationData(bdLocation));
        }

        @Override
        public void onReceivePoi(BDLocation bdLocation) {

        }
    }

}
