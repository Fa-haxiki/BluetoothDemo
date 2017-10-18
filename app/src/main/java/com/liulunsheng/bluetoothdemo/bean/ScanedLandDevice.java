package com.liulunsheng.bluetoothdemo.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Liu Lunsheng on 2017/10/18.
 * QQ:619639650
 */

public class ScanedLandDevice {

    private BluetoothDevice mBluetoothDevice;
    private String mDeviceName;
    private byte[] mScanRecord;
    private int mRSSI;
    private String mLandId;

    public ScanedLandDevice(BluetoothDevice bluetoothDevice, String deviceName, byte[] scanRecord, int RSSI, String landId) {
        mBluetoothDevice = bluetoothDevice;
        mDeviceName = deviceName;
        mScanRecord = scanRecord;
        mRSSI = RSSI;
        mLandId = landId;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        mScanRecord = scanRecord;
    }

    public int getRSSI() {
        return mRSSI;
    }

    public void setRSSI(int RSSI) {
        mRSSI = RSSI;
    }

    public String getLandId() {
        return mLandId;
    }

    public void setLandId(String landId) {
        mLandId = landId;
    }
}
