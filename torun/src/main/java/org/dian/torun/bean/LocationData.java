package org.dian.torun.bean;

import com.baidu.location.BDLocation;

/**
 * Created by BaiYa on 2014/4/10.
 */
public class LocationData {

    /**时间*/
    private String time;
    /**纬度*/
    private double latitude;
    /**经度*/
    private double lontitude;
    /**定位精度*/
    private float radius;

    /**位置信息*/
    private String addr;
    /**运营商*/
    //private int operationers;


    public LocationData(BDLocation location) {
        setTime(location.getTime());
        setLatitude(location.getLatitude());
        setLontitude(location.getLongitude());
        setRadius(location.getRadius());
        setAddr(location.getAddrStr());
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public void setLontitude(double lontitude) {
        this.lontitude = lontitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "time='" + time + '\'' +
                ", latitude=" + latitude +
                ", lontitude=" + lontitude +
                ", radius=" + radius +
                ", addr='" + addr + '\'' +
                '}';
    }
}
