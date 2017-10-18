package com.liulunsheng.bluetoothdemo.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Liu Lunsheng on 2017/10/16.
 * QQ:619639650
 */

public class ScanedBikeDevice {

    private String mDeviceName;
    private BluetoothDevice mBluetoothDevice;
    private byte[] mScanRecord;
    private int mRSSI;
    private String mBikeId;
    private int minPark; // 1:在停车点内0：不在停车点

    public ScanedBikeDevice(String deviceName, BluetoothDevice bluetoothDevice, byte[] scanRecord, int RSSI) {
        mDeviceName = deviceName;
        mBluetoothDevice = bluetoothDevice;
        mScanRecord = scanRecord;
        mRSSI = RSSI;
    }

    public ScanedBikeDevice(String deviceName, BluetoothDevice bluetoothDevice, byte[] scanRecord, int RSSI, String bikeId, int inPark) {
        mDeviceName = deviceName;
        mBluetoothDevice = bluetoothDevice;
        mScanRecord = scanRecord;
        mRSSI = RSSI;
        mBikeId = bikeId;
        minPark = inPark;
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

    public int getInPark() {
        return minPark;
    }

    public void setInPark(int inPark) {
       minPark = inPark;
    }

    public String getBikeId() {
        return mBikeId;
    }

    public void setBikeId(String bikeId) {
        mBikeId = bikeId;
    }
}
