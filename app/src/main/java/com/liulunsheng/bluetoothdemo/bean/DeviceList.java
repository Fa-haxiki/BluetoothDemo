package com.liulunsheng.bluetoothdemo.bean;

/**
 * Created by Liu Lunsheng on 2017/10/18.
 * QQ:619639650
 */

public class DeviceList {

    public int MAX = 20;
    private int location_flag = 0;
    private ScanedBikeDevice[] mScanedBikeDevices = new ScanedBikeDevice[MAX];

    public int getLocation_flag() {
        return location_flag;
    }

    public void setLocation_flag(int location_flag) {
        this.location_flag = location_flag;
    }

    public ScanedBikeDevice[] getScanedBikeDevices() {
        return mScanedBikeDevices;
    }

    public void setScanedBikeDevices(ScanedBikeDevice[] scanedBikeDevices) {
        mScanedBikeDevices = scanedBikeDevices;
    }
}
